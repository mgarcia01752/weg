/* 
 * File:   PiComm.h
 * Author: John
 *
 * Created on July 15, 2016, 8:49 PM
 */

#ifndef PICOMM_H
#define	PICOMM_H

#define GPS_OK "150:OK\0"
#define GPS_ERROR "151:ERROR\0"
#define UV_OK "250:OK\0"
#define UV_ERROR "251:ERROR\0"
#define TP_OK "350:OK\0"
#define TP_ERROR "351:ERROR\0"

void sendPiCommand(char *str);
int getPiCommand(void);

#endif	/* PICOMM_H */

