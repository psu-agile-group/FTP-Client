package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;

public abstract class Command {
    FTPClient ftpClient;
    public Command(FTPClient ftpClient)
    {
        this.ftpClient = ftpClient;
    }
    public abstract Boolean run( String[] args );
}

