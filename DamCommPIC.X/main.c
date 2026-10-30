/*
 * File:   main.c
 * Author: John
 *
 * Created on July 15, 2016, 8:35 PM
 */


#define FCK  8000000ULL
#define FCY  4000000ULL

#include <xc.h>
#include <p24F16KA101.h>
#include <libpic30.h>
#include <stdio.h>
#include "config.h"
#include "TPsensor.h"
#include "UART.h"
#include "GPS.h"
#include "UVsensor.h"
#include "PiComm.h"


#define UART_1_BAUD_RATE 9600
#define UART_2_BAUD_RATE 9600

volatile char piFlag = 0;
volatile unsigned int UVindex;
volatile double Tdata;
volatile unsigned int Pdata;

    
void init_osc(void);

void main(void) {
    
    init_osc();
    AD_Init();
    UART1_init(UART_1_BAUD_RATE); //UART1 = GPS module
    UART2_init(UART_2_BAUD_RATE); //UART2 = RPi comm
    GPS_init(); 
    init_I2C();
    
    char buf[255];
    char nmeaCopy[120];
    struct calib_data _bmp180_coeffs;

    readCoefficients(&_bmp180_coeffs);
    
    IEC0bits.U1RXIE = 1; //Enable GPS receive interrupt
    IEC1bits.U2RXIE = 1; //Enable Pi receive interrupt
    
    while(1){
        
    UVindex = getUVindex();
    Tdata = getTempData(&_bmp180_coeffs);
    Pdata = getPress(&_bmp180_coeffs);
    
        if(GPS_newNMEAreceived()){
          
            /* Snapshot the ISR-owned buffer before formatting a response. */
            IEC0bits.U1RXIE = 0;
            const char *nmea = GPS_lastNMEA();
            unsigned int index = 0;
            while (index < sizeof(nmeaCopy) - 1 && nmea[index]) {
                nmeaCopy[index] = nmea[index];
                index++;
            }
            nmeaCopy[index] = '\0';
            IEC0bits.U1RXIE = 1;
            
            snprintf(buf, sizeof(buf), "600:|%u|%.1f|%u|%s",
                     UVindex, Tdata, Pdata, nmeaCopy);
            
            /* Consume a pending request atomically before transmission. */
            IEC1bits.U2RXIE = 0;
            unsigned char requested = piFlag;
            piFlag = 0;
            IEC1bits.U2RXIE = 1;
            if (requested) sendPiCommand(buf);
        }   
    }  
}

void init_osc(void){
    
    OSCCON = 0x77A0; // 8 MHz fast RC oscillator with postscaler
    CLKDIV = 0x4000; //8 MHz Internal RC oscillator divided  by 1 = 8 MHz clock
    OSCTUN = 0x00; //Tune RC oscillator to factory settings
}

//Interrupt handler for GPS output sentence
void __attribute__((__interrupt__, auto_psv)) _U1RXInterrupt(void)
{ 
   char c = getGPSsentence();
   IFS0bits.U1RXIF = 0; //clear interrupt flag
}

//Interrupt handler for command from RPi
void __attribute__((__interrupt__, auto_psv)) _U2RXInterrupt(void)
{  
    static unsigned int command = 0;
    static unsigned char digits = 0;
    if (U2STAbits.OERR) U2STAbits.OERR = 0;
    while (U2STAbits.URXDA) {
        char c = U2RXREG;
        if (c >= '0' && c <= '9') {
            command = command * 10 + (unsigned int)(c - '0');
            if (++digits == 3) {
                if (command == 600) piFlag = 1;
                digits = 0; command = 0;
            }
        } else { digits = 0; command = 0; }
    }
    IFS1bits.U2RXIF = 0; //clear interrupt flag
}

