/* 
 * File:   UART1.h
 * Author: John
 *
 * Created on July 5, 2016, 9:06 PM
 */

#ifndef UART1_H
#define	UART1_H

void UART1_init(unsigned int baud);
void UART1_PutChar(char ch);
char UART1_GetChar(void);

#endif	/* UART1_H */

