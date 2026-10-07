#!/usr/bin/env bash
#
# Checks scripts/bump-version.sh against a throw-away pom. Nothing in your real
# pom.xml is touched. Run with:   bash scripts/check-bump.sh
# Provided; do not change.

cd "$(dirname "$0")/.." || exit 2
SCRIPT="$PWD/scripts/bump-version.sh"
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

make_pom() {
cat > "$1" <<'POM'
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>za.co.wethinkcode</groupId>
    <artifactId>demo</artifactId>
    <version>1.4.9</version>
    <dependencies>
        <dependency>
            <artifactId>lib</artifactId>
            <version>3.46.1.3</version>
        </dependency>
    </dependencies>
</project>
POM
}

pass=0; fail=0
check() {
    if [ "$2" = "true" ]; then echo "PASS: $1"; pass=$((pass+1));
    else echo "FAIL: $1"; fail=$((fail+1)); fi
}
project_version() { grep -m1 -o '<version>[^<]*</version>' "$1" | sed 's/<[^>]*>//g'; }

POM="$WORK/pom.xml"

make_pom "$POM"; OUT=$(bash "$SCRIPT" patch "$POM" 2>/dev/null)
check "patch: 1.4.9 -> 1.4.10"                       "$([ "$(project_version "$POM")" = "1.4.10" ] && echo true || echo false)"
check "prints '1.4.9 -> 1.4.10' on stdout"           "$([ "$OUT" = "1.4.9 -> 1.4.10" ] && echo true || echo false)"

make_pom "$POM"; bash "$SCRIPT" minor "$POM" >/dev/null 2>&1
check "minor: 1.4.9 -> 1.5.0 (patch resets)"         "$([ "$(project_version "$POM")" = "1.5.0" ] && echo true || echo false)"

make_pom "$POM"; bash "$SCRIPT" major "$POM" >/dev/null 2>&1
check "major: 1.4.9 -> 2.0.0 (minor and patch reset)" "$([ "$(project_version "$POM")" = "2.0.0" ] && echo true || echo false)"

make_pom "$WORK/before.xml"; make_pom "$POM"; bash "$SCRIPT" patch "$POM" >/dev/null 2>&1
check "dependency version 3.46.1.3 is untouched"     "$(grep -q '<version>3.46.1.3</version>' "$POM" && echo true || echo false)"
check "exactly one line of the pom changed"          "$([ "$(diff "$WORK/before.xml" "$POM" | grep -c '^>')" = "1" ] && echo true || echo false)"

mkdir "$WORK/cwd"; make_pom "$WORK/cwd/pom.xml"
(cd "$WORK/cwd" && bash "$SCRIPT" patch >/dev/null 2>&1)
check "defaults to ./pom.xml when no file is given"  "$([ "$(project_version "$WORK/cwd/pom.xml")" = "1.4.10" ] && echo true || echo false)"

make_pom "$POM"; ERR=$(bash "$SCRIPT" huge "$POM" 2>&1 >/dev/null); CODE=$?
check "unknown bump type: 'Usage:' on stderr, exit 2" "$([ $CODE -eq 2 ] && echo "$ERR" | grep -q '^Usage:' && echo true || echo false)"
check "unknown bump type leaves the pom alone"       "$([ "$(project_version "$POM")" = "1.4.9" ] && echo true || echo false)"

ERR=$(bash "$SCRIPT" 2>&1 >/dev/null); CODE=$?
check "no arguments: 'Usage:' on stderr, exit 2"     "$([ $CODE -eq 2 ] && echo "$ERR" | grep -q '^Usage:' && echo true || echo false)"

ERR=$(bash "$SCRIPT" patch "$WORK/missing.xml" 2>&1 >/dev/null); CODE=$?
check "missing pom: message on stderr, exit 1"       "$([ $CODE -eq 1 ] && [ -n "$ERR" ] && echo true || echo false)"

echo
echo "$pass passed, $fail failed"
[ "$fail" -eq 0 ]
