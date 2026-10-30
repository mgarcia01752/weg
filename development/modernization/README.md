# WEG modernization — 2026-10-29

## Authorized scope and baseline

The user requested modernization of the entire WEG college project after a source
review. The baseline is the checked-out WEG repository before this change. The
pre-existing untracked `.vscode/` directory is unrelated and remains untouched.
No commits, pushes, package installations, system service activation, boot changes,
or firmware flashing are part of this work.

## Design decisions

- Keep Java/Swing and the PIC → Pi UART bridge architecture.
- Make the default experience an offline simulator, with optional fault scenarios.
- Separate immutable sensor observations, source adapters, persistence, statistics,
  and presentation. Share bounded socket transport with the legacy facade.
- Keep existing `weg.das` public class names; document changed invalid-input semantics.
- Build with standard JDK tooling, targeting Java 17; use no downloaded runtime
  dependency for the application. Retain old third-party sources outside the build.
- Replace WiringPi with Linux termios; bound all bridge buffers and waits, default
  to loopback, and prevent duplicate bridge ownership of one serial device.
- Preserve CSV on interruption; export GPX from CSV without overwriting either file.
- Use optional camera still capture, with timestamped last-sample sidecars.
- Restrict PIC edits to source defects found during review; do not claim a validated
  firmware release or replace the old sensor drivers speculatively.

## Confirmed defects addressed

The review reproduced incorrect latitude conversion and invalid/negative temperature
handling in Java. Source inspection found an unbounded 50-byte bridge buffer,
indefinite reconnect/read behavior, Swing updates on the acquisition thread,
uninitialized GPS access, and a width-unsafe PIC calibration pointer cast.

The new tests cover coordinates/hemispheres, checksum validation, no-fix frames,
negative/malformed readings, transport fragmentation and deadlines, disconnect
recovery, distance gaps, CSV round trips, GPX validity and preservation of files.

## Validation

See `validation.md` for the final host results and explicit device limitations.
`docs/hardware.md` contains the physical-board acceptance checklist. The optional
`UiSmokeTest` requires a display-capable Java runtime and X server; the normal core
suite runs headlessly. Local GUI QA used tools extracted under ignored `build/`,
without a system package installation.

## Remaining hardware questions

- Which Pi, display resolution, UART mapping, camera, and original board revision
  are still available?
- Does the actual XC16 version support the updated formatted-output call, and does
  the buffer snapshot meet the interrupt-latency budget on the original clock?
- Are BMP180 calibration/error handling and UV conversion accurate on this board?
- What are the actual battery runtime, enclosure, and environmental requirements?

These do not block the simulator/recorder. They must be resolved before claiming
field-ready physical hardware support.
