
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
#define DEFAULT_SOCKET_PORT       5000
#define MAX_TX_RX_BUFFER_LENGTH   50
#define DEFAULT_BAUD              115200
#define DEFAULT_IPv4_LOOPBACK     "127.0.0.1"
#define VERSION                   "1.0"
#define DEFAULT_UART_LOCATION     "/dev/ttyS0"

/* Error Codes */
#define ERROR_UNABLE_TO_OPEN_SERIAL_DEVICE  -1
#define ERROR_TO_START_WIRED_PI             -2
#define ERROR_CLI_ARG_MISSING_OPTION        -3  


/* Function Declarations */
void init_buf(char *buf, size_t size);
void usage(void);


int main(int argc, char * argv[]) {

    /* CLI Options */
    int bInputCLICheck = FALSE;
    int iLoopCliCount = 1;        //MUST BE = 1
    char *cLoopCliCount = '\0';
    
    int listenfd = 0, connfd = 0 , n = 0;
    struct sockaddr_in serv_addr;

    char sendBuff[MAX_TX_RX_BUFFER_LENGTH];
    char caRxSocket[MAX_TX_RX_BUFFER_LENGTH];
    char caRxUart[MAX_TX_RX_BUFFER_LENGTH];

    int iOpt = 0;

    char * cInputCommand = '\0';

    /*http://www.gnu.org/software/libc/manual/html_node/Using-Getopt.html#Using-Getopt*/
    while ((iOpt = getopt(argc, argv, "i:t::h::v::l:")) != -1) {

        switch (iOpt) {

          case 'i':
            cInputCommand = optarg;
            bInputCLICheck = TRUE;
            break;
          
          case 'l':
            cLoopCliCount = optarg;
            iLoopCliCount = atoi(cLoopCliCount); 
            break;          
            
          case 'h':           
            usage();
            exit(0);
          
          case 'v':
            cInputCommand = optarg;
            printf("\n\nVersion: %s\n\n", VERSION);
            exit(0);
          
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
      
      printf("Using Socket IPC\n");
    
      listenfd = socket(AF_INET, SOCK_STREAM, 0);
      memset( & serv_addr, '0', sizeof(serv_addr));
      memset(sendBuff, '0', sizeof(sendBuff));
  
      serv_addr.sin_family = AF_INET;
      
      /* Loopback Address */
      serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
      
      /* Socket Port 5000 */
      serv_addr.sin_port = htons(DEFAULT_SOCKET_PORT);
  
      bind(listenfd, (struct sockaddr * ) & serv_addr, sizeof(serv_addr));
  
      listen(listenfd, 10);
    }

    /****************************************************************************
     *            This section is setting up communicating to the UART 
     ****************************************************************************/

    int fd;

    /*Open Connection*/
    if ((fd = serialOpen(DEFAULT_UART_LOCATION, DEFAULT_BAUD)) < 0) {
        fprintf(stderr, "Unable to open serial device: %s\n", strerror(errno));
        return ERROR_UNABLE_TO_OPEN_SERIAL_DEVICE;
    }

    //printf("Open UART Connection\n");

    /*Verify that WireingPI is Working*/
    if (wiringPiSetup() == -1) {
        fprintf(stdout, "Unable to start wiringPi: %s\n", strerror(errno));
        return ERROR_TO_START_WIRED_PI;
    }

    /****************************************************************************
     *             Start Socket monitoring or execute CLI 
     ****************************************************************************/

    while(!bInputCLICheck) {
    
        printf("Listening Socket\n");
    
        /* Blocking - Waiting for Connection */
        connfd = accept(listenfd, (struct sockaddr*)NULL, NULL); 

        printf("Listening Socket-1\n");

        /* Clear Buffer */
        init_buf(caRxSocket, sizeof(caRxSocket));

        printf("Listening Socket-2\n");

        /* Get Command from Client */
        while ( (n = read(connfd, caRxSocket, sizeof(caRxSocket)-1)) > 0) {

          printf("Listening Socket-3\n");

          caRxSocket[n] = 0;
          if(fputs(caRxSocket, stdout) == EOF) {
              printf("\n Error : Fputs error\n");
          }
          
          printf("Sending To UART-1 -> %s" , caRxSocket);

          /* Send command to UART */
          serialPuts(fd, caRxSocket);
        
          delay(5);

          /*********************************************************************** 
          *         Get Result from UART and Concatenate -> caRxUart
          ************************************************************************/
          
          printf("Receiving From UART-1\n");
          
          int iIndex = 0;
          
          init_buf(caRxUart, sizeof(caRxUart));
          while (serialDataAvail(fd)) {
          
            /* Load Charcaters into Array */
            caRxUart[iIndex++] =  serialGetchar(fd);          
          }
          
          printf("Sending Socket-3 -> Index: %d -> %s -> SizeOf: %d \n", iIndex , caRxUart,(int)strlen(caRxUart));
          
          /* Send back to Socket Client */
          write(connfd, caRxUart, strlen(caRxUart)); 
          
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
        
        serialPuts(fd, cInputCommand);
  
        delay(3);
    
        printf(" Ouput -> ");
    
        while (serialDataAvail(fd)) {
            printf("%c", serialGetchar(fd));
            fflush(stdout);
        }
    
        printf("\n");
        
        iLoopCliCount--;
      }
      
    }

    return 0;
}

void init_buf(char *buf, size_t size){
  int i;
  for(i=0; i<size; i++){
    buf[i] = '\0';     
  }
}

void usage(void) {
  
  printf("\n\n\nData Aquaition Module IPC Ver: %s\n"
         "Options are:\n"
             "\t-i: Serial Input <command>\n"
             "\t-l: Loop Input option <Number of Loops for option i>\n"
             "\t-v: Version\n"
             "\t-h: Usage an Exit\n\n\n\n",VERSION);

}