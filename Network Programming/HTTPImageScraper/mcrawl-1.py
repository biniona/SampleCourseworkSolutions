import re
import socket
import os
import queue
import threading 
import time
import sys, getopt

from parseLinks import create_dir, getCookie, returnCode, getContentType
from urlSocketFunctions import parseURLToHostNameAndFilePath, createSocket


def main(argv):
    #main code 

    #declare global variables  
    global qLock, fileNameLock, cookieLock, THREAD_NUM, server, port, dirName, crawled, q, localDirectoryName
    global threadsCookie
    #this is to test if stuff is actually assigned in opt args
    optFlagsArr = [False,False,False,False]
    #get opt args
    try:
       opts, args = getopt.getopt(argv,"n:h:p:f:")
    except getopt.GetoptError:
       print('mcrawl-1.py -n [ -n max-flows ] [ -h hostname ] [ -p port ] [-f local- directory]')
       sys.exit(2)
    for opt, arg in opts:
        if opt == '-n':
           optFlagsArr[0] = True
           THREAD_NUM = int(arg)
        elif opt in ("-h", "--ifile"):
           optFlagsArr[1] = True
           server = arg
        elif opt in ("-p", "--ofile"):
           optFlagsArr[2] = True
           port = int(arg)
        elif opt in ("-f", "--ofile"):
           optFlagsArr[3] = True
           localDirectoryName = arg
    
    if False in optFlagsArr:
        print('mcrawl-2.pt -n [ -n max-flows ] [ -h hostname ] [ -p port ] [-f local- directory]')
        sys.exit(2)

    #assign global variables
    qLock = threading.Lock()
    fileNameLock = threading.Lock()
    cookieLock = threading.Lock()
    dirName = create_dir(localDirectoryName)
    crawled = list()
    q = queue.Queue()
    crawled.append("index.html")

    #local testing assignments

    #localDirectoryName = server
    #THREAD_NUM = 10
    #server = 'eychtipi.cs.uchicago.edu'
    #localDirectoryName = server
    #port = 80
    
    s = createSocket(server,port)
    if s is None:
        print("Bad Hostname")
        sys.exit(2)
    threadsCookie = crawl_links(server, "index.html", s, dirName, q, crawled, "NONE", 0)
    s.close()
    threads = []
    workingThreads = []
    for i in range(0, THREAD_NUM):
        t = threading.Thread( name = "thread{}".format(THREAD_NUM) ,target = parse, args = (workingThreads, i))
        threads.append(t)
        workingThreads.append(True)
        t.start()
        #print("thread {} has started".format(i))
    counter = 0
    for thread in threads:
        thread.join()
        #print("thread {} is done".format(counter))
        counter+=1
    #print("DONE!!!!! Queue empty: {}".format(q.empty()))

def parse(workingThreads, threadNum):
    global threadsCookie, q, server, dirName, crawled, port
    while True:
        while not q.empty():
            #print("THREAD_NUM: {} COOKIE: {}".format(threadNum, cookie))
            line = None
            with qLock:
                line = q.get()
                line = "".join(line.split())
                crawled.append(line)
    
            parsed = parseURLToHostNameAndFilePath(line)
            if(parsed[0] == 1):
                if parsed[1] == server:
                    newSocket = createSocket(server, port)
                    if (newSocket != None):
                        
                        newCookie = crawl_links(server, parsed[2], newSocket, dirName, q, crawled, threadsCookie, threadNum)
                        with cookieLock:
                            threadsCookie = newCookie
                        newSocket.close()
    
            if (parsed[0] == 2):
                newSocket = createSocket(server, port)
                if (newSocket != None):
                    newCookie = crawl_links(server, parsed[2], newSocket, dirName, q, crawled, threadsCookie, threadNum)
                    with cookieLock:
                        threadsCookie = newCookie
                    newSocket.close()
            
        workingThreads[threadNum] = False    
        #print("thread {} waiting for job...".format(threadNum))
        time.sleep(3)
        if True not in workingThreads:
            break
        workingThreads[threadNum] = True 

    #print("THREAD IS DEAD!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")


def parse_links(document, q, crawled):
    i = 0
    match = list()
    if ".html" in document:
        with open(document) as fh:
            for line in fh:
                i+=1
                match.append(re.findall('src="(.+?)"', line))
                match.append(re.findall('(?:href|HREF)="(.+?)"', line))
        for line in match:
            for li in line:
                li = "".join(li.split())
                with qLock:
                    if li in q.queue:
                        continue
                    elif li in crawled:
                        continue
                    else:
                        q.put(li)
        

def crawl_links (server, subDirectory, socket, subDir, q, crawled, cookie, threadNum):
    HTTP11 = False 
    subDirectory = "".join(subDirectory.split())
    if (cookie == "NONE"):
        #request = "GET /"+subDirectory+" HTTP/1.1\r\nHost: "+server+"\r\n\r\n"
        request = "GET /"+subDirectory+" HTTP/1.0\r\nHost: "+server+"\r\n\r\n"
    else:
        #request = "GET /"+subDirectory+" HTTP/1.1\r\nHost: "+server+"\r\nCookie: "+cookie+"\r\n\r\n"
        request = "GET /"+subDirectory+" HTTP/1.0\r\nHost: "+server+"\r\nCookie: "+cookie+"\r\n\r\n"
    with fileNameLock:
        fileName = subDirectory.split('/')[-1]
        originalFileName = fileName
        filePath = subDir + fileName
        i = 0;
        while os.path.isfile(filePath):
            split = originalFileName.split(".")
            adj = split[0]+"-"+str(i)
            split[0] = adj
            fileName = ".".join(split)
            filePath = subDir + fileName
            i+=1
    socket.send(request.encode())
    try:
        f = None
        contentType = None
        nextPos = 0 
        result = b''
        newResult = b' '
        recvSize = 4096
        try:
            #print("waiting for result thread{}...".format(threadNum))
            while((len(newResult) > 0)):

                newResult = socket.recv(recvSize)
                result += newResult
            #print("Got result in thread{}!".format(threadNum))
        except TimeoutError:
            #print("TIME OUT ERROR IN CRAWL_LINKS")
            return cookie
        except ConnectionResetError:
            #print("CONNECTION ERROR IN CRAWL_LINKS")
            return cookie
        if not HTTP11:
            originalLen = len(result)
            result_list = result.split(b'\r\n\r\n',1)
            header = result_list[0].decode('utf-8')
            contentType = getContentType(header)
            
            if (len(result_list) > 1):
                result = result_list[1]
            else:
                #print("EMPTY TRANSMISSION")
                print(result)             
                return "NONE"
            oldCookie = cookie
            cookie = getCookie(header)
            code = returnCode(header)
            if(code == 402):
                #print("RATE LIMITED")
                with qLock:
                    q.put(subDirectory)              
                return "NONE"
            if((len(result)== 0)):
                #print("EMPTY")
                with qLock:
                    q.put(subDirectory)              
                return "NONE"
            #print("SUCCESS!")
            f = open(filePath,"wb")

        f.write(result)
        f.close()
        parse_links(filePath, q, crawled) 
    except IsADirectoryError:
        #print("Bad link")
        return cookie
    return cookie

if __name__ == '__main__':
    main(sys.argv[1:])


