import socket

def isHTML(document) :    
    with open(document) as fh:
            for line in fh:
                if "<!DOCTYPE html>" in line :
                    return True
    return False  

def parseURLToHostNameAndFilePath(url) :
    isUrl = 0
    filePath = ""
    domainName = ""
    if "http://" in url:
	    isUrl = 1
	    tempList = url.split("//")
	    #domain and file path
	    domainList = tempList[1].split("/")
	    domainName = domainList[0]
	    filePath = domainList[1]
    elif "https://" in url:
        isUrl = 1
        tempList = url.split("//")
        #domain and file path
        domainList = tempList[1].split("/")
        domainName = domainList[0]
        filePath = domainList[1]
    elif "#" not in url:
	    isUrl = 2
	    filePath = url
    return (isUrl, domainName, filePath)

def createSocket(server, port) : 
    try:
        server_ip = socket.gethostbyname(server)
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((server_ip, port))
        return s
    except socket.gaierror:
        #print("BAD LINK")
        return None
    except TimeoutError:
        #print("TIME OUT")
        return None


    
