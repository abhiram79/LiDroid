#!/bin/sh
set -eu
ROOTFS=/work/rootfs

# DEBIAN_VERSION comes from the Dockerfile ENV (suite like trixie). It drives
# the APT sources (suite plus its "-updates"/"-security" pockets) below.
: "${DEBIAN_VERSION:?DEBIAN_VERSION must be set (e.g. trixie)}"

mkdir -p "$ROOTFS/etc/apt"
cat > "$ROOTFS/etc/apt/sources.list" <<EOF
deb http://deb.debian.org/debian ${DEBIAN_VERSION} main contrib non-free non-free-firmware
deb http://deb.debian.org/debian ${DEBIAN_VERSION}-updates main contrib non-free non-free-firmware
deb http://security.debian.org/debian-security ${DEBIAN_VERSION}-security main contrib non-free non-free-firmware
EOF

# Give the build-time chroot working DNS (apt needs it). The DNS shipped to
# the guest is fixed further down (10.0.2.3 = LiDroid's slirp resolver).
cp /etc/resolv.conf "$ROOTFS/etc/resolv.conf"

# Stop packages' postinst from starting services via invoke-rc.d: there is no
# init running inside the chroot, so a stock policy file makes invoke-rc.d
# skip them (dpkg policy recommends exactly this for image builds).
printf '#!/bin/sh\nexit 101\n' > "$ROOTFS/usr/sbin/policy-rc.d"
chmod +x "$ROOTFS/usr/sbin/policy-rc.d"

# Install the guest package set. dpkg/apt work fine in the chroot even with
# no CAP_SYS_ADMIN (nothing needs a mounted /proc); a containerized postinst
# can still fail against a non-running system, so don't lose the whole
# squashfs build over it. The binaries are arm64 and the builder stage is
# pinned to linux/arm64, so everything here executes natively.
DEBIAN_FRONTEND=noninteractive chroot "$ROOTFS" apt-get update || true
DEBIAN_FRONTEND=noninteractive chroot "$ROOTFS" apt-get -y install \
    --no-install-recommends \
    apt debian-archive-keyring bash \
    openrc busybox \
    curl ca-certificates \
    opendoas sudo passwd adduser uidmap \
    podman crun fuse-overlayfs passt slirp4netns netavark aardvark-dns \
    docker.io docker-compose \
    lxc lxc-templates \
    iptables nftables bridge-utils iproute2 \
    procps kmod \
    dropbear openssh-sftp-server \
    gzip xz-utils \
    tigervnc-standalone-server \
    pulseaudio pulseaudio-utils \
    fonts-dejavu-core xfonts-base \
    isc-dhcp-client \
    dnsmasq-base \
    lsb-base \
    || true

# The policy file must not leak into the guest: package installs inside the
# VM should still be able to restart their services.
rm -f "$ROOTFS/usr/sbin/policy-rc.d"

# Apply file capabilities to newuidmap/newgidmap (rootless podman uid
# mapping). Debian ships them setuid-root too, but keeping the explicit
# caps means the squashfs carries the security.capability xattr — preserved
# by mksquashfs without -no-xattrs — like the cap-based schemes expect.
if command -v setcap >/dev/null 2>&1; then
    setcap cap_setuid+ep "$ROOTFS/usr/bin/newuidmap"
    setcap cap_setgid+ep "$ROOTFS/usr/bin/newgidmap"
fi

# Ensure doas and sudo are setuid-root. Debian ships them that way, but on
# overlay-mounted build hosts chmods can silently fail — reapply.
chmod u+s "$ROOTFS/usr/bin/doas"  2>/dev/null || true
chmod u+s "$ROOTFS/usr/bin/sudo"  2>/dev/null || true

# `wheel` is the BSD/Alpine convention for admin users and Debian doesn't
# create it upstream (it ships `sudo` instead). Neither the doas nor the
# sudo allow-rule below would match anything without it, so add it pinned
# to GID 101 — the first free system GID on Debian (users=100), and well
# clear of the 1000+ range adduser allocates regular users/groups from.
if ! grep -q '^wheel:' "$ROOTFS/etc/group"; then
    echo 'wheel:x:101:' >> "$ROOTFS/etc/group"
fi

