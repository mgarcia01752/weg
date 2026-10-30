#!/usr/bin/env bash
set -euo pipefail
root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
"$root/scripts/build.sh"
exec "${JAVA_HOME:+$JAVA_HOME/bin/}java" -Dswing.aatext=true -Dawt.useSystemAAFontSettings=on -jar "$root/build/weg.jar" "$@"
