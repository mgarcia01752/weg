#define FCK  8000000ULL
#define FCY  4000000ULL

#include <libpic30.h>
#include <p24F16KA101.h>
#include <string.h>
#include <stdio.h>
#include "UART.h"
#include "GPS.h"

//Initialize GPS module by sending commands to module
void GPS_init(void){
    
    sendCommand(PMTK_SET_NMEA_OUTPUT_RMCGGA); //outputs only RMC sentences
    __delay_us(1000);  
}

void getGPSsentence(char *GPS_String){
    
    char i;
    char GPS_data[255];
    char *value;
    value = GPS_data;
    do{
    i = UART1_GetChar();
    }while(i != '$');
      
    *value = i;
    
    do{
        value++;
        *value = UART1_GetChar();
    }while(*value != '\n');   

    value = GPS_data;
    sprintf(GPS_String,"150:%s",value);
    
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
