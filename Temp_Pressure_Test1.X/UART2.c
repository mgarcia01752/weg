#define FCK  8000000ULL
#define FCY  4000000ULL


#include <libpic30.h>
#include <p24F16KA101.h>

//Initialize UART2 module for RPi communication
void UART2_init(unsigned int baud){
    
    U2BRG = ((FCY / baud) / 16)-1;  //Baud rate calculation
    U2MODEbits.UARTEN = 0;  //Disable module
    U2MODE = 0; //8-bit data,no parity, one stop bit
    U2STA = 0;  //Reset status register
    U2MODEbits.UARTEN = 1; //Enable module
    U2STAbits.UTXEN = 1; //Enable transmission
}

//Sends byte via UART2 module
void UART2_PutChar(char ch){
    
    //U2STAbits.UTXEN = 1;
    while(U2STAbits.UTXBF == 1);
    U2TXREG = ch;
    //while(U2STAbits.TRMT == 0);
}

//Receives byte via UART2 module
char UART2_GetChar(void){
    
    char temp;
    
    while(U2STAbits.URXDA == 0);
    
    temp = U2RXREG;
    return temp;
}

//Sends array ending in '\n' via UART2 module
void sendArray(char *str){
    
    char i;
    
    do{
        UART2_PutChar(*str);
        i = *str;
        str++;
    }while(i != '\n');
}
