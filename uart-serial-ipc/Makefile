CC ?= cc
CFLAGS ?= -O2 -g
CFLAGS += -std=c11 -Wall -Wextra -Wpedantic -Werror

.PHONY: all clean test
all: DamCommSocket
DamCommSocket: DamCommSocket.c
	$(CC) $(CPPFLAGS) $(CFLAGS) $(LDFLAGS) -o $@ $< $(LDLIBS)
clean:
	$(RM) DamCommSocket *.o
test: DamCommSocket
	python3 ../tests/test_bridge.py
