#define FCK  8000000ULL
#define FCY  4000000ULL

#include <libpic30.h>
#include <p24F16KA101.h>
#include <string.h>
#include <stdio.h>
#include "UART.h"
#include "GPS.h"


#define MAXLINELENGTH 120

// double buffer to get two sentence types, GGA and RMC
volatile char line1[MAXLINELENGTH];
volatile char line2[MAXLINELENGTH];
// our index into filling the current line
volatile int lineidx=0;
// pointers to the double buffers
volatile char *currentline;
volatile char *lastline;
volatile unsigned char recvdflag;


//Initialize GPS module by sending commands to module
void GPS_init(void){
    
    recvdflag = false;
	lineidx = 0;
	currentline = line1;
	lastline = line2;
    sendCommand(PMTK_SET_NMEA_OUTPUT_RMCGGA); //outputs RMC & GGA sentences  
    __delay_ms(1000);
}

//Resets receive flag and returns pointer to last sentence
char *GPS_lastNMEA(void) {
    
	recvdflag = false;
	return (char *)lastline;
}

//Returns true if GPS sentence has been received
char GPS_newNMEAreceived(void) {
    
	return recvdflag;
}

//Retrieves sentence from GPS module
char getGPSsentence(void){
    
    char c = 0;

    c = UART1_GetChar();
    
    if (c == '$') {
		currentline[lineidx] = 0;
		lineidx = 0;
	}
    
	if (c == '\n') {
		currentline[lineidx] = 0;

		if (currentline == line1) {
			currentline = line2;
			lastline = line1;
		} else {
			currentline = line1;
			lastline = line2;
		}
		lineidx = 0;
		recvdflag = true;
	}

	currentline[lineidx++] = c;
	if (lineidx >= MAXLINELENGTH)
	lineidx = MAXLINELENGTH-1;

	return c;
    
}

//Sends commands to GPS module via UART1 module
void sendCommand(char *str){
    
    char i;
    
    do{
        UART1_PutChar(*str);
        i = *str;
        str++;
    }while(i != '\n');
}

