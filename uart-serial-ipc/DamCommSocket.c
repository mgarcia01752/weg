
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <sys/types.h>
#include <time.h>

#include <wiringPi.h>
#include <wiringSerial.h>

/* DEFAULTS */
#define DEFAULT_SOCKET_PORT                   5000
#define MAX_TX_RX_BUFFER_LENGTH               50
#define DEFAULT_BAUD                          9600
#define DEFAULT_IPv4_LOOPBACK                 "127.0.0.1"
#define VERSION                               "1.1"
#define DEFAULT_UART_LOCATION                 "/dev/ttyS0"
#define UART_TX_TO_RX_DELAY                   1000
#define GPIO_TO_PIC_RESET					  4					/* Broadcom GPIO = 23 - WiringPI = 4 */

/* Error Codes */
#define ERROR_NONE                             0
#define ERROR_UNABLE_TO_OPEN_SERIAL_DEVICE    -1
#define ERROR_TO_START_WIRED_PI               -2
#define ERROR_CLI_ARG_MISSING_OPTION          -3

/* DEBUG */
#define DEBUG_ON                              TRUE  
#define DEBUG_OFF                             FALSE

/* Data Module Protocol */
#define GPS_COORDINATE                        "101"
#define UV_SENSOR                             "201"
#define TEMPERATURE_C                         "301"
#define TEMPERATURE_F                         "302"
#define BAROMETER                             "303"
#define SOLAR_POWER_VOLTAGE                   "400"
#define PIC_RESET							  "800:"
                                     


                    /* Function Declarations */

/*
**  Loads Array with Nulls
*/
void initBuffer(char *buf, size_t size);

/*
**  Prints STDOUT usage of Program
*/
void usage(void);

/*
**	String Carriage Returns and NewLine
*/
void strip_CR_NL(char *buf, size_t size);

/*
*
*/
void setResetToPIC();


/* DEBUG GLOBAL */
int bDebug = DEBUG_OFF;

