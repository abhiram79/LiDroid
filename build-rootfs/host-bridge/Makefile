# build-rootfs/host-bridge/Makefile
# musl-static aarch64 build. Invoked from Dockerfile.rootfs's vsock-builder stage.
CC      ?= gcc
CFLAGS  ?= -O2 -Wall -Wextra -Wno-unused-parameter -static -D_GNU_SOURCE \
           -fstack-protector-strong -D_FORTIFY_SOURCE=2
LDFLAGS ?=

BIN = lidroid-hostd

all: $(BIN)

$(BIN): lidroid-hostd.c
	$(CC) $(CFLAGS) -o $@ $< $(LDFLAGS)

clean:
	rm -f $(BIN)

.PHONY: all clean
