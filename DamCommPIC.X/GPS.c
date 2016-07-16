#define FCK  8000000ULL
#define FCY  4000000ULL

#include <libpic30.h>
#include <p24F16KA101.h>
#include "UART.h"
#include "GPS.h"

//Initialize GPS module by sending commands to module
void GPS_init(void){
    
    sendCommand(PMTK_SET_NMEA_OUTPUT_RMCONLY); //outputs only RMC sentences
    __delay_us(1000);  
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
