package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class cdCommand extends Command {
    public cdCommand(FTPClient ftpClient) {
        super(ftpClient);
    }

    @Override
    public String help() {
        return "cd\t\t\tChanges the current directory.";
    }

    @Override
    public FTPSession run(FTPSession currentSession, String[] lineSplit) {

        //FIXME
        if (lineSplit.length == 2) {
            change_working_directory_on_server(currentSession, lineSplit[1]);
        } else {
            currentSession.output = "Error: Invalid Argument";
        }
        return currentSession;
    }

    private void change_working_directory_on_server(FTPSession currentSession, String remotePath) {
        try {
            if (remotePath == "..") {
                ftpClient.changeToParentDirectory();
            } else {
                ftpClient.changeWorkingDirectory(remotePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            currentSession.remote_directory = ftpClient.printWorkingDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
