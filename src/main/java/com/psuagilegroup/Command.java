package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;

public abstract class Command {
    FTPClient ftpClient;
    public Command(FTPClient ftpClient)
    {
        this.ftpClient = ftpClient;
    }
    public abstract FTPSession run( FTPSession currentSession, String[] args );
    public String help(){return "";}
}

