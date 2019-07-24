package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class loginCommand extends Command{
    public loginCommand(FTPClient ftpClient)
    {
        super(ftpClient);
    }
    @Override
    public String help(){
        return "login\tLogs into server.";
    }
    @Override
    public FTPSession run(  FTPSession currentSession, String[] args )
    {
        connectInfo save = currentSession.save;

        try {
            ftpClient.connect(save.server, save.port);
            show_Message_fromServer();
            int replyCode = ftpClient.getReplyCode();

            // FTPReply stores a set of constants for FTP reply codes.
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                currentSession.output = "Operation failed. Server reply code: " + replyCode;
                return currentSession;
            }
            boolean success = ftpClient.login(save.user, save.pass); // login method in the library
            show_Message_fromServer(); // show messg from the server after log in.
            if (!success) {
                currentSession.output = "COULD NOT LOGIN TO SERVER";
            } else {
                currentSession.output = "LOGGED INTO SERVER";
            }


        } catch (IOException e) {
            currentSession.output = "Oops! Something wrong happened connecting";
            e.printStackTrace();
        }

        currentSession = initSession(currentSession);
        return currentSession;
    }

    private FTPSession initSession(FTPSession currentSession ){
        try {
            currentSession.remote_directory = ftpClient.printWorkingDirectory();
            currentSession.local_directory = new java.io.File(".").getCanonicalPath();
        }catch(java.io.IOException e) {
            System.out.println("Oops! Something wrong happened: " + e);
        }
        return currentSession;
    }
}
