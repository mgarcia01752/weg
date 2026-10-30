# Raspberry Pi and original sensor board

## Prerequisites

- A supported Raspberry Pi OS suitable for the actual Pi and display.
- JDK 17 or newer, a C compiler, Make, and Bash.
- The original PIC24F16KA101 board and its GPS/BMP180/UV wiring.
- An enabled UART with the serial console disabled and user read/write access.
- Optional supported Pi camera and `rpicam-still` for photos.

Use current OS tooling to configure the UART/display/camera. The files under
`RaspberryPiConfig/` and the 2016 instructions are historical references, not modern
installation templates. The repository does not contain a verified complete
schematic, bill of materials, power budget, or enclosure design. Confirm wiring,
logic levels, shared ground, and power from the original hardware documentation.

Build the application and bridge, then query the actual device:

```bash
./scripts/build.sh
make -C uart-serial-ipc
./uart-serial-ipc/DamCommSocket --device /dev/serial0 --once
```

Choose the device path appropriate to your board (`/dev/serial0` is only a default).
Use OS-managed serial-device permissions rather than running the dashboard as root.
The bridge does not reset the PIC over GPIO. If the board needs a manual reset,
perform that separately according to its wiring.

Start the bridge and, in another terminal, the dashboard:

```bash
./uart-serial-ipc/DamCommSocket --device /dev/serial0
./scripts/run.sh --mode live
```

Only one bridge may access a device. The listener uses `127.0.0.1:5000`. For remote
viewing, keep that default and create an SSH tunnel from your desktop:

```bash
ssh -N -L 5000:127.0.0.1:5000 YOUR_PI_USER@YOUR_PI_HOST
```

Then connect the desktop dashboard to `127.0.0.1:5000`. A non-loopback `--bind`
exposes an unauthenticated service; it is unnecessary when using the tunnel.

## Optional user service

```bash
./scripts/install-user-service.sh --device /dev/serial0
systemctl --user enable --now weg-bridge.service
journalctl --user -u weg-bridge.service -f
```

The installer builds the binary, writes a user unit, and reloads the user service
manager. It does not enable/start the service, install packages, change groups,
configure lingering, or modify system files. The user must already have device
access. User services normally follow the user's login lifecycle. Configure boot
operation deliberately using your OS administration procedures if required.

To remove the service:

```bash
systemctl --user disable --now weg-bridge.service
rm -- "$HOME/.config/systemd/user/weg-bridge.service"
systemctl --user daemon-reload
```

## Required hardware acceptance checks

The host tests do not validate the PIC or the physical sensors. Before relying on
live data, build the firmware with the correct MPLAB/XC16 toolchain and verify:

1. Boot, UART pin mapping, 9600/8N1, and complete checksummed GGA/RMC frames.
2. Command 600 produces a response and unsupported commands do not.
3. GPS no-fix, reconnect, southern/western coordinates, and a measured walking route.
4. BMP180 calibration reads, known temperature/pressure comparisons, and UV scaling.
5. Long-running acquisition, unplug/reconnect behavior, and camera permissions.
6. Clean shutdown and recovery from power interruption.

The targeted firmware edits fix a width-unsafe calibration pointer cast, byte sign
extension, sentence buffering, and request handling. They are **not** a complete
rewrite of the old I2C driver. Its existing timeout/error propagation and sensor
calibration need board-level review. No firmware was flashed by this modernization.
