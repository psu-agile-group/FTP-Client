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
            //show_Message_fromServer(ftpClient);
            int replyCode = ftpClient.getReplyCode();

            // FTPReply stores a set of constants for FTP reply codes.
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return currentSession;
            }
            boolean success = ftpClient.login(save.user, save.pass); // login method in the library
            //show_Message_fromServer(ftpClient); // show messg from the server after log in.
            if (!success) {
                System.out.println("Could not login to the server");
            } else {
                System.out.println("LOGGED IN SERVER");
            }
            System.out.println();


        } catch (IOException e) {
            System.out.println("Oops! Something wrong happened");
            e.printStackTrace();
        }

        currentSession.local_directory = "";
        try {
            currentSession.remote_directory = ftpClient.printWorkingDirectory();
        }catch (IOException ex){
            System.out.println(ex);
        }
        return currentSession;
    }
}
