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
    struct calib_data _bmp180_coeffs;

    readCoefficients(&_bmp180_coeffs);
    
    IEC0bits.U1RXIE = 1; //Enable GPS receive interrupt
    IEC1bits.U2RXIE = 1; //Enable Pi receive interrupt
    
    while(1){
        
    UVindex = getUVindex();
    Tdata = getTempData(&_bmp180_coeffs);
    Pdata = getPress(&_bmp180_coeffs);
    
        if(GPS_newNMEAreceived()){
          
            char *nmea = GPS_lastNMEA();
            
            sprintf(buf,"600:|%d|%.1f|%d|%s",UVindex,Tdata,Pdata,nmea);
            
            if(piFlag >= 1){
               sendPiCommand(buf);
               piFlag--;
            }
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
    piFlag = 2;
    char c = U2RXREG;  //Empty buffer to prevent overflow
    IFS1bits.U2RXIF = 0; //clear interrupt flag
}

