#define FCK  8000000ULL
#define FCY  4000000ULL


#include <libpic30.h>
#include <xc.h>
#include <stdio.h>
#include "TPsensor.h"


//Initialize I2C module with SCL set to 100 kHz
void init_I2C(void){
    
    int temp;
    
    I2C1BRG = 39; // Sets SCL to 100 kHz
    I2C1CONbits.I2CEN = 0;
    I2C1CONbits.DISSLW = 1;
    IFS1bits.MI2C1IF = 0;
    I2C1CONbits.I2CEN = 1; //Enable I2C module
    temp = I2C1RCV;
    reset_I2C(); 
}

//Restarts I2C module
void restart_I2C(void){
    
    int x = 0;
    
    I2C1CONbits.RSEN = 1;
       
    while(I2C1CONbits.RSEN){
        
        __delay_us(1);
        x++;
        if(x > 20) break;
    }
    
    __delay_us(2);
}

//Initiates I2C start event
void start_I2C(void){
    
    int x = 0;
    I2C1CONbits.ACKDT = 0;
    __delay_us(10);
    I2C1CONbits.SEN = 1;
    
    while(I2C1CONbits.SEN){
        
        __delay_us(1);
        x++;
        if(x > 20) break;
    }
    __delay_us(2);
}

//Resets I2C module
void reset_I2C(void){
    
    int x = 0;
    
    I2C1CONbits.PEN = 1;
    
    while(I2C1CONbits.PEN){
        
        __delay_us(1);
        x++;
        if(x > 20) break; 
    }
    
    I2C1CONbits.RCEN = 0;
    IFS1bits.MI2C1IF = 0;
    I2C1STATbits.IWCOL = 0;
    I2C1STATbits.BCL = 0;
    __delay_us(10);
}

//Sends one byte via I2C
char sendByte_I2C(int data){
    
    int i;
    
    while(I2C1STATbits.TBF);
    IFS1bits.MI2C1IF = 0;
    I2C1TRN = data;
    
    for(i=0;i<500;i++){
        
        if(!I2C1STATbits.TRSTAT) break;
        __delay_us(1);
    }
        if(i==500){
            return(1);
        }
    if(I2C1STATbits.ACKSTAT == 1){
        
        reset_I2C();
        return(1);
    }
    
    __delay_us(2);
    return(0);
}

//Reads one byte via I2C
char read_I2C(void){
    
   int i = 0;
   char data = 0;

   //set I2C module to receive
   I2C1CONbits.RCEN = 1;

   //if no response, break
   while (!I2C1STATbits.RBF)
   {
      i ++;
      if (i > 2000) break;
   }

   //get data from I2CRCV register
   data = I2C1RCV;

   //return data
   return data;
}

//Reads one byte via I2C and returns an ACK
char read_ack_I2C(void)	
{
   int i = 0;
   char data = 0;

   //set I2C module to receive
   I2C1CONbits.RCEN = 1;

   //if no response, break
   while (!I2C1STATbits.RBF)
   {
      i++;
      if (i > 2000) break;
   }

   //get data from I2CRCV register
   data = I2C1RCV;

   //set ACK to high
   I2C1CONbits.ACKEN = 1;

   //wait before exiting
   __delay_us(10);

   //return data
   return data;
}

//Polls I2C device to ensure activity
unsigned char poll_I2C(char addr){
    
    unsigned char temp = 0;
    
    start_I2C();
    temp = sendByte_I2C(addr);
    reset_I2C();
    
    return temp;
}

//Reads signed integer over I2C 
void readSCast(char reg, int *value){
    
    unsigned int i;
    readCast(reg,&i);
    *value = (int)i;
}

//Reads unsigned integer over I2C
void readCast(char reg, unsigned int *value){
    
    
    read16(reg,(unsigned long*)value);
}

//Reads signed long value over I2C
void readS16(char reg,long *value){
    
    unsigned long i;
    read16(reg, &i);
    *value = (long)i;
}

//Reads unsigned long value over I2C
void read16(char reg, unsigned long *value){
    
    start_I2C();
    sendByte_I2C(BMP180_WRITE);
    sendByte_I2C(reg);
    restart_I2C();
    sendByte_I2C(BMP180_READ);
    *value = read_ack_I2C();
    *value = (*value << 8) + (read_I2C());
    reset_I2C();
    
}

//Calculate temperature in 0.1 degrees Celsius
long getTemp(struct calib_data *value){
    
    long X1,X2,B5,T;
    long UT = getUT();
    X1 = ((UT - (long)(value->ac6)) * (long)(value->ac5)) >> 15;
    X2 = ((long)(value->mc) << 11) / (X1+(long)(value->md));
    B5 = X1 + X2;
    T = (B5+8) >> 4;
    return T;
}