# doas: members of the `wheel` group can become root after entering their
# password (cached for ~5 min). Standard *BSD/wheel convention.
mkdir -p "$ROOTFS/etc/doas.d"
echo "permit persist :wheel" > "$ROOTFS/etc/doas.d/doas.conf"
chmod 0400 "$ROOTFS/etc/doas.d/doas.conf"

# sudo: equivalent rule for users who prefer sudo over doas.
mkdir -p "$ROOTFS/etc/sudoers.d"
echo "%wheel ALL=(ALL) ALL" > "$ROOTFS/etc/sudoers.d/wheel"
chmod 0440 "$ROOTFS/etc/sudoers.d/wheel"

# Set root password to "lidroid" (pre-hashed with openssl).
# /etc/shadow is edited offline instead of running chpasswd inside the
# chroot — no PAM dependencies, and it works regardless of how the emulated
# build environment is set up.
# No fixed -salt: openssl generates a random salt so the stored hash differs
# per build (the password stays the documented default "lidroid").
ROOT_HASH=$(openssl passwd -6 lidroid)
sed -i "s|^root:[^:]*:|root:${ROOT_HASH}:|" "$ROOTFS/etc/shadow"

# Strip docs/man/locale to shrink squashfs
rm -rf "$ROOTFS/usr/share/man" "$ROOTFS/usr/share/doc" \
       "$ROOTFS/usr/share/locale" "$ROOTFS/usr/share/info"

# Debian's pulseaudio package ships a SysV init script. LiDroid starts
# pulseaudio directly from lidroid-x11 (start-stop-daemon), never as a
# service; a stock script left behind would show up as an unmanaged service
# in /etc/init.d and confuse OpenRC's boot ordering (it also expects udev).
rm -f "$ROOTFS/etc/init.d/pulseaudio"

# Pre-create podman storage dirs (saves first-boot mkdir)
mkdir -p "$ROOTFS/var/lib/containers/storage" \
         "$ROOTFS/run/containers/storage" \
         "$ROOTFS/run/libpod" \
         "$ROOTFS/run/crun"

# Copy custom service files into the rootfs
cp /work/files/etc/init.d/lidroid-bootstrap "$ROOTFS/etc/init.d/"
cp /work/files/etc/init.d/lidroid-network   "$ROOTFS/etc/init.d/"
cp /work/files/etc/init.d/lidroid-resize    "$ROOTFS/etc/init.d/"
cp /work/files/etc/init.d/lidroid-ready     "$ROOTFS/etc/init.d/"
cp /work/files/etc/init.d/lidroid-x11       "$ROOTFS/etc/init.d/"
cp /work/files/etc/init.d/lidroid-vsock     "$ROOTFS/etc/init.d/"
cp /work/files/etc/init.d/lidroid-hostd     "$ROOTFS/etc/init.d/"
cp /work/files/etc/init.d/lidroid-downloads "$ROOTFS/etc/init.d/"
cp /work/files/etc/init.d/lidroid-migrate   "$ROOTFS/etc/init.d/"
# localmount: Debian's openrc package does not ship this boot service, but
# the boot runlevel needs it to mount /proc, /sys and the persist dir.
cp /work/files/etc/init.d/localmount        "$ROOTFS/etc/init.d/"
chmod +x "$ROOTFS/etc/init.d/lidroid-"* "$ROOTFS/etc/init.d/localmount"

# busybox udhcpc hook script (used by lidroid-network; busybox looks for it
# at this path when no -s option is given).
mkdir -p "$ROOTFS/usr/share/udhcpc"
cp /work/files/usr/share/udhcpc/default.script "$ROOTFS/usr/share/udhcpc/"
chmod 0755 "$ROOTFS/usr/share/udhcpc/default.script"

