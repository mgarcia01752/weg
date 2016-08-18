#define FCK  8000000ULL
#define FCY  4000000ULL

#include <libpic30.h>
#include <p24F16KA101.h>
#include <string.h>
#include <stdio.h>


void AD_Init(void){
    
    AD1PCFG = 0xFBFF; // Congigure A/D port, AN10 input set as analog input
    AD1CON1 = 0; // Integer format, manual conversion trigger
    AD1CON2 = 0; // Use Vdd and Vss as reference, interrupt after sample
    AD1CON3 = 0x0100; // Sample time = 1Tad, Tad = Tcy
    AD1CHS = 0x0A0A; //Configure input channel to AN10
    AD1CSSL = 0; //No inputs are scanned
    AD1CON1bits.ADON = 1; //Turn ADC on
}

void getUVindex(char *UV_index){
    
    unsigned int ADCValue;
    unsigned int UVindex;
    AD1CON1bits.SAMP = 1; // Start Sampling
    __delay_ms(1);
    AD1CON1bits.SAMP = 0;
    while(!AD1CON1bits.DONE); //Wait for conversion to be completed
    ADCValue = ADC1BUF0; //Store ADC value
    UVindex = ((ADCValue * 3.3) / 1024) / 0.1;
    sprintf(UV_index,"250:%d\r\n",UVindex);
}


