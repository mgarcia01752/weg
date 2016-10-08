/* 
 * File:   UART.h
 * Author: John
 *
 * Created on July 15, 2016, 8:34 PM
 */

#ifndef UART_H
#define	UART_H

void UART1_init(unsigned long baud);
void UART2_init(unsigned long baud);
void UART1_PutChar(char ch);
char UART1_GetChar(void);
void UART2_PutChar(char ch);
char UART2_GetChar(void);
void sendArray(char *str);

#endif	/* UART_H */