//Calculates pressure in Pascals
long getPressure(struct calib_data *value){
    
    long B6,X1,X2,X3,B3,P,B5;
    unsigned long B4,B7;
    long UT = getUT();
    long UP = getUP();
    
    X1 = ((UT - (long)(value->ac6)) * (long)(value->ac5)) >> 15;
    X2 = ((long)(value->mc) << 11) / (X1+(long)(value->md));
    B5 = X1 + X2;
    B6 = B5-4000;
    X1 = ((long)(value->b2)*((B6*B6) >> 12)) >> 11;
    X2 = ((long)(value->ac2) * B6) >> 11;
    X3 = X1 + X2;
    B3 = ((((long)(value->ac1) * 4 + X3) ) +2) / 4;
    X1 = ((long)(value->ac3) * B6) >> 13;
    X2 = ((long)(value->b1) * ((B6*B6) >> 12)) >> 16;
    X3 = ((X1+X2)+2)/4;
    B4 = (long)(value->ac4) * (unsigned long)(X3+32768) >> 15;
    B7 = ((unsigned long)UP - B3) * (50000);
    if(B7 < 0x80000000){
        P = (B7*2)/B4;
    }
    else{
        P = (B7/B4)*2;
    }
    X1 = (P >> 8) * (P >> 8);
    X1 = (long)(X1 *3038) >> 16;
    X2 = (long)(-7357 * P) >> 16;
    P = (P + ((X1+X2+3791) >> 4));
    return P;  
}

//Initializes temperature reading and gets data from BMP180
long getUT(void){
    
    long UT;
    start_I2C();
    sendByte_I2C(BMP180_WRITE);
    sendByte_I2C(CNTRL);
    sendByte_I2C(TEMP);
    reset_I2C();
    __delay_us(4500);
    readS16(DATA,&UT);
    return UT;
}

//Initializes pressure reading and gets data from BMP180
long getUP(void){
    
    long UP;
    start_I2C();
    sendByte_I2C(BMP180_WRITE);
    sendByte_I2C(CNTRL);
    sendByte_I2C(PRES);
    reset_I2C();
    __delay_us(5000);
    readSUP16(DATA, &UP);
    return UP;   
}

//Reads calibration values from BMP180
void readCoefficients(struct calib_data *value){
    
    readSCast(BMP085_REGISTER_CAL_AC1, &value->ac1);
    readSCast(BMP085_REGISTER_CAL_AC2, &value->ac2);
    readSCast(BMP085_REGISTER_CAL_AC3, &value->ac3);
    readCast(BMP085_REGISTER_CAL_AC4, &value->ac4);
    readCast(BMP085_REGISTER_CAL_AC5, &value->ac5);
    readCast(BMP085_REGISTER_CAL_AC6, &value->ac6);
    readSCast(BMP085_REGISTER_CAL_B1, &value->b1);
    readSCast(BMP085_REGISTER_CAL_B2, &value->b2);
    readSCast(BMP085_REGISTER_CAL_MB, &value->mb);
    readSCast(BMP085_REGISTER_CAL_MC, &value->mc);
    readSCast(BMP085_REGISTER_CAL_MD, &value->md);
}

//Reads signed long value from BMP180 for pressure reading
void readSUP16(char reg,long *value){
    
    unsigned long i;
    readUP16(reg, &i);
    *value = (long)i;
}

//Reads unsigned long value from BMP180 for pressure reading
void readUP16(char reg,  unsigned long *value){
    
    unsigned char msb,lsb,xlsb;
    start_I2C();
    sendByte_I2C(BMP180_WRITE);
    sendByte_I2C(reg);
    restart_I2C();
    sendByte_I2C(BMP180_READ);
    msb = read_ack_I2C();
    lsb = read_ack_I2C();
    xlsb = read_ack_I2C();
    *value = (((unsigned long) msb << 16) + ((unsigned long) lsb << 8) + (unsigned long) xlsb) >> 8;
    reset_I2C();    
}
//Calculates temperature in Fahrenheit
double getTempData(struct calib_data *value){
        
        long temp = getTemp(value);
        double Tdata = (double)temp;
        Tdata = ((Tdata/10) * 1.8) + 32; 
        
        return Tdata;
}
//Calculates pressure in millibars
unsigned int getPress(struct calib_data *value){
    
    unsigned int PreData;
    long press = getPressure(value);
    
    press = (press/100); 
    
    PreData = (unsigned int)press;
    return PreData;
}