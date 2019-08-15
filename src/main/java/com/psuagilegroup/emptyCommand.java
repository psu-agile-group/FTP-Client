package com.psuagilegroup;


import org.apache.commons.net.ftp.FTPClient;

public class emptyCommand extends Command {
    public emptyCommand(FTPClient ftpClient) {
        super(ftpClient);
    }

    @Override
    public FTPSession run(FTPSession currentSession, String[] args) {
        return currentSession;
    }
}