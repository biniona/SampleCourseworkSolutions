import re
import os


def getCookie(header):
    cookie = "NONE"
    match = re.search('Set-Cookie: (.+); Domain', header)
    try:
        if match:
            cookie = match.group(1)
    except AttributeError:
        return "NONE"
    return cookie 

def getContentType(header):
    content = "NONE"
    match = re.search('Content-Type: (.+)', header)
    try:
        if match:
            content = match.group(1)
    except AttributeError:
        return "NONE"
    return content 

def returnCode(header):
    returnCode = header.split(" ")[1];
    return int(returnCode)


def create_dir (localDirectoryName):
    dirName = localDirectoryName
    orignalDirName = dirName
    identifier = "/"
    i = 0
    while(True):
        try:
            dirName = orignalDirName+identifier
            os.mkdir(dirName)
            break;
        except FileExistsError:
            i+=1
            identifier = str(i) + "/"
            continue;
    return dirName

