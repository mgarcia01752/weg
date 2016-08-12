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

    
void init_osc(void);

void main(void) {
    
    
    init_osc();
    AD_Init();
    
    int command;
    char GPS_data[255];
    char Temp_data[20];
    char Press_data[20];
    char UV_data[10];
    struct calib_data _bmp180_coeffs;
    
    
    UART1_init(UART_1_BAUD_RATE); //UART1 = GPS module
    UART2_init(UART_2_BAUD_RATE); //UART2 = RPi comm
    GPS_init();
    init_I2C();
    
    readCoefficients(&_bmp180_coeffs);
   // __delay_ms(20000);
    
 
    while(1){
        
        command = getPiCommand();
        
        switch(command){
            
            case 100:
                sendPiCommand(GPS_OK);
                break;
            case 101:
                getGPSsentence(GPS_data,GGA);
                sendPiCommand(GPS_data);
                break;
            case 102:
                getGPSsentence(GPS_data,RMC);
                sendPiCommand(GPS_data);
                break;
            case 110:
                sendPiCommand(GPS_OK);
                break;
            case 200:
                sendPiCommand(UV_OK);
                break;
            case 201:
                getUVindex(UV_data);
                sendPiCommand(UV_data);
                break;
            case 210:
                sendPiCommand(UV_OK);
                break;
            case 300:
                sendPiCommand(TP_OK);
                break;
            case 301:
                getTempString(Temp_data,&_bmp180_coeffs,CELSIUS);
                sendPiCommand(Temp_data);
                break;
            case 302:
                getTempString(Temp_data,&_bmp180_coeffs,FAHRENHEIT);
                sendPiCommand(Temp_data);
                break;
            case 303:
                getPressString(Press_data,&_bmp180_coeffs);
                sendPiCommand(Press_data);
                break;
            case 310:
                sendPiCommand(TP_OK);
                break;
            case 400:
                sendPiCommand("450:<SP Voltage>\r\n\0");
                break;
            default:
                sendPiCommand(RECEIVE_ERROR);
        }
    }  
}

void init_osc(void){
    
    OSCCON = 0x77A0; // 8 MHz fast RC oscillator with postscaler
    CLKDIV = 0x4000; //8 MHz Internal RC oscillator divided  by 1 = 8 MHz clock
    OSCTUN = 0x00; //Tune RC oscillator to factory settings
}
