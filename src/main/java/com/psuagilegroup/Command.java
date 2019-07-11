package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class Command {
    FTPClient ftpClient;
    public Command(FTPClient ftpClient)
    {
        this.ftpClient = ftpClient;
    }
    public abstract Boolean run( String[] args );
}