# Copy /usr/local/bin scripts (resize daemon + login wrapper + getty selector)
mkdir -p "$ROOTFS/usr/local/bin"
cp /work/files/usr/local/bin/lidroid-resize "$ROOTFS/usr/local/bin/"
cp /work/files/usr/local/bin/lidroid-login  "$ROOTFS/usr/local/bin/"
cp /work/files/usr/local/bin/lidroid-getty  "$ROOTFS/usr/local/bin/"
cp /work/files/usr/local/bin/lidroid-backup "$ROOTFS/usr/local/bin/"
cp /work/files/usr/local/bin/lidroid-docs   "$ROOTFS/usr/local/bin/"
# lidroid-vsock-agent is COPY'd in from the vsock-builder Docker stage. Make
# sure it's executable (cross-arch COPY can lose the mode bit on some buildkit
# versions).
chmod +x "$ROOTFS/usr/local/bin/lidroid-vsock-agent" 2>/dev/null || true
# lidroid-hostd is also COPY'd from the vsock-builder stage; same mode-bit guard.
# The CLIs are argv[0]-dispatch symlinks onto the one multi-call binary.
chmod +x "$ROOTFS/usr/local/bin/lidroid-hostd" 2>/dev/null || true
# lidroid-overlay-normalize is COPY'd from the vsock-builder stage; mode-bit guard.
chmod +x "$ROOTFS/usr/local/bin/lidroid-overlay-normalize" 2>/dev/null || true
ln -sf lidroid-hostd "$ROOTFS/usr/local/bin/lidroid-notify"
ln -sf lidroid-hostd "$ROOTFS/usr/local/bin/lidroid-forward"
ln -sf lidroid-hostd "$ROOTFS/usr/local/bin/lidroid-open"
ln -sf lidroid-hostd "$ROOTFS/usr/local/bin/lidroid-power"
ln -sf lidroid-hostd "$ROOTFS/usr/local/bin/lidroid-headless"
ln -sf lidroid-hostd "$ROOTFS/usr/local/bin/lidroid-server"
ln -sf lidroid-hostd "$ROOTFS/usr/local/bin/lidroid-phone-ip"
chmod +x "$ROOTFS/usr/local/bin/lidroid-"*
# vsock agent's initial forward table (read at lidroid-vsock startup).
mkdir -p "$ROOTFS/etc/lidroid"
cp /work/files/etc/lidroid/forwards.conf "$ROOTFS/etc/lidroid/forwards.conf"
chmod 0644 "$ROOTFS/etc/lidroid/forwards.conf"
# Migration scripts dir (seeded with its README; per-version <v>.sh added over time).
mkdir -p "$ROOTFS/etc/lidroid/migrations"
cp /work/files/etc/lidroid/migrations/README "$ROOTFS/etc/lidroid/migrations/README"
# Install every migration script so a new one needs no build-script edit.
for f in /work/files/etc/lidroid/migrations/*.sh; do
    [ -f "$f" ] || continue
    cp "$f" "$ROOTFS/etc/lidroid/migrations/"
    chmod 0755 "$ROOTFS/etc/lidroid/migrations/$(basename "$f")"
done
# System-version stamp: the migration anchor. Baked from the app versionCode at
# build time; compared against /mnt/persist/.lidroid/applied-version at boot.
printf '%s\n' "${SYSTEM_VERSION:-0}" > "$ROOTFS/etc/lidroid/system-version"
chmod 0644 "$ROOTFS/etc/lidroid/system-version"
cp /work/files/etc/inittab "$ROOTFS/etc/inittab"
cp /work/files/etc/rc.conf "$ROOTFS/etc/rc.conf"

# /etc/profile.d/*.sh — sourced by Debian's /etc/profile in login shells.
# lidroid-color.sh: COLORTERM=truecolor (24-bit color). lidroid-x11.sh:
# DISPLAY / PULSE_SERVER for the in-app GUI viewer. Copy by explicit name so
# a renamed/removed asset fails the build (set -e) instead of silently
# shipping a squashfs without these exports.
mkdir -p "$ROOTFS/etc/profile.d"
cp /work/files/etc/profile.d/lidroid-color.sh "$ROOTFS/etc/profile.d/"
cp /work/files/etc/profile.d/lidroid-x11.sh   "$ROOTFS/etc/profile.d/"
chmod 0644 "$ROOTFS/etc/profile.d/lidroid-color.sh" "$ROOTFS/etc/profile.d/lidroid-x11.sh"

# /etc/containers/storage.conf — pin Podman to the in-kernel overlay driver.
# Without this file, Podman auto-detects fuse-overlayfs (installed alongside
# podman in this rootfs) and uses it, which is slower than native overlay.
mkdir -p "$ROOTFS/etc/containers"
cp /work/files/etc/containers/storage.conf "$ROOTFS/etc/containers/storage.conf"
chmod 0644 "$ROOTFS/etc/containers/storage.conf"

# Offline copy of the docs/guide site, staged into /work/docs-guide by
# build_rootfs() in build-all.sh (this must run after the "Strip docs" rm -rf
# above, or mksquashfs would ship nothing here). Served in-guest by
# lidroid-docs.
mkdir -p "$ROOTFS/usr/share/doc/lidroid"
cp /work/docs-guide/*.html /work/docs-guide/style.css "$ROOTFS/usr/share/doc/lidroid/"
chmod 0644 "$ROOTFS/usr/share/doc/lidroid/"*

# Hostname (read by lidroid-bootstrap via `hostname -F /etc/hostname`)
echo "lidroid" > "$ROOTFS/etc/hostname"
echo "127.0.0.1 localhost lidroid" > "$ROOTFS/etc/hosts"
echo "::1 localhost ip6-localhost" >> "$ROOTFS/etc/hosts"

# Static default resolver config for first boot: 10.0.2.3 is the slirp DNS
# the guest sees (matches the qemu command line); the public fallbacks follow.
# lidroid-network rewrites this with the DHCP-provided DNS once the network
# is actually up. (The copy installed above was only for the build-time apt.)
cat > "$ROOTFS/etc/resolv.conf" <<'EOF'
nameserver 10.0.2.3
nameserver 8.8.8.8
nameserver 1.1.1.1
EOF

# Login banner shown by getty before the login prompt.
# Static release string (no \S escape — its behavior varies between getty
# variants, and hardcoding the suite can never go stale).
cat > "$ROOTFS/etc/issue" <<'EOF'
Welcome to LiDroid (Debian GNU/Linux 13 (trixie))
Kernel \r on \m (\l)

  Default login:  root  /  lidroid
  Change root password:    passwd
  Create a regular user:   adduser <name>
                           adduser <name> wheel
                           (wheel group → can run doas/sudo)

EOF

# PID 1: Debian's busybox binary carries the init applet the inittab expects,
# but ships no applet symlinks — so point /sbin/init straight at the binary
# (merged-usr rootfs: /bin is a symlink to usr/bin, it still resolves).
ln -sf /bin/busybox "$ROOTFS/sbin/init"

# Explicitly enable the lxc-net default bridge (lxcbr0, 10.0.3.1/24, served
# by dnsmasq). It replaces Alpine's old per-container dnsmasq.lxcbr0 service.
if [ -f "$ROOTFS/etc/default/lxc" ]; then
    sed -i 's|^USE_LXC_BRIDGE=.*|USE_LXC_BRIDGE="true"|' "$ROOTFS/etc/default/lxc"
fi

# Set runlevels via direct symlinks (rc-update is just `ln -s
# /etc/init.d/X /etc/runlevels/<level>/X` under the hood).
mkdir -p "$ROOTFS/etc/runlevels/default" "$ROOTFS/etc/runlevels/boot"
# The boot runlevel needs localmount (mount local filesystems: /proc, /sys,
# /var, the persist dir); Debian's openrc package doesn't ship it, ours does.
if [ -e "$ROOTFS/etc/init.d/localmount" ]; then
    ln -sf /etc/init.d/localmount "$ROOTFS/etc/runlevels/boot/localmount"
fi
# Guard each link: a dangling symlink makes OpenRC log an error every boot
# and stalls lidroid-ready's `after *` on a phantom.
for svc in lidroid-migrate lidroid-bootstrap lidroid-network lidroid-resize dropbear docker lxc lxc-net lidroid-x11 lidroid-vsock lidroid-downloads lidroid-hostd lidroid-ready; do
    if [ -e "$ROOTFS/etc/init.d/$svc" ]; then
        ln -sf "/etc/init.d/$svc" "$ROOTFS/etc/runlevels/default/$svc"
    else
        echo "WARN: init script /etc/init.d/$svc missing, skipping runlevel symlink"
    fi
done

# Disable services we don't need (initramfs already handles them, or they're noise in the VM)
for svc in hwclock swclock urandom networking sysctl bootmisc syslog; do
    rm -f "$ROOTFS/etc/runlevels/boot/$svc" "$ROOTFS/etc/runlevels/default/$svc"
done
