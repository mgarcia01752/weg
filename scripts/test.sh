#!/usr/bin/env bash
set -euo pipefail
root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
"$root/scripts/build.sh"
mkdir -p "$root/build/test-classes"
mapfile -d '' tests < <(find "$root/tests/java" -name '*.java' -print0)
"${JAVA_HOME:+$JAVA_HOME/bin/}javac" --release 17 -Xlint:all -cp "$root/build/classes" -d "$root/build/test-classes" "${tests[@]}"
"${JAVA_HOME:+$JAVA_HOME/bin/}java" -ea -Djava.awt.headless=true -cp "$root/build/classes:$root/build/test-classes" weg.core.CoreTest
make -C "$root/uart-serial-ipc" test
