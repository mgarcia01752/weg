#include "UART.h"
#include <p24F16KA101.h>
#include <libpic30.h>
#include <stdlib.h>

//Sends commands to RPi via UART2 module
void sendPiCommand(char *str){
    
    char i;
    
    do{
        UART2_PutChar(*str);
        i = *str;
        str++;
    }while(i != '\0');
}