int main(int argc, char * argv[]) {

    /* CLI Options */
    int bInputCLICheck = FALSE;
    
	int iLoopCliCount = 1;        //MUST BE = 1
    char *cLoopCliCount = '\0';
    
	int iBaudRate = DEFAULT_BAUD;
    char *cBaudRate;
	
	char *cSocketPort;
	int iSocketPort = DEFAULT_SOCKET_PORT;
    
    int listenfd = 0, connfd = 0 , n = 0;
    struct sockaddr_in serv_addr;

    char sendBuff[MAX_TX_RX_BUFFER_LENGTH];
    char caRxSocket[MAX_TX_RX_BUFFER_LENGTH];
    char caRxUart[MAX_TX_RX_BUFFER_LENGTH];

    int iOpt = 0;
    
    char * cInputCommand = '\0';

    /*http://www.gnu.org/software/libc/manual/html_node/Using-Getopt.html#Using-Getopt*/
    while ((iOpt = getopt(argc, argv, "p::i::l::b::T::GUBSrhvd::")) != -1) {

        switch (iOpt) {

          case 'p':
            cSocketPort = optarg;
            iSocketPort = atoi(cSocketPort);
            break;		
		
          case 'i':
            cInputCommand = optarg;
            bInputCLICheck = TRUE;
            break;
            
          case 'b':
            cBaudRate = optarg;
            iBaudRate = atoi(cBaudRate);
            break;
          
          case 'l':
            cLoopCliCount = optarg;
            iLoopCliCount = atoi(cLoopCliCount); 
            break;          
            
          case 'h':           
            usage();
            exit(ERROR_NONE);
          
          case 'd':           
            bDebug = DEBUG_ON;
            break;
          
          case 'v':
            cInputCommand = optarg;
            printf("\n\nVersion: %s\n\n", VERSION);
            exit(ERROR_NONE);
			
		  case 'r':
			printf("Reseting PIC via GPIO(23) Pin(%d) \n",GPIO_TO_PIC_RESET);     			
			setResetToPIC();			
			exit(ERROR_NONE);
			break;
            
          case 'G':
            printf("Get GPS Data\n");
            bInputCLICheck = TRUE;
            cInputCommand = GPS_COORDINATE;
			iLoopCliCount = 1;
            break;

          case 'U':
            printf("Get Ultra Violet Data\n");
            bInputCLICheck = TRUE;
            cInputCommand = UV_SENSOR;
            iLoopCliCount = 1;
			break;
            
          case 'B':
            printf("Get Barometer Data\n");
            bInputCLICheck = TRUE;
            cInputCommand = BAROMETER;
            iLoopCliCount = 1;
			break;
          
          case 'T':
		  
			printf("Get Temperature Data\n");
			cInputCommand = TEMPERATURE_F;
            bInputCLICheck = TRUE;
            iLoopCliCount = 1;
			break;
            
          case 'S':
            printf("Get Solar Data\n");
            bInputCLICheck = TRUE;
            cInputCommand = SOLAR_POWER_VOLTAGE;
            iLoopCliCount = 1;
			break;                        
            
          
          case '?':
  
              if (optopt == 'i') {
                  printf("\nMissing mandatory input option\n\n");
  
              } else {
                  usage();
              }
  
              exit(ERROR_CLI_ARG_MISSING_OPTION);
            
            default:
              /*nothing to see*/
              break;

        }
    }

    /****************************************************************************
     *  This section is setting up the socket for IPC interaction   
     ****************************************************************************/
    if (bInputCLICheck == FALSE) {
      
      printf("Starting Socket IPC -> use '-d' option for debuging\n");
    
      listenfd = socket(AF_INET, SOCK_STREAM, 0);
      memset( & serv_addr, '0', sizeof(serv_addr));
      memset(sendBuff, '0', sizeof(sendBuff));
  
      serv_addr.sin_family = AF_INET;
      
      /* Use all ALL IP Address found on PI*/
      serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
      
      /* Socket Port 5000 = DEFAULT */
      serv_addr.sin_port = htons(iSocketPort);
  
      bind(listenfd, (struct sockaddr * ) & serv_addr, sizeof(serv_addr));
  
      listen(listenfd, 10);
      
      if (bDebug) printf("Starting Socket IPC Listening on Port: %d\n", iSocketPort);
    }

    /****************************************************************************
     *            This section is setting up communicating to the UART 
     ****************************************************************************/

    int fd;

    if (bDebug) printf("Starting WiredPI API\n");
    
    /*Verify that WireingPI is Working*/
    if (wiringPiSetup() == -1) {
        fprintf(stdout, "Unable to start wiringPi: %s\n", strerror(errno));
        exit(ERROR_TO_START_WIRED_PI);
    }

    if (bDebug) printf("Opening UART Connection of Device: %s\n", DEFAULT_UART_LOCATION);

    /*Open Connection*/
    if ((fd = serialOpen(DEFAULT_UART_LOCATION, iBaudRate)) < 0) {
        fprintf(stderr, "Unable to open serial device: %s\n", strerror(errno));
        exit(ERROR_UNABLE_TO_OPEN_SERIAL_DEVICE);
    }

    if (bDebug) printf("Opened UART Connection of Device: %s\n", DEFAULT_UART_LOCATION);

    /****************************************************************************
     *             Start Socket monitoring or execute CLI 
     ****************************************************************************/

    while(!bInputCLICheck) {
    
        if (bDebug) printf("+-------------------------Listening Socket---------------------------+\n");
    
        /* Blocking - Waiting for Connection */
        connfd = accept(listenfd, (struct sockaddr*)NULL, NULL); 

        if (bDebug) printf("Incomming Connection Open\n");

        /* Clear Buffer */
        initBuffer(caRxSocket, sizeof(caRxSocket));

        /* Get Command from Client */
        while ((n = read(connfd, caRxSocket, sizeof(caRxSocket)-1)) > 0) {
          
          /* Strip CR and NL */
		  strip_CR_NL(caRxSocket, sizeof(caRxSocket));
          
          if (bDebug) printf("Socket -> UART -> (%s)\n" , caRxSocket);
          
		  /* end Reset to PIC */
		  if (strcmp(caRxSocket,PIC_RESET)) {
			  if (bDebug) printf("Sending PIC Reset Via Socket Command");
			  setResetToPIC();
			  continue;
		  }
		  
          /* Send command to UART */
          serialPuts(fd, caRxSocket);
          
          delay(UART_TX_TO_RX_DELAY);

          /*********************************************************************** 
          *         Get Result from UART and Concatenate -> caRxUart
          ************************************************************************/         
          int iIndex = 0;
          
          initBuffer(caRxUart, sizeof(caRxUart));
          while (serialDataAvail(fd)) {
          
            /* Load Charcaters into Array */
            caRxUart[iIndex++] =  serialGetchar(fd);
      
          }
          
          if (bDebug) printf("Response From UART -> Index: %d -> %s -> SizeOf: %d \n", iIndex , caRxUart,(int)strlen(caRxUart));
          
          /* Send back to Socket Client */
          write(connfd, caRxUart, strlen(caRxUart)); 
          
          if (bDebug) printf("UART -> Socket-> %s\n" , caRxUart);
          
          /* Close Connection */
          close(connfd);
          
          /* Wait a second */
          sleep(1);
        
        } 

     }

    /* If command is sent via CLI */
    if (bInputCLICheck) {
    
      while (iLoopCliCount>0) {
      
        printf("\nInput -> %s", cInputCommand);
     
	    /* Strip CR and NL */
		strip_CR_NL(cInputCommand, sizeof(cInputCommand));
	 
        serialPuts(fd, cInputCommand);
        fflush (stdout);
        
        delay(UART_TX_TO_RX_DELAY);
    
        printf(" Output -> ");
    
        while (serialDataAvail(fd)) {
            printf("%c", serialGetchar(fd));
            fflush(stdout);
        }
    
        printf("\n");
        
        iLoopCliCount--;
      }
      
      serialClose(fd);
      
      if (bDebug) printf("Closed UART Connection of Device: %s\n", DEFAULT_UART_LOCATION);
    }

    return 0;
}

