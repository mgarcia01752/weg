#define _POSIX_C_SOURCE 200809L
#include <arpa/inet.h>
#include <errno.h>
#include <fcntl.h>
#include <getopt.h>
#include <limits.h>
#include <poll.h>
#include <signal.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/file.h>
#include <termios.h>
#include <time.h>
#include <unistd.h>

/* One serial owner; bounded, framed requests. No GPIO reset or elevated privileges. */
#define FRAME_CAPACITY 513
static volatile sig_atomic_t stopping;
static void stop(int sig) { (void)sig; stopping = 1; }
static long long now_ms(void) {
    struct timespec t;
    if (clock_gettime(CLOCK_MONOTONIC, &t) < 0) return 0;
    return (long long)t.tv_sec * 1000 + t.tv_nsec / 1000000;
}
static int wait_fd(int fd, short events, long long deadline) {
    while (!stopping) {
        long long left = deadline - now_ms();
        if (left <= 0) { errno = ETIMEDOUT; return -1; }
        struct pollfd p = { .fd = fd, .events = events };
        int result = poll(&p, 1, left > 100 ? 100 : (int)left);
        if (result < 0) { if (errno == EINTR) continue; return -1; }
        if (result == 0) continue;
        if (p.revents & events) return 0;
        if (p.revents & (POLLHUP | POLLERR | POLLNVAL)) { errno = EIO; return -1; }
    }
    errno = EINTR;
    return -1;
}
static int write_all(int fd, const char *data, size_t size, long long deadline) {
    while (size > 0) {
        if (wait_fd(fd, POLLOUT, deadline) < 0) return -1;
        ssize_t n = write(fd, data, size);
        if (n < 0) { if (errno == EINTR || errno == EAGAIN) continue; return -1; }
        if (n == 0) { errno = EIO; return -1; }
        data += n; size -= (size_t)n;
    }
    return 0;
}
/* Both the original PIC's NUL framing and newline framing are accepted. */
static int read_frame(int fd, char *buffer, size_t capacity, long long deadline) {
    size_t length = 0;
    while (!stopping) {
        if (wait_fd(fd, POLLIN, deadline) < 0) return -1;
        unsigned char c;
        ssize_t n = read(fd, &c, 1);
        if (n < 0) { if (errno == EINTR || errno == EAGAIN) continue; return -1; }
        if (n == 0) { errno = ECONNRESET; return -1; }
        if (c == '\r') continue;
        if (c == '\n' || c == '\0') {
            if (length == 0) continue;
            buffer[length] = '\0'; return 0;
        }
        if (c < 32 || c > 126) { errno = EPROTO; return -1; }
        if (length + 1 >= capacity) { errno = EMSGSIZE; return -1; }
        buffer[length++] = (char)c;
    }
    errno = EINTR; return -1;
}
static int serial_open(const char *device, int baud) {
    int fd = open(device, O_RDWR | O_NOCTTY | O_NONBLOCK);
    if (fd < 0) return -1;
    if (flock(fd, LOCK_EX | LOCK_NB) < 0) { close(fd); return -1; }
    struct termios tty;
    if (tcgetattr(fd, &tty) < 0) { close(fd); return -1; }
    tty.c_iflag = 0; tty.c_oflag = 0; tty.c_lflag = 0;
    tty.c_cflag = CS8 | CLOCAL | CREAD;
    tty.c_cc[VMIN] = 0; tty.c_cc[VTIME] = 0;
    speed_t speed = baud == 115200 ? B115200 : B9600;
    if (cfsetispeed(&tty, speed) < 0 || cfsetospeed(&tty, speed) < 0 || tcsetattr(fd, TCSANOW, &tty) < 0) {
        close(fd); return -1;
    }
    return fd;
}
static int query(int fd, char *frame, int timeout) {
    if (tcflush(fd, TCIFLUSH) < 0) return -1;
    long long deadline = now_ms() + timeout;
    if (write_all(fd, "600", 3, deadline) < 0) return -1;
    if (read_frame(fd, frame, FRAME_CAPACITY, deadline) < 0) return -1;
    if (strncmp(frame, "600:|", 5) != 0) { errno = EPROTO; return -1; }
    return 0;
}
static int number(const char *text, int min, int max) {
    char *end;
    errno = 0;
    long n = strtol(text, &end, 10);
    if (errno || *text == '\0' || *end || n < min || n > max) {
        fprintf(stderr, "Invalid numeric argument: %s\n", text); exit(2);
    }
    return (int)n;
}
static void usage(void) {
    puts("WEG serial bridge 2.0\n"
         "Usage: DamCommSocket [options]\n"
         "  --device PATH     Serial device (default /dev/serial0)\n"
         "  --bind IPV4       Listen address (default 127.0.0.1)\n"
         "  --port PORT       TCP port (default 5000)\n"
         "  --baud RATE       9600 or 115200 (default 9600)\n"
         "  --timeout-ms N    UART deadline, 100..3000 (default 2500)\n"
         "  --once            Print one sensor frame and exit\n"
         "  --help            Show help\n"
         "Protocol: send 600 followed by newline; receive one frame or ERR line.\n"
         "Remote access: use an SSH tunnel; this bridge does not authenticate clients.");
}
int main(int argc, char **argv) {
    const char *device = "/dev/serial0", *address = "127.0.0.1";
    int port = 5000, baud = 9600, timeout = 2500;
    bool once = false;
    static const struct option options[] = {
        {"device", required_argument, NULL, 's'}, {"bind", required_argument, NULL, 'a'},
        {"port", required_argument, NULL, 'p'}, {"baud", required_argument, NULL, 'b'},
        {"timeout-ms", required_argument, NULL, 't'}, {"once", no_argument, NULL, '1'},
        {"help", no_argument, NULL, 'h'}, {NULL, 0, NULL, 0}
    };
    int opt;
    while ((opt = getopt_long(argc, argv, "s:a:p:b:t:1h", options, NULL)) != -1) {
        switch (opt) {
        case 's': device = optarg; break;
        case 'a': address = optarg; break;
        case 'p': port = number(optarg, 1, 65535); break;
        case 'b': baud = number(optarg, 9600, 115200); break;
        case 't': timeout = number(optarg, 100, 3000); break;
        case '1': once = true; break;
        case 'h': usage(); return 0;
        default: usage(); return 2;
        }
    }
    if (optind != argc || (baud != 9600 && baud != 115200)) { usage(); return 2; }
    struct sockaddr_in server = { .sin_family = AF_INET, .sin_port = htons((unsigned short)port) };
    if (inet_pton(AF_INET, address, &server.sin_addr) != 1) { fputs("Invalid IPv4 bind address\n", stderr); return 2; }
    struct sigaction action = {0}; action.sa_handler = stop;
    sigemptyset(&action.sa_mask);
    sigaction(SIGINT, &action, NULL); sigaction(SIGTERM, &action, NULL);
    signal(SIGPIPE, SIG_IGN);
    int serial = serial_open(device, baud);
    if (serial < 0) { perror("Open serial device"); return 1; }
    char frame[FRAME_CAPACITY];
    if (once) {
        int result = query(serial, frame, timeout);
        if (result == 0) puts(frame); else perror("Sensor query");
        close(serial); return result == 0 ? 0 : 1;
    }
    int listener = socket(AF_INET, SOCK_STREAM, 0);
    if (listener < 0) { perror("socket"); close(serial); return 1; }
    int reuse = 1;
    if (setsockopt(listener, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse)) < 0 ||
        bind(listener, (struct sockaddr *)&server, sizeof(server)) < 0 || listen(listener, 8) < 0) {
        perror("Listen"); close(listener); close(serial); return 1;
    }
    fprintf(stderr, "WEG bridge: %s:%d -> %s (%d baud)\n", address, port, device, baud);
    while (!stopping) {
        if (wait_fd(listener, POLLIN, now_ms() + 1000) < 0) {
            if (errno == ETIMEDOUT || errno == EINTR) continue;
            perror("Wait for client"); break;
        }
        int client = accept(listener, NULL, NULL);
        if (client < 0) { if (errno != EINTR) perror("Accept"); continue; }
        if (fcntl(client, F_SETFL, O_NONBLOCK) < 0) { close(client); continue; }
        char command[16];
        const char *error = NULL;
        if (read_frame(client, command, sizeof(command), now_ms() + 1000) < 0) error = "ERR invalid or timed-out request\n";
        else if (strcmp(command, "600") != 0) error = "ERR only command 600 is supported\n";
        else if ((serial < 0 && (serial = serial_open(device, baud)) < 0) || query(serial, frame, timeout) < 0) {
            fprintf(stderr, "Sensor query: %s\n", strerror(errno));
            if (serial >= 0) close(serial);
            serial = -1;
            error = "ERR sensor timeout or invalid frame\n";
        }
        if (error) (void)write_all(client, error, strlen(error), now_ms() + 500);
        else {
            if (write_all(client, frame, strlen(frame), now_ms() + 500) == 0)
                (void)write_all(client, "\n", 1, now_ms() + 500);
        }
        close(client);
    }
    close(listener); close(serial);
    return stopping ? 0 : 1;
}
