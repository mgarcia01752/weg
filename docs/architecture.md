# WEG architecture

```text
Simulation ─┐
CSV replay ─┼─ SampleSource → immutable Sample → dashboard + TripStats
TCP bridge ─┘                                  └→ TripLog CSV → GPX
    │
Linux termios UART → PIC24 → GPS / BMP180 / UV ADC
```

## Runtime

`weg.core` is independent of Swing. `SampleSource` provides one observation;
`Simulator`, `ReplaySource`, and `SocketSource` implement it. The desktop uses a
single acquisition worker and posts presentation updates to Swing's event thread.
Camera capture has its own worker so capture does not interrupt sensor polling.
Closing the window cancels polling, closes the CSV, and shuts down workers.

Polling is once per second **after** each read completes. Live reads have a 1.5 s
TCP connect timeout and a 4 s whole-response deadline. DNS resolution is delegated
to the JVM/OS; use an IP address for predictable connection latency. Errors are
shown as unavailable, followed by a bounded-delay retry. A sample older than five
wall-clock seconds is labeled stale. Playback stops at EOF or a malformed row.

Each connection/disconnection has a generation number so late worker callbacks
cannot overwrite the new session's state. All CSV operations share the acquisition
worker; recording start/stop is ordered with incoming samples.

## Measurements

A `Sample` carries a UTC receipt/recorded timestamp, °C, hPa, UV index, decimal-degree
coordinates, elevation in metres, satellite count, and fix validity. `NaN` means
unavailable. Missing values appear as a dash, never a fabricated zero. The PIC's
Fahrenheit output is converted at the input boundary. Satellite count is unavailable
in RMC and currently represented as zero; GGA supplies the actual count.

NMEA checksums are mandatory. GGA and RMC accept two-letter talker identifiers such
as GP and GN. Coordinates convert degrees plus minutes/60 with hemisphere signs.
No-fix sentences can still deliver environmental measurements. GPS altitude is
used only when GGA reports metres. The sample timestamp is host receipt time,
not GPS UTC; the legacy GGA payload alone does not supply a date.

Distance uses a spherical haversine calculation. Speed is km/h, and course is
movement direction, not a magnetic compass. Missing fixes, non-increasing times,
and gaps over ten seconds break distance accumulation. Movement under one metre
has no displayed course. No GPS-jitter filter or terrain correction is claimed.
Elapsed time and distance reset when connecting or starting a trip.

The route is a north-up trace with local longitude scaling, **not a basemap or
navigation system**. At most 1,800 recent samples are drawn. CSV recording retains
all accepted samples. The plot is intended for local excursions; polar and
dateline-crossing map projections are not implemented.

## Wire protocol

TCP request: ASCII `600\n`. TCP response: one `600:|...` frame followed by newline,
or an `ERR ...\n` line. Maximum response length is 512 bytes. The bridge rejects
unsupported commands, non-printable input, overflow, and unframed/timed-out input.
It accepts NUL or newline delimiters from the original PIC and strips CR.

UART request: ASCII `600`, 9600 baud, 8 data bits, no parity, one stop bit. The
bridge flushes pending receive bytes before requesting a sample. The old firmware
may send more than one response per request; only the first complete response is
returned. The updated firmware recognizes command 600 and schedules one response.
There are no transaction IDs, so a delayed response on old firmware cannot be
proved to belong to a particular request. Validate cadence on the real board.

A cooperative exclusive file lock prevents two bridge processes from owning the
same UART. Other serial programs must also be stopped. The bridge serializes clients. Idle TCP clients
are limited to one second; UART responses default to 2.5 seconds, configurable up
to 3 seconds. A failed serial query closes the device; the next request attempts to reopen it.
No implicit GPIO reset occurs. The listener defaults to loopback;
remote use should go through SSH forwarding.

## Storage and camera

CSV schema version 1 is identified by this exact header:

```text
time,temperature_c,pressure_hpa,uv_index,latitude,longitude,elevation_m,satellites,fix
```

Trip filenames identify the source (`simulate-trip-`, `live-trip-`, or
`replay-trip-` for headless re-recordings). Acquisition failures add an unavailable observation so even short outages break
the exported route. Rows are appended and flushed after each accepted sample. Flush is not a guarantee
against power loss; no per-row filesystem sync is performed. Existing files are
never overwritten. GPX export uses a temporary sibling file and publishes only
a completed document. Export preserves timestamps and separates tracks at missing
fixes, time reversal, and gaps over ten seconds. A corrupt/truncated CSV row stops
playback/export with its row number instead of silently losing data.

The optional live-mode photo button runs `rpicam-still` without sudo, with a
12-second process limit. Photos and camera logs live in a directory named after
the trip, with a CSV sidecar holding the last sensor sample. That sample's timestamp
is preserved; it is not claimed to be an exact shutter-time position. Photos are
not embedded into GPX. The camera is never started automatically.

## Build ownership

`scripts/build.sh` compiles the checked-in Java sources with `--release 17` and
packages `build/weg.jar`. It ignores old checked-in distribution binaries and map
libraries. The PIC remains a separate MPLAB/XC16 build. Linux bridge builds use
`uart-serial-ipc/Makefile`. No installer performs package installation or changes
boot configuration.
