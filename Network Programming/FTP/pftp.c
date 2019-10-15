#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <string.h>
#include <pthread.h>
#include <getopt.h>
#include "client.h" 
#define USEERROR "invalid or missing options \n usage: snc [­l] [­u] [hostname] port"
#define HELPMESSAGE "\n This is a program for downloading a file from an ftp server\n\n -s or --hostname [hostname] \n -f or --file [file] \n -h or --help [help] \n -v or --version [version] \n -p or --port [port] \n -n or --username [username] \n -P or --password [password] \n -t or --thread [config_file] \n -m or --mode [mode] \n -l or --logfile [logfile]\n\n"
#define VERSIONMESSAGE "\n Application: pftp \n Author: Alek Binion \n Version: 0.1 \n"
void parseInput(parsedInput* parsed, int argc, char *argv[]);
void parseURLFromHostName (parsedInput* parsed);
void parseConfigFile(char * config_file_string, parsedInput * parsedArr, int * parsedArrLen); 
void parseInputLong(parsedInput* parsed, int argc, char *argv[]);


void error(char *msg)
{
    perror(msg);
    exit(1);
}

//GOOD TESTS
//./pftp -s ftp://mirror.keystealth.org/gnu/ -f /ProgramIndex
//./pftp -s ftp1.cs.uchicago.edu -f /rfc959.pdf -n cs23300 -P youcandoit
//./pftp -s ftp2.cs.uchicago.edu -f /rfc959.pdf -n sockprogramming -P rocks
//./pftp -t config_files/cf1

int main(int argc, char *argv[])
{
	parsedInput parsed;
	parseInputLong(&parsed,argc,argv);
  //help
  if(parsed.help){
    printf(HELPMESSAGE);
    return 0;
  }
  //version
  else if(parsed.version){
    printf(VERSIONMESSAGE);
    return 0;
  }
  //parallel download code
  else if(parsed.hasConfig_file){
    int parsedArrLen = 0;
    parsedInput parsedArr[1024];  
    parseConfigFile(parsed.config_file, parsedArr, &parsedArrLen);
    int i = 0;
    for(i = 0; i < parsedArrLen; i++){
      //peroforming last setup o
      parsedArr[i].threadNum = i; 
      parsedArr[i].numThreads = parsedArrLen;
      parsedArr[i].hasLogfile = parsed.hasLogfile;
      strcpy(parsedArr[i].logfile, parsed.logfile);
      parseURLFromHostName(&parsedArr[i]);
    }

    pthread_t t [parsedArrLen];
    //get the file from multiple threads
    for(i = 0; i < parsedArrLen; i++){
      threadedClientArgs args;
      args.parsed = &parsedArr[i];
      args.dataPipe = true;
      pthread_create(&t[i], NULL, threadClient, (&args));
      sleep(1); 
    } 
    //join the threads
    for(i=0; i < parsedArrLen; i++){
       (void) pthread_join(t[i], NULL);
    }
    //merge the temporary files created from threads into file
    FILE * final = fopen(parsedArr[0].fileName, "w"); 
    for(i = 0; i < parsedArrLen; i++){
      char ThreadFile[1024];
      sprintf(ThreadFile, "%d", i);
      strcat(ThreadFile,  parsedArr[i].fileName);
      FILE * fileToMerge = fopen ( ThreadFile,"r");
      //this file copy code is from https://stackoverflow.com/questions/5263018/copying-binary-files
      size_t n, m;
      unsigned char buff[8192];
      do {
          n = fread(buff, 1, sizeof buff, fileToMerge);
          if (n) m = fwrite(buff, 1, n, final);
          else   m = 0;
      } while ((n > 0) && (n == m));
      if (m) perror("copy");
      //delete temporary file
      remove(ThreadFile);
      if (fclose(fileToMerge)) perror("close output file");
    }
    if (fclose(final)) perror("close output file");
    return 0;
  }
  //standard
  else{
    parseURLFromHostName(&parsed);
    client(&parsed, true);
  }

	return 0;
}