/*
*	Init Array with NULL elements
*/
void initBuffer(char *buf, size_t size){
  int i;
  for(i=0; i<size; i++){
    buf[i] = '\0';     
  }
}

/*
*	Remove CR and NL from Character Array
*/
void strip_CR_NL(char *buf, size_t size) {

	int i = 0;
	
	for(i=0; i<size; i++){
		if ((buf[i] == '\r') || (buf[i] == '\n')) {
			
			if (bDebug) printf("FOUND CR or NL at Index (%d)\n",i);
			
			/*Replace with NULL*/
			buf[i] = '\0';
		}   
	}
}

/*
**	Send Reset to PIC Via GPIO
*/
void setResetToPIC() {
	
	wiringPiSetup () ;
      			
	/* Set Pin to Ouput mode */			
	digitalWrite(4,LOW);
	delay(UART_TX_TO_RX_DELAY);
	digitalWrite(4,HIGH);	
}

void usage(void) {
  
  printf("\n\n\nData Acquisition Module IPC Ver: %s\n"
         "Options are:\n"
             "\t-b: Set BaudRate <9600|115200> DEFAULT: 9600 \n"
             "\t-i: Serial Input <command>\n"
             "\t-l: Loop Input option <Number of Loops for option i>\n"
			 "\t-p: Socket Port DEFAULT = 5000\n"
			 "\t-r: Send Reset to PIC via GPIO 23\n"
             "\t-v: Version\n"
             "\t-d: Enable Debug\n"
             "\t-G: GPS Data\n"
             "\t-U: UltraViolet Data\n"
             "\t-T: Temperature Data <c|f> Default: Fahrenheit\n"
             "\t-B: Barometer Data\n"
             "\t-S: Solar Power Voltage Data\n"
             "\t-h: Usage an Exit\n\n\n\n",VERSION);

}