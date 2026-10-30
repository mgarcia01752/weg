#!/usr/bin/env python3
"""Exercise the real bridge using a PTY: no WiringPi, root, or physical UART."""
import os
from pathlib import Path
import pty
import select
import socket
import subprocess
import threading
import tempfile
import csv
import xml.etree.ElementTree as ET
import time
import unittest

ROOT = Path(__file__).resolve().parents[1]
BINARY = Path(os.environ.get('WEG_BRIDGE', ROOT / 'uart-serial-ipc/DamCommSocket'))
FRAME = b'600:|3|62.5|1000|$GPGGA,070024.000,4009.3988,N,07452.0023,W,1,07,1.26,65.1,M,-34.0,M,,*67'


class BridgeTest(unittest.TestCase):
    def setUp(self):
        self.master, self.slave = pty.openpty()
        with socket.socket() as probe:
            probe.bind(('127.0.0.1', 0))
            self.port = probe.getsockname()[1]
        self.process = subprocess.Popen([str(BINARY), '--device', os.ttyname(self.slave),
                                         '--port', str(self.port), '--timeout-ms', '300'],
                                        stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        deadline = time.monotonic() + 3
        while time.monotonic() < deadline:
            if self.process.poll() is not None:
                self.fail(self.process.communicate()[1].decode())
            try:
                with socket.create_connection(('127.0.0.1', self.port), timeout=.05):
                    return
            except OSError:
                time.sleep(.02)
        self.fail('Bridge failed to start')

    def tearDown(self):
        self.process.terminate()
        _, stderr = self.process.communicate(timeout=3)
        self.assertEqual(self.process.returncode, 0, stderr.decode())
        os.close(self.master)
        os.close(self.slave)

    def exchange(self, payload, command=b'600\n', fragmented=False):
        failures = []
        def sensor():
            try:
                request = b''
                deadline = time.monotonic() + 2
                while len(request) < 3 and time.monotonic() < deadline:
                    ready, _, _ = select.select([self.master], [], [], .1)
                    if ready:
                        request += os.read(self.master, 128)
                if request != b'600':
                    raise AssertionError(f'Wrong serial request: {request!r}')
                if payload is not None:
                    if fragmented:
                        for start in range(0, len(payload), 7):
                            os.write(self.master, payload[start:start+7])
                            time.sleep(.002)
                    else:
                        os.write(self.master, payload)
            except Exception as error:
                failures.append(error)
        thread = threading.Thread(target=sensor) if command == b'600\n' else None
        if thread:
            thread.start()
        with socket.create_connection(('127.0.0.1', self.port), timeout=2) as client:
            for byte in command:
                client.sendall(bytes([byte]))
            response = b''
            while True:
                data = client.recv(2048)
                if not data:
                    break
                response += data
        if thread:
            thread.join(3)
            self.assertFalse(thread.is_alive())
        if failures:
            raise failures[0]
        return response

    def test_full_frame_longer_than_old_buffer(self):
        self.assertGreater(len(FRAME), 50)
        self.assertEqual(self.exchange(FRAME + b'\0', fragmented=True), FRAME + b'\n')

    def test_newline_and_leading_separators(self):
        self.assertEqual(self.exchange(b'\r\n\0' + FRAME + b'\r\n'), FRAME + b'\n')

    def test_overflow_and_recovery(self):
        self.assertTrue(self.exchange(b'x' * 700 + b'\n').startswith(b'ERR '))
        self.assertEqual(self.exchange(FRAME + b'\0'), FRAME + b'\n')

    def test_timeout_and_recovery(self):
        self.assertTrue(self.exchange(None).startswith(b'ERR '))
        self.assertEqual(self.exchange(FRAME + b'\0'), FRAME + b'\n')

    @unittest.skipUnless((ROOT / 'build/weg.jar').exists(), 'Build Java before end-to-end test')
    def test_java_to_bridge_to_uart_recording(self):
        failures = []
        def sensor():
            try:
                for _ in range(2):
                    request = b''
                    deadline = time.monotonic() + 5
                    while len(request) < 3 and time.monotonic() < deadline:
                        ready, _, _ = select.select([self.master], [], [], .1)
                        if ready:
                            request += os.read(self.master, 128)
                    if request != b'600':
                        raise AssertionError('End-to-end serial request missing')
                    os.write(self.master, FRAME + b'\0')
            except Exception as error:
                failures.append(error)
        thread = threading.Thread(target=sensor)
        thread.start()
        try:
            with tempfile.TemporaryDirectory(prefix='weg-e2e-') as folder:
                java = str(Path(os.environ['JAVA_HOME']) / 'bin/java') if 'JAVA_HOME' in os.environ else 'java'
                result = subprocess.run([java, '-jar', str(ROOT / 'build/weg.jar'), '--headless',
                                         '--mode', 'live', '--port', str(self.port), '--samples', '2',
                                         '--output', folder], capture_output=True, text=True, timeout=10)
                self.assertEqual(result.returncode, 0, result.stderr)
                trip = next(Path(folder).glob('*.csv'))
                with trip.open() as stream:
                    rows = list(csv.DictReader(stream))
                self.assertEqual(len(rows), 2)
                self.assertAlmostEqual(float(rows[0]['latitude']), 40 + 9.3988 / 60)
                tree = ET.parse(next(Path(folder).glob('*.gpx')))
                self.assertEqual(len(tree.findall('.//{http://www.topografix.com/GPX/1/1}trkpt')), 2)
        finally:
            thread.join(6)
        self.assertFalse(thread.is_alive())
        if failures:
            raise failures[0]

    def test_second_bridge_cannot_own_same_uart(self):
        result = subprocess.run([str(BINARY), '--device', os.ttyname(self.slave), '--once'],
                                capture_output=True, timeout=2)
        self.assertNotEqual(result.returncode, 0)
        self.assertIn(b'Open serial device', result.stderr)

    def test_invalid_command(self):
        self.assertIn(b'only command 600', self.exchange(None, b'101\n'))

    def test_idle_client_cannot_hold_bridge_forever(self):
        start = time.monotonic()
        with socket.create_connection(('127.0.0.1', self.port), timeout=2) as client:
            self.assertTrue(client.recv(100).startswith(b'ERR '))
        self.assertLess(time.monotonic() - start, 2)


if __name__ == '__main__':
    unittest.main()
