package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class rmdirCommand extends Command {

    public rmdirCommand(FTPClient ftpClient) {
        super(ftpClient);
    }

    @Override
    public String help() {
        return "rmdir\t\tRemove directory on server.";
    }

    @Override
    public FTPSession run(FTPSession currentSession, String[] args) {
        remove_dir_on_server(args[1]);
        return currentSession;
    }

    private void remove_dir_on_server(String dirToRemove) {
        ftpClient.enterLocalActiveMode();
        /*boolean flag = false;
        try {
            flag = ftpClient.changeWorkingDirectory("/htdocs");
            show_Message_fromServer(ftpClient);
            if(flag)
                System.out.println("hit");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //System.out.printf("The directory is %s", dirToRemove);
        //System.out.printf("%s", dirToRemove);
        try {
            boolean deleted = ftpClient.removeDirectory(dirToRemove);
            if (deleted) {
                System.out.println("The directory was removed successfully.");
            } else {
                System.out.println("Could not delete the directory, it may not be empty.");
            }

        } catch (IOException ex) {
            System.out.println("Oh no, there was an error: " + ex.getMessage());
            ex.printStackTrace();

        }


    }
}
