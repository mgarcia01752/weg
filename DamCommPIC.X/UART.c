#define FCK  8000000ULL
#define FCY  4000000ULL

#include <libpic30.h>
#include <p24F16KA101.h>


//Initializes UART1 module for communication with GPS
void UART1_init(unsigned long baud){
    
    U1BRG = ((FCY / baud) / 16)-1; //Baud rate calculation
    U1MODEbits.UARTEN = 0;  //Disable module
    U1MODE = 0; //8-bit data,no parity, one stop bit
    U1STA = 0;  //Reset status register
    IEC0bits.U1RXIE = 0; //disable receive interrupt
    IFS0bits.U1RXIF = 0; //clear interrupt flag
    IPC2bits.U1RXIP = 6; //set interrupt priority
    U1MODEbits.UARTEN = 1; //Enable module
    U1STAbits.UTXEN = 1;  //Enable transmission
}

//Initializes UART2 module for communication with RPi
void UART2_init(unsigned long baud){
    
    U2BRG = ((FCY / baud) / 16)-1; //Baud rate calculation
    U2MODEbits.UARTEN = 0; //Disable module
    U2MODE = 0; //8-bit data,no parity, one stop bit
    U2STA = 0;  //Reset status register
    IEC1bits.U2RXIE = 0; //disable receive interrupt
    IFS1bits.U2RXIF = 0; //clear interrupt flag
    IPC7bits.U2RXIP = 5; //set interrupt priority
    U2MODEbits.UARTEN = 1; //Enable module
    U2STAbits.UTXEN = 1; //Enable transmission
}

//Sends one byte via UART1 module
void UART1_PutChar(char ch){
    
    while(U1STAbits.UTXBF != 0);
    U1TXREG = ch;
}

//Sends one byte via UART2 module
void UART2_PutChar(char ch){
    
    while(U2STAbits.UTXBF == 1);
    U2TXREG = ch;
}

//Receives one byte via UART1 module
char UART1_GetChar(void){
    
    char temp;  
    if(U1STAbits.OERR == 1){
        U1STAbits.OERR = 0;
    }
    while(U1STAbits.URXDA == 0);
    temp = U1RXREG;
    return temp;
}

//Receives one byte via UART2 module
char UART2_GetChar(void){
    
    char temp; 
    if(U2STAbits.OERR == 1){
        U2STAbits.OERR = 0;
    }
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
