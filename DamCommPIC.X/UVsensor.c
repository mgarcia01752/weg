#include <p24F16KA101.h>

void AD_Init(void){
    
    AD1PCFG = 0xFFFE; // Congigure A/D port, AN0 input set as analog input
    AD1CON1 = 0x20E2; // Integer format, manual conversion trigger
    AD1CON2 = 0; // Use Vdd and Vss as reference, interrupt after sample
    AD1CON3 = 0x0F00; // Sample time = 15Tad, Tad = Tcy
    AD1CHS = 0; //Configure input channel to AN0
    AD1CSSL = 0; //No inputs are scanned
    AD1CON1bits.ADON = 1; //Turn ADC on
}

int getADconv(void){
    
    unsigned int ADCValue;
    AD1CON1bits.SAMP = 1; // Start Sampling
    while(!AD1CON1bits.DONE); //Wait for conversion to be completed
    ADCValue = ADC1BUF0; //Store ADC value
    return ADCValue;
}