void parseInputLong(parsedInput* parsed, int argc, char *argv[]){
   parsed->port = 21;
   strcpy(parsed->user, "anonymous");
   strcpy(parsed->password, "user@localhost.localnet");
   strcpy(parsed->mode, "I");
   //assign all bool values to false immediately
   parsed->help = false;
   parsed->version = false; 
   parsed->hasPort = false;
   parsed->hasUser = false;
   parsed->hasPassword = false;
   parsed->hasMode = false;
   parsed->hasLogfile = false;
   parsed->hasConfig_file = false;
   parsed->isThread = false; 
   static struct option long_options[] =
        {
          {"help",               no_argument,       0, 'h'},
          {"version",            no_argument,       0, 'v'},
          {"file",               required_argument, 0, 'f'},
          {"hostname",           required_argument, 0, 's'},
          {"port",               required_argument, 0, 'p'},
          {"username",           required_argument, 0, 'n'},
          {"password",           required_argument, 0, 'P'},
          {"mode",               required_argument, 0, 'm'},
          {"thread", required_argument, 0, 't'},
          {"logfile",            required_argument, 0, 'l'},
          {"h",  no_argument,       0, 'h'},
          {"v",  no_argument,       0, 'v'},
          {"f",  required_argument, 0, 'f'},
          {"s",  required_argument, 0, 's'},
          {"p",  required_argument, 0, 'p'},
          {"u",  required_argument, 0, 'n'},
          {"P",  required_argument, 0, 'P'},
          {"m",  required_argument, 0, 'm'},
          {"t",  required_argument, 0, 't'},
          {"l",  required_argument, 0, 'l'},
          {0, 0, 0, 0}
        };
  int c;
  int option_index = 0;
  while((c = getopt_long (argc, argv, "hvf:s:p:m:s:t:l:",
                       long_options, &option_index))){

    if (c == -1)
        break;

    switch (c)
        {
        case 0:
          /* If this option set a flag, do nothing else now. */
          if (long_options[option_index].flag != 0)
            break;
          printf ("option %s", long_options[option_index].name);
          if (optarg)
            printf (" with arg %s", optarg);
          printf ("\n");
          break;
        case 's':
            strcpy(parsed->hostname, optarg);
            break;
        case 'f':
            strcpy(parsed->file, optarg);
            strcpy(parsed->fileName, optarg + 1);
            
            break;
        case 'h':
            parsed->help = true;
            break;
        case 'v':
            parsed->version = true;
            break;
          //options parsing
          case 'p':
            parsed->port = atoi(optarg);
            parsed->hasPort = true; 
            break;
          case 'n':
            strcpy(parsed->user, optarg);
            parsed->hasUser = true; 
            break;
          case 'P':
            strcpy(parsed->password, optarg);
            parsed->hasPassword = true; 
            break;
          case 'm':
            strcpy(parsed->mode, optarg);
            parsed->hasMode = true; 
            break;
          case 'l':
            strcpy(parsed->logfile, optarg);
            parsed->hasLogfile = true; 
            break;
          case 't':
            strcpy(parsed->config_file, optarg);
            parsed->hasConfig_file = true; 
            break;        
          default:
           fprintf(stderr,"7 ERROR: OPTION INCORRECt\n");
           exit(7);
           return;
        } //end block for switch
      }
}

