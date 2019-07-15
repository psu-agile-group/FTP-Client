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
    protected void show_Message_fromServer() {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (int i = 0; i < replies.length; ++i) {
                System.out.println("SERVER: " + replies[i]);
            }
        }
    }
}

