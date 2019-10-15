#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <netdb.h> 
#include <arpa/inet.h> 
#include <stdbool.h>
#define BUFFERSIZE 256
#define ERRORMESSAGE "internal error \n"

/*THIS IS LITERALLY THE CODE FROM THE RPI TUTORIAL POSTED
ON THE SLIDES I MODIFIED IT SLIGHTLY TO FIT THE SPECIFICATIONS
OF THE PROJECT. I TRIED TO FOLLOW THE TUTORIAL EXACTLY, SO THIS CODE
IS FOR ALL INTENTS AND PURPOSES THE CODE FROM LECTURE.

SOURCE OF CODE:

http://www.cs.rpi.edu/~moorthy/Courses/os98/Pgms/socket.html
*/


typedef struct {
    char hostname[1024];
    char file[1024];
    char fileName[100];    
    bool help; 
    bool version; 
    //options
    int port;
    char user[1024];
    char password[1024];
    char mode[1024];
    char logfile[1024];
    char config_file[1024];
    int numThreads; 
    int threadNum;
    //option bools
    bool hasPort;
    bool hasUser;
    bool hasPassword;
    bool hasMode;
    bool hasLogfile;
    bool hasConfig_file;
    bool isThread; 
} parsedInput;

typedef struct{
  parsedInput * parsed;
  bool dataPipe; 
} threadedClientArgs; 

static void error(char *msg)
{
    perror(msg);
    exit(0);
}

void ftpControl(parsedInput* parsed, int sockfd);
char* createStringForFTP(char * str, char* command, char* input);
void parsePASV (char* str, int * values);
void * threadClient(void * input);
int client(parsedInput* parsed, bool control);
void checkError(char * buffer);
void writeToLog(char * file, char * string, char * buffer, bool hasLog);
void communicateWithServer(int sockfd, char * str, char * logfile, bool hasLog);
void communicateWithServerUpdateBuffer(int sockfd, char * str, char * logfile, bool hasLog, char * buffer);

void * threadClient(void * input){
    threadedClientArgs * args = (threadedClientArgs *) input;
    client(args->parsed, args->dataPipe);
    return NULL;
}

int client(parsedInput* parsed, bool control)
{
    int port = parsed->port; 
    char host[1024];
    strcpy(host, parsed->hostname);
    int sockfd, portno;
    struct sockaddr_in serv_addr;
    struct hostent *server;
    char buffer[BUFFERSIZE];
    portno = port;
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    
    if (sockfd < 0){
        error(ERRORMESSAGE);
    }
    server = gethostbyname(host);
    if (server == NULL) {
        //gethostbyname failed 
        
        fprintf(stderr,"0 Can't connect to server \n");
        exit(1);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr, 
         (char *)&serv_addr.sin_addr.s_addr,
         server->h_length);
    serv_addr.sin_port = htons(portno);
    bzero(buffer,BUFFERSIZE);
    //TCP
    if (connect(sockfd,(struct sockaddr *)&serv_addr,sizeof(serv_addr)) < 0) 
        error(ERRORMESSAGE);
    //enter FTP CONTROL PIPE
    if(control){
        ftpControl(parsed, sockfd);
    }
    //create FTP DATA PIPE 
    else{
        return sockfd; 
    }
    close(sockfd); 
    return 0; 
}



