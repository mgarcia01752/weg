# Host validation — 2026-10-29

## Passed

- Java sources compile with `javac --release 17 -Xlint:all` on the available
  OpenJDK 21 host without warnings; the JAR runs without external Java libraries.
- `scripts/test.sh`: 46 Java behavioral assertions and eight Python bridge tests.
- Tests cover NMEA conversion/checksums, missing fixes, negative/malformed sensor
  values, CSV round trips, safe GPX export, distance gaps, simulator faults,
  fragmented responses, trickling socket deadlines, oversized UART frames,
  serial timeouts/recovery, duplicate serial ownership, and idle-client deadlines.
- End-to-end test: Java live recorder → actual C bridge → pseudo-terminal sensor
  responder → CSV and parsed GPX with correct coordinates.
- All seven bridge tests present at the sanitizer checkpoint passed with both
  AddressSanitizer and UndefinedBehaviorSanitizer enabled. The eighth, subsequently
  added Java-to-bridge integration test passed with the normal bridge binary.
- Headless simulation recording and replay both generated CSV and GPX artifacts.
- GUI smoke test passed startup, recording, stop, reconnect, resize, and shutdown.
  Screenshots were visually inspected at 1100×740 and 800×480. A scrollable content
  area preserves access to plots on small displays. The final default-size preview
  is `docs/assets/dashboard.png`.
- Bash syntax checks for all new/updated scripts, launcher `--help`, service
  installer `--help`, and `git diff --check` passed.
- User service generation tested with an isolated configuration directory and
  mocked `systemctl`. No user/system service was installed or activated.

## Environment and limits

The host has a headless Java installation. GUI validation used a copied JDK with
matching desktop libraries and Xvfb extracted locally under ignored `build/`;
no system packages were installed. Those temporary QA tools are not application
dependencies. The dashboard requires a display-capable JDK/JRE when run graphically.
The application supports `JAVA_HOME` for selecting a full local JDK.

The normal test command is headless. The optional GUI smoke test can be run after
`scripts/test.sh` on a desktop Java installation, for example:

```bash
xvfb-run -a java -cp build/classes:build/test-classes \
  weg.core.UiSmokeTest build/ui-smoke
```

ShellCheck was not available. The generated systemd unit was inspected; host-level
`systemd-analyze verify` also emitted unrelated installed-unit permission/version
warnings, so service-manager behavior remains unverified.

No Raspberry Pi, PIC toolchain/board, camera, physical sensor, battery, Windows, or
macOS test was performed. The firmware source changes are not a validated binary
release. Follow `docs/hardware.md` for the required device acceptance checks.

## Scope review

The code-cleanup review compared ordinary and whitespace-ignored diffs. Most
removed lines are the old generated form, boilerplate, and obsolete polling/bridge
implementation. Core responsibilities have focused owners, and the legacy socket
facade delegates to the same transport as the new application. Existing `weg.das`
class names remain; CLI and invalid-data behavior changes are documented.
The pre-existing `.vscode/` directory was not changed. No commits or pushes were made.
