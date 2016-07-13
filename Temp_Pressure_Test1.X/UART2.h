/* 
 * File:   UART2.h
 * Author: John
 *
 * Created on July 5, 2016, 9:10 PM
 */

#ifndef UART2_H
#define	UART2_H

void UART2_init(unsigned int baud);
void UART2_PutChar(char ch);
char UART2_GetChar(void);
void sendArray(char *str);

#endif	/* UART2_H */

