/* 
 * File:   TPsensor.h
 * Author: John
 *
 * Created on July 15, 2016, 8:34 PM
 */

#ifndef TPSENSOR_H
#define	TPSENSOR_H

#define FCK  8000000ULL
#define FCY  4000000ULL
#define BMP180_READ 0xEF    //BMP180 device address + read
#define BMP180_WRITE 0xEE   //BMP180 device address + write
#define CNTRL 0xF4  //BMP180 control register address
#define TEMP 0x2E   //BMP180 initialize temperature reading
#define PRES 0x34   //BMP180 initialize pressure reading
#define DATA 0xF6   //BMP180 data register address


//Structure to hold calibration coefficients
 struct calib_data{
    int ac1;
    int ac2;
    int ac3;
    unsigned int ac4;
    unsigned int ac5;
    unsigned int ac6;
    int b1;
    int b2;
    int mb;
    int mc;
    int md;
};

//Addresses for calibration data
enum{
      BMP085_REGISTER_CAL_AC1            = 0xAA,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CAL_AC2            = 0xAC,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CAL_AC3            = 0xAE,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CAL_AC4            = 0xB0,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CAL_AC5            = 0xB2,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CAL_AC6            = 0xB4,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CAL_B1             = 0xB6,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CAL_B2             = 0xB8,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CAL_MB             = 0xBA,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CAL_MC             = 0xBC,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CAL_MD             = 0xBE,  // R   Calibration data (16 bits)
      BMP085_REGISTER_CHIPID             = 0xD0,
      BMP085_REGISTER_VERSION            = 0xD1,
      BMP085_REGISTER_SOFTRESET          = 0xE0,
      BMP085_REGISTER_CONTROL            = 0xF4,
      BMP085_REGISTER_TEMPDATA           = 0xF6,
      BMP085_REGISTER_PRESSUREDATA       = 0xF6,
      BMP085_REGISTER_READTEMPCMD        = 0x2E,
      BMP085_REGISTER_READPRESSURECMD    = 0x34
};

void init_I2C(void);
void readS16(char reg,long *value);
void read16(char reg, unsigned long *value);
void readCoefficients(struct calib_data *value);
char sendByte_I2C(int);
void reset_I2C(void);
void restart_I2C(void);
void start_I2C(void);
char read_I2C(void);
char read_ack_I2C(void);
unsigned char poll_I2C(char);
long getTemp(struct calib_data *value);
long getPressure(struct calib_data *value);
long getUT(void);
long getUP(void);
void readCast(char reg, unsigned int *value);
void readSCast(char reg, int *value);
void readSUP16(char reg, long *value);
void readUP16(char reg, unsigned long *value);


#endif	/* TPSENSOR_H */