void ftpControl(parsedInput* parsed, int sockfd){
        const bool hasLog =  parsed->hasLogfile;
        int n;
        int size = 0; 
        char buffer[BUFFERSIZE];
        //INITIAL CONNECTION MESSAGE
        n = read(sockfd,buffer,(BUFFERSIZE-1)); 
        writeToLog (parsed->logfile, "S->C: %s", buffer, hasLog);
        //USER 
        char userStr[1024];
        createStringForFTP(userStr, "USER", parsed->user); 
        communicateWithServer(sockfd,userStr, parsed->logfile, hasLog);  
        //PASS
        char passStr[1024];
        createStringForFTP(passStr, "PASS", parsed->password); 
        communicateWithServer(sockfd,passStr, parsed->logfile, hasLog);  
        //PASV
        bzero(buffer,BUFFERSIZE);
        communicateWithServerUpdateBuffer(sockfd,"PASV\n", parsed->logfile, hasLog, buffer);  
        int values[6];
        parsePASV(buffer, values);
        int newPort = (values[4] * 256) + values[5];
        //OPEN DATA PIPE; 
        parsed->port = newPort;
        int dataPipe = client(parsed, false);
        //TYPE
        char typeStr[1024];
        createStringForFTP(typeStr, "TYPE", parsed->mode);
        communicateWithServer(sockfd,typeStr, parsed->logfile, hasLog);  
        //SIZE
        char sizeStr[1024];
        createStringForFTP(sizeStr, "SIZE", parsed->file);
        communicateWithServerUpdateBuffer(sockfd,sizeStr, parsed->logfile, hasLog, buffer);  
        //buffer+4 is to get past "213 "
        size = atoi(buffer+4);
        if (n < 0) 
             error(ERRORMESSAGE);
        //IF YOU ARE A THREAD, WHAT DATA WILL YOU GET
        int numBytes = 0;
        int startByte = 0;
        int endByte = 0;
        if (parsed->isThread){
            numBytes = size/(parsed->numThreads);
            startByte = numBytes * parsed->threadNum;
            endByte = 0;
            if (parsed->threadNum == parsed->numThreads){
                endByte = size; 
            }
            else{
                endByte = startByte + (numBytes-1); 
            }
            //REST  
            char restStr[1024];
            char startByteString[10];
            sprintf(startByteString, "%d", startByte);
            createStringForFTP(restStr, "REST", startByteString);
            communicateWithServer(sockfd,restStr, parsed->logfile, hasLog);              
        }
        //RETR
        char retrStr[1024];
        createStringForFTP(retrStr, "RETR", parsed->file);
        communicateWithServer(sockfd,retrStr, parsed->logfile, hasLog);              
        //READ FILE FROM DATA PIPE UNTIL CLOSED
        void * voidBuffer[BUFFERSIZE];
        int bytesReceived = recv(dataPipe, voidBuffer, 255, MSG_WAITALL);
        FILE * fp;
        if(parsed->isThread){
            char ThreadFile[1024];
            sprintf(ThreadFile, "%d", parsed->threadNum);
            strcat(ThreadFile,  parsed->fileName);
            fp = fopen ( ThreadFile,"w");
            int currByte = startByte;
            while(bytesReceived){
                currByte+=bytesReceived;
                //writing the last bytes to the file
                if(currByte > endByte){
                   int lastBytesToWrite = (endByte - (currByte-bytesReceived));
                   fwrite(voidBuffer, 1, lastBytesToWrite, fp);
                   break;
                }
                fwrite(voidBuffer, 1, bytesReceived, fp);
                bzero(voidBuffer,BUFFERSIZE);
                bytesReceived = recv(dataPipe, voidBuffer, 255, MSG_WAITALL);
            }
        }
        else{
            fp = fopen ( parsed->fileName,"w");
            while(bytesReceived){
                fwrite(voidBuffer, 1, bytesReceived, fp);
                bzero(voidBuffer,BUFFERSIZE);
                bytesReceived = recv(dataPipe, voidBuffer, 255, MSG_WAITALL);
            }
            communicateWithServer(sockfd,"QUIT\n", parsed->logfile, hasLog);  
        }
        //CLOSE CONTROL SOCKET
        fclose(fp);
        close(dataPipe);
        close(sockfd);
        
}


char* createStringForFTP(char * str, char* command, char* input){
    bzero(str,1024);
    strcat(str, command);
    strcat(str, " ");
    strcat(str, input);
    strcat(str, "\n");
    return str;   
}

void writeToLog(char * logfile, char * string, char * buffer, bool hasLog){
    if(hasLog){
        FILE * logfileFILE = fopen (logfile,"ab");
        fprintf (logfileFILE, string, buffer);
        fclose(logfileFILE);
    }
}

void checkError(char * buffer){
    char bufferCopy[1024];
    strcpy(bufferCopy,buffer);
    char * errorCode2 = strtok(bufferCopy, " ");
    int code = atoi(errorCode2);
    if (code == 530){
        fprintf(stderr,"2 ERROR: Authentication failed\n");
        exit(2);
    }
    if (code == 550){
            fprintf(stderr,"3 ERROR: File not found\n");
            exit(3);
    }
    if (code == 500){
        fprintf(stderr,"4 ERROR: Syntax Error, Command Not Recognized\n");
        exit(4);
    }
    if ((code == 202) || (code == 504)){
        fprintf(stderr,"5 ERROR: Syntax Error, Command not implemented by server\n");
        exit(5);
    }

}

void communicateWithServer(int sockfd, char * str, char * logfile, bool hasLog){
    int n;
    char buffer[BUFFERSIZE];
    n = write(sockfd,str,strlen(str));
    writeToLog (logfile, "C->S: %s", str, hasLog);
    if (n < 0) 
         error(ERRORMESSAGE);
    bzero(buffer,BUFFERSIZE);
    n = read(sockfd,buffer,(BUFFERSIZE-1));
    writeToLog (logfile, "S->C: %s", buffer, hasLog);
    checkError(buffer);
    if (n < 0) 
         error(ERRORMESSAGE);
}

void communicateWithServerUpdateBuffer(int sockfd, char * str, char * logfile, bool hasLog, char * buffer){
    int n;
    n = write(sockfd,str,strlen(str));
    writeToLog (logfile, "C->S: %s", str, hasLog);
    if (n < 0) 
         error(ERRORMESSAGE);
    bzero(buffer,BUFFERSIZE);
    n = read(sockfd,buffer,(BUFFERSIZE-1));
    writeToLog (logfile, "S->C: %s", buffer, hasLog);
    checkError(buffer);
    if (n < 0) 
         error(ERRORMESSAGE);
}

//this function is just to parse the response from
//sending the PASV command
void parsePASV (char* str, int * values)
{
    int i = 0; 
    char *pt;
    pt = strtok (str,",");
    while (pt != NULL) {
        if(i==0){
            char * strAdj = pt + (strlen(pt) - 3);
            pt = strAdj;
        }
        else if (i == 5){
            char *p = strchr(pt, ')');
            *p = 0;
        }
        int a = atoi(pt);
        values[i] = a;
        pt = strtok (NULL, ","); 
        i++;
    }
    return;
}

