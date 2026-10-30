#!/usr/bin/env bash
set -euo pipefail
root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
java_bin="${JAVA_HOME:+$JAVA_HOME/bin/}"
command -v "${java_bin}javac" >/dev/null || { echo 'Install a JDK 17 or newer.' >&2; exit 1; }
mkdir -p "$root/build/classes"
# Limit cleanup to this script's class output; never touch vendor firmware outputs.
find "$root/build/classes" -type f -name '*.class' -delete
mapfile -d '' sources < <(find "$root/WegStartupUI/src" -name '*.java' -print0)
"${java_bin}javac" --release 17 -Xlint:all -d "$root/build/classes" "${sources[@]}"
"${java_bin}jar" --create --file "$root/build/weg.jar" --main-class weg.ui.WegStartupUI -C "$root/build/classes" .
echo "Built $root/build/weg.jar"
