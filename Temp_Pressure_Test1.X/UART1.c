#define FCK  8000000ULL
#define FCY  4000000ULL


void UART1_init(unsigned int baud){
    
    U1BRG = ((FCY / baud) / 16)-1;
    //U1MODE = 0x8000;
    U1MODEbits.UARTEN = 0;
    U1MODE = 0; //8-bit data,no parity, one stop bit
    U1STA = 0;
    U1MODEbits.UARTEN = 1;
    U1STAbits.UTXEN = 1;
}

void UART2_PutChar(char ch){
    
    //U1STAbits.UTXEN = 1;
    while(U1STAbits.UTXBF != 0);
    U1TXREG = ch;
    //while(U1STAbits.TRMT == 0);
}

char UART2_GetChar(void){
    
    char temp;
    
    while(U1STAbits.URXDA == 0);
    
    temp = U1RXREG;
    return temp;
}


