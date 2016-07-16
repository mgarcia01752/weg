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


void init_osc(void);

void main(void) {
    
    TRISBbits.TRISB1 = 1; //Set RB1 as input for RX
    AD1PCFG = 0xFFFF; //Disable analog inputs, set all as digital I/O
    init_osc();
    UART1_init(9600); //UART1 = GPS module
    UART2_init(115200); //UART2 = RPi comm
    
    int command;
    
    while(1){
        
        command = getPiCommand();
        
        switch(command){
            
            case 100:
                sendPiCommand(GPS_OK);
                break;
            case 101:
                sendPiCommand("150:<NMEA Sentence>\0");
                break;
            case 110:
                sendPiCommand(GPS_OK);
                break;
            case 200:
                sendPiCommand(UV_OK);
                break;
            case 201:
                sendPiCommand("250:<UV Index>\0");
                break;
            case 210:
                sendPiCommand(UV_OK);
                break;
            case 300:
                sendPiCommand(TP_OK);
                break;
            case 301:
                sendPiCommand("350:<Temperature(C)>\0");
                break;
            case 302:
                sendPiCommand("350:<Pressure(mB)>\0");
                break;
            case 310:
                sendPiCommand(TP_OK);
                break;
            case 400:
                sendPiCommand("450:<SP Voltage>\0");
                break;
            default:
                sendPiCommand("TRANSMIT ERROR\0");
        }
    }
    
    
}

void init_osc(void){
    
    OSCCON = 0x77A0; // 8 MHz fast RC oscillator with postscaler
    CLKDIV = 0x4000; //8 MHz Internal RC oscillator divided  by 1 = 8 MHz clock
    OSCTUN = 0x00; //Tune RC oscillator to factory settings
}
