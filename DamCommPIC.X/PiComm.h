/* 
 * File:   PiComm.h
 * Author: John
 *
 * Created on July 15, 2016, 8:49 PM
 */

#ifndef PICOMM_H
#define	PICOMM_H

#define GPS_OK "150:OK\r\n"
#define GPS_ERROR "151:ERROR\r\n"
#define UV_OK "250:OK\r\n"
#define UV_ERROR "251:ERROR\r\n"
#define TP_OK "350:OK\r\n"
#define TP_ERROR "351:ERROR\r\n"
#define RECEIVE_ERROR "900:"

void sendPiCommand(char *str);
int getPiCommand(void);

#endif	/* PICOMM_H */

