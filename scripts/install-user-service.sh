#!/usr/bin/env bash
set -euo pipefail
root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
device=/dev/serial0
while (($#)); do
    case "$1" in
        --help|-h) echo 'Usage: scripts/install-user-service.sh [--device /dev/serial0]'; exit 0 ;;
        --device) (($# >= 2)) || { echo 'Missing --device value' >&2; exit 2; }; device=$2; shift 2 ;;
        *) echo "Unknown option: $1" >&2; exit 2 ;;
    esac
done
[[ "$device" == /dev/* && "$device" != *$'\n'* ]] || { echo 'Device must be an absolute /dev path.' >&2; exit 2; }
command -v systemctl >/dev/null
command -v python3 >/dev/null
make -C "$root/uart-serial-ipc"
unit_dir="${XDG_CONFIG_HOME:-$HOME/.config}/systemd/user"
mkdir -p "$unit_dir"
python3 - "$root" "$device" "$unit_dir/weg-bridge.service" <<'PY'
from pathlib import Path
import sys
root, device, target = sys.argv[1:]
def quote(value):
    # Escape systemd specifiers and command-line metacharacters, not shell syntax.
    return '"' + value.replace('\\', '\\\\').replace('"', '\\"').replace('%', '%%').replace('$', '$$') + '"'
command = quote(str(Path(root) / 'uart-serial-ipc/DamCommSocket')) + ' --device ' + quote(device)
Path(target).write_text('[Unit]\nDescription=WEG serial sensor bridge\n\n[Service]\nType=simple\nExecStart=' + command + '\nRestart=on-failure\nRestartSec=3\nNoNewPrivileges=true\nUMask=0077\n\n[Install]\nWantedBy=default.target\n')
PY
systemctl --user daemon-reload
printf 'Installed %s/weg-bridge.service\nStart with: systemctl --user enable --now weg-bridge.service\n' "$unit_dir"
