# Modernization and migration

## Replaced behavior

- Build with `scripts/build.sh`; launch `build/weg.jar`, not the old `dist` JAR.
- Java 17 is the minimum language/API level. NetBeans properties match that target.
- The generated fixed-layout Swing form was replaced by a hand-maintained dashboard.
- Simulation is the default. Hardware and camera access are explicit.
- TCP reconnect attempts are bounded; invalid data does not kill acquisition.
- Trips persist as CSV and export as GPX. Routes/plots operate offline.
- The C bridge uses termios rather than WiringPi, with bounded framing and deadlines.
- Use `--device`, `--bind`, `--port`, `--baud`, `--timeout-ms`, and `--once`.
  Old per-sensor flags (`-G`, `-U`, `-T`, `-B`, `-S`), raw `-i`, GPIO-reset `-r`,
  and debug `-d` are retired. The hardware protocol implemented here is command 600.
- No root Java launcher, broad chmod 777, rc.local replacement, or automatic PIC reset.
- Photos use optional `rpicam-still`; the legacy preview facade uses `rpicam-vid`.

The existing `weg.das` class names remain as compatibility helpers, but the new
application uses `weg.core`. Invalid UV/pressure input now returns null; invalid
text temperature returns a dash. Callers must treat these as unavailable. GPS
parsing validates checksums and no longer accepts malformed sentences.

## Preserved historical material

Original PIC project metadata, third-party map sources, image assets, old packaged
binaries, and boot configuration remain available for reference. They are not
consumed by the modern Java/bridge build. `docs/legacy-install.md` preserves the
original installation guide, explicitly marked as historical.

Old desktop/rc.local templates are retired. Use the project-relative `startup/WEG.sh`
wrapper and the documented user service. `startup/UpdateGUI.sh` performs an explicit
fast-forward-only Git update followed by a fresh build when the user invokes it;
it never runs during application startup.

## Deliberate boundaries

This is an excursion recorder, not a safety navigation or weather-warning product.
Offline basemaps, turn-by-turn navigation, magnetic heading, GPS noise filtering,
battery telemetry, BLE/mobile clients, and a new hardware enclosure are not claimed.
The desktop can be used now with simulation and recordings; physical-board support
must pass the acceptance checks in `hardware.md`.
