/*
 * File:   main.c
 * Author: John
 *
 * Created on May 18, 2016, 7:36 PM
 */
#define FCK  8000000ULL
#define FCY  4000000ULL

#include <xc.h>
#include <p24F16KA101.h>
#include <libpic30.h>
#include <stdio.h>
#include "config.h"
#include "TempSensor.h"
#include "UART2.h"


void init_osc(void);

char stateI2C; 


void main(void) {
    
    char x;  //Holds character sent from RPi
    char buff1[100];  //Array for temperature formatted string
    char buff2[100];  //Array for pressure formated string
    
    struct calib_data _bmp180_coeffs;
    struct calib_data *holder;
    
    holder = &_bmp180_coeffs;
    long temp = 0;
    long pressure = 0;
    TRISBbits.TRISB1 = 1; //Set RB1 as input
    AD1PCFG = 0xFFFF;  //Disable analog inputs,set all as digital I/O
    init_osc();  //Initialize oscillator
    init_I2C();  //Initialize I2C module
    UART2_init(9600); //Initialize UART1 module to communicate to RPi
    
    while(stateI2C == 1){               //Ensure I2C device is responsive
    stateI2C = poll_I2C(BMP180_WRITE);
    }
    
    readCoefficients(holder);
    
    __delay_us(8000);
    temp = getTemp(holder);
    pressure = getPressure(holder);
    
    temp = (temp/10); //Temperature in Celsius
    pressure = (pressure/100); //Pressure in mbars
    char a = '\r';
    char b = '\n';
    
    sprintf(buff1, "Temperature: %d Celsius %c%c", (int)temp, a, b);
    sprintf(buff2, "Air Pressure: %d millibars %c%c", (int)pressure, a, b);
    
    
    while(1){
    x = UART2_GetChar();
    
    if(x == 't'){
        
        sendArray(buff1);
        sendArray(buff2);
        x = 0;
    }
    
    }
}

void init_osc(void){
    
    OSCCON = 0x77A0; // 8 MHz fast RC oscillator with postscaler
    CLKDIV = 0x4000; //8 MHz Internal RC oscillator divided  by 1 = 8 MHz clock
    OSCTUN = 0x00; //Tune RC oscillator to factory settings
}
