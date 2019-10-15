#ifndef _CLIENT_H_
#define _CLIENT_H_

#include <stdbool.h>

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

int client(parsedInput* parsed, bool control);
void ftpControl(parsedInput* parsed, int sockfd);
void * threadClient(void * input);



#endif