//updated parseInput For p2
void parseInput(parsedInput* parsed, int argc, char *argv[]){
   int option_index = 0;
   //assign default values
   parsed->port = 21;
   strcpy(parsed->user, "anonymous");
   strcpy(parsed->password, "user@localhost.localnet");
   strcpy(parsed->mode, "I");
   //assign all bool values to false immediately
   parsed->help = false;
   parsed->version = false; 
   parsed->hasPort = false;
   parsed->hasUser = false;
   parsed->hasPassword = false;
   parsed->hasMode = false;
   parsed->hasLogfile = false;
   parsed->hasConfig_file = false;
   parsed->isThread = false; 
   while (( option_index = getopt(argc, argv, "s:f:hvp:n:P:m:l:t:")) != -1){
   switch (option_index) {
	 case 's':
       strcpy(parsed->hostname, optarg);
       break;
	 case 'f':
       strcpy(parsed->file, optarg);
       strcpy(parsed->fileName, optarg + 1);
       
       break;
 	 case 'h':
       parsed->help = true;
       break;
 	 case 'v':
       parsed->version = true;
       break;
     //options parsing
     case 'p':
       parsed->port = atoi(optarg);
       parsed->hasPort = true; 
       break;
     case 'n':
       strcpy(parsed->user, optarg);
       parsed->hasUser = true; 
       break;
     case 'P':
       strcpy(parsed->password, optarg);
       parsed->hasPassword = true; 
       break;
     case 'm':
       strcpy(parsed->mode, optarg);
       parsed->hasMode = true; 
       break;
     case 'l':
       strcpy(parsed->logfile, optarg);
       parsed->hasLogfile = true; 
       break;
     case 't':
       strcpy(parsed->config_file, optarg);
       parsed->hasConfig_file = true; 
       break;        
     default:
      fprintf(stderr,"7 ERROR: OPTION INCORRECt\n");
      exit(7);
      return;
     } //end block for switch
   }  //end block for while

   return;
}

//BROKEN  
void parseURLFromHostName (parsedInput* parsed)
{   
    char url[1024];
    strcpy(url, parsed->hostname); 
    char urlBeginning[7];
    strncpy(urlBeginning, url, 6); 
    urlBeginning[6] = 0; 
    char domain[1024];
    char filePath[1024];
    bool hasFilePath = false;

    
    if (!strcmp(urlBeginning, "ftp://")){
        //parse with ftp://
        sscanf(url, "ftp://%[^/]", domain);
        strcpy(filePath, url+6+strlen(domain));
        hasFilePath = true;
    }
    else{
        //parse without ftp://
        char * pt;
        pt = strchr(url, '/');
        strcpy(domain,url);
        if(pt != NULL){
            domain[strlen(domain) - strlen(pt)] = 0;
            strcpy(filePath, pt);
            hasFilePath = true; 
        }
    }
    strcpy(parsed->hostname, domain);
    if (hasFilePath){
        strcat(filePath, parsed->file);
        strcpy(parsed->file,filePath);
        //this is to parse the filenames in threads 
        if(parsed->isThread){
          //get rid of / and \n
          strcpy(parsed->fileName,parsed->file + 1);
          if (parsed->fileName[(strlen(parsed->fileName)-1)] == '\n'){
            parsed->fileName[(strlen(parsed->fileName)-1)] = 0;
          }
        }
    }
    return;
}

void parseConfigFile(char * config_file_string, parsedInput * parsedArr, int * parsedArrLen){
    FILE * config_file;
    char * line = NULL;
    size_t len = 0;
    ssize_t read;
    int index = 0;
    config_file = fopen(config_file_string, "r");
    if (config_file == NULL)
        exit(EXIT_FAILURE);
    while ((read = getline(&line, &len, config_file)) != -1) {
        char * line_adj = line + 6;
        char s[2][2] = {{":"},{"@"}};
        char *token;
        char stringForParser[1024]; 
        bzero(stringForParser, 1024);
        parsedInput parsed;
        token = strtok(line_adj, s[0]);
        strcpy(parsed.user, token);
        int tok = 1; 
        while( token != NULL ) {
           token = strtok(NULL, s[tok]);
           if(tok == 1){
              strcpy(parsed.password, token);
           }
           if(tok == 2){
              strcpy(parsed.hostname, token);
           }

           tok++;
           if (tok>2){
             break;
           }
       }
       parsed.port = 21;
       //"I" is binary
       strcpy(parsed.mode, "I");
       parsed.isThread = true; 
       parsedArr[index] = parsed;
       index++;
    }
    fclose(config_file);
    if (line)
        free(line);
    * parsedArrLen = index;
}

