#!/bin/sh
# Renders the in-guest LiDroid guide from guide.conf into a static HTML site:
# index.html, one <slug>.html per page, and style.css. Output is staged into
# the rootfs Docker context (build-rootfs/docs-guide/) by build_rootfs() in
# build-all.sh, then COPYed into the squashfs and served in-guest by
# lidroid-docs.
#
# Usage: build-guide.sh <guide.conf> <output-dir>
#
# POSIX sh + awk only: no network, no build tools, runs anywhere build-all.sh
# runs (including mawk, the Ubuntu default). The format it parses is documented
# at the top of guide.conf.
set -eu

[ $# -eq 2 ] || { echo "usage: $0 <guide.conf> <output-dir>" >&2; exit 1; }
CONF=$1
OUT=$2
[ -f "$CONF" ] || { echo "$0: config not found: $CONF" >&2; exit 1; }
mkdir -p "$OUT"

# ── Stylesheet: dark, matched to the app's palette (LiDroidBackground #0d0c14,
#    surfaceContainerLow #17161f, primary violet). Served alongside the pages. ──
cat > "$OUT/style.css" <<'CSS'
:root { color-scheme: dark; }
* { box-sizing: border-box; }
html { -webkit-text-size-adjust: 100%; }
body {
  margin: 0;
  background: #0d0c14;
  color: #e2e0ea;
  font: 16px/1.65 "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}
header {
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #26252f;
}
header .brand {
  font-size: 1.25rem;
  font-weight: 700;
  color: #f5f3fb;
  text-decoration: none;
  letter-spacing: 0.01em;
}
header .tag { color: #8d8a9c; font-size: 0.85rem; margin-left: 0.75rem; }
main { max-width: 44rem; margin: 0 auto; padding: 1.25rem 1.25rem 2rem; }
h1 { font-size: 1.9rem; line-height: 1.2; margin: 1.25rem 0 1rem; color: #f5f3fb; }
h2 { font-size: 1.3rem; margin: 2rem 0 0.5rem; color: #f5f3fb; }
h3 { font-size: 1.05rem; margin: 1.5rem 0 0.5rem; }
p, li { overflow-wrap: break-word; }
a { color: #b9a7fc; text-decoration: none; }
a:hover { text-decoration: underline; }
ul { padding-left: 1.4rem; }
li { margin: 0.3rem 0; }
pre {
  background: #17161f;
  border: 1px solid #26252f;
  border-radius: 8px;
  padding: 0.85rem 1rem;
  overflow-x: auto;
}
pre code { font: 0.85rem/1.5 ui-monospace, "Cascadia Code", Menlo, Consolas, monospace; color: #cfdce2; }
code { font: 0.9em ui-monospace, "Cascadia Code", Menlo, Consolas, monospace; }
section.toc {
  background: #17161f;
  border: 1px solid #26252f;
  border-radius: 12px;
  padding: 0.25rem 1.25rem 0.75rem;
  margin: 1.5rem 0;
}
footer {
  max-width: 44rem;
  margin: 0 auto;
  padding: 1rem 1.25rem 2.5rem;
  border-top: 1px solid #26252f;
  color: #8d8a9c;
  font-size: 0.85rem;
}
CSS

# ── Renderer ──────────────────────────────────────────────────────────────────
awk -v out="$OUT" '
    # ── Pass 1: collect pages (order) and titles for cross-links ──
    FNR == NR {
        if ($0 ~ /^online:/) online = substr($0, 9)
        else if ($0 ~ /^page:/) {
            rest = substr($0, 6)
            pos = index(rest, "|")
            slug = substr(rest, 1, pos - 1)
            titles[slug] = substr(rest, pos + 1)
            pages[++npages] = slug
        }
        next
    }

    # ── Helpers (extra params = locals, so recursion/state cannot leak) ──
    function esc(s,   r) {
        r = s
        gsub(/&/, "\\&amp;", r)
        gsub(/</, "\\&lt;", r)
        gsub(/>/, "\\&gt;", r)
        gsub(/"/, "\\&quot;", r)
        return r
    }

    # Inline [[slug]] / [[slug|text]] → anchors. Runs AFTER esc(), so the text
    # is already entity-safe and only hrefs/labels are touched here.
    # Scanned with index() rather than a regex: mawk ERE mishandles the
    # POSIX "unmatched ] first in a bracket expression" idiom ([^]]), which
    # would swallow the closing bracket into the target.
    function link(s,   result, start, p, inner, q, target, label, href) {
        result = ""
        while ((start = index(s, "[[")) > 0) {
            result = result substr(s, 1, start - 1)
            s = substr(s, start + 2)
            p = index(s, "]]")
            if (p == 0) { result = result "[["; break }
            inner = substr(s, 1, p - 1)
            s = substr(s, p + 2)
            if ((q = index(inner, "|")) > 0) {
                target = substr(inner, 1, q - 1)
                label = substr(inner, q + 1)
            } else {
                target = inner
                label = inner
            }
            href = (target ~ /:\/\//) ? target : target ".html"
            result = result "<a href=\"" href "\">" label "</a>"
        }
        return result s
    }

    function flushpara(   i, joined) {
        if (curfile == "" || np == 0) return
        joined = para[1]
        for (i = 2; i <= np; i++) joined = joined " " para[i]
        print "<p>" joined "</p>" > curfile
        np = 0
        for (i in para) delete para[i]
    }

    function closeblocks() {
        if (openul) { print "</ul>" > curfile; openul = 0 }
        flushpara()
    }

    function emit_header(s, t) {
        if (curfile == "") return
        print "<!doctype html>" > curfile
        print "<html lang=\"en\">" > curfile
        print "<head><meta charset=\"utf-8\">" > curfile
        print "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" > curfile
        print "<title>" esc(t) ((s == "index") ? "" : " · LiDroid Guide") "</title>" > curfile
        print "<link rel=\"stylesheet\" href=\"style.css\"></head>" > curfile
        print "<body><header><a class=\"brand\" href=\"index.html\">LiDroid</a><span class=\"tag\">linux on android</span></header>" > curfile
        print "<main><h1>" esc(t) "</h1>" > curfile
    }

    function emit_toc(   i) {
        if (curfile == "") return
        print "<section class=\"toc\"><h2>Contents</h2><ul>" > curfile
        for (i = 2; i <= npages; i++)
            print "<li><a href=\"" pages[i] ".html\">" esc(titles[pages[i]]) "</a></li>" > curfile
        print "</ul></section>" > curfile
    }

    function emit_footer(s,   i, idx, prev, nxt, nav) {
        if (curfile == "") return
        idx = 0
        for (i = 1; i <= npages; i++) if (pages[i] == s) { idx = i; break }
        prev = (idx > 1) ? pages[idx - 1] : ""
        nxt = (idx < npages) ? pages[idx + 1] : ""
        nav = "</main><footer><p>"
        if (prev != "") nav = nav "<a href=\"" prev ".html\">&#8592; " esc(titles[prev]) "</a> &#183; "
        nav = nav "<a href=\"index.html\">Index</a>"
        if (nxt != "") nav = nav " &#183; <a href=\"" nxt ".html\">" esc(titles[nxt]) " &#8594;</a>"
        nav = nav "</p>"
        if (online != "") nav = nav "<p>Full documentation online: <a href=\"" online "\">" online "</a></p>"
        print nav "</footer></body></html>" > curfile
    }

    # ── Pass 2: render ──
    {
        line = $0

        # Inside a fenced code block everything is emitted verbatim (escaped),
        # including lines that look like config comments.
        if (incode) {
            if (line ~ /^```$/) { incode = 0; print "</code></pre>" > curfile }
            else print esc(line) > curfile
            next
        }

        # Config comments: single hash + space/EOL. Body headings are ## / ###,
        # so they never collide with this.
        if (line ~ /^#($| )/) next

        if (line ~ /^online:/) { online = substr(line, 9); next }

        if (line ~ /^page:/) {
            if (curfile != "") {
                closeblocks()
                if (slug == "index") emit_toc()
                emit_footer(slug)
                close(curfile)
            }
            rest = substr(line, 6)
            pos = index(rest, "|")
            slug = substr(rest, 1, pos - 1)
            curfile = out "/" slug ".html"
            emit_header(slug, substr(rest, pos + 1))
            next
        }

        if (line ~ /^```.*/) { closeblocks(); incode = 1; print "<pre><code>" > curfile; next }

        if (line ~ /^## /)  { closeblocks(); print "<h2>" link(esc(substr(line, 4))) "</h2>" > curfile; next }
        if (line ~ /^### /) { closeblocks(); print "<h3>" link(esc(substr(line, 5))) "</h3>" > curfile; next }

        if (line ~ /^- /) {
            flushpara()
            if (!openul) { print "<ul>" > curfile; openul = 1 }
            print "<li>" link(esc(substr(line, 3))) "</li>" > curfile
            next
        }

        if (line ~ /^$/) { closeblocks(); next }

        if (openul) { print "</ul>" > curfile; openul = 0 }
        para[++np] = link(esc(line))
        next
    }

    END {
        if (curfile != "") {
            closeblocks()
            if (slug == "index") emit_toc()
            emit_footer(slug)
            close(curfile)
        }
    }
' "$CONF" "$CONF"

echo "build-guide.sh: rendered $(find "$OUT" -name '*.html' | wc -l) pages + style.css into $OUT"
