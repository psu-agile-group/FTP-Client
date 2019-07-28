package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

public class chmodCommand extends Command {
    public chmodCommand(FTPClient ftpClient) {
        super(ftpClient);
    }

    @Override
    public String help() {
        return "chmod\t\tchange mode.";
    }

    @Override
    public FTPSession run(FTPSession currentSession, String[] lineSplit) {
        if (lineSplit.length != 3) {
            System.out.println("Usage: 'chmod [mode] [remote_path]'");
        } else {
            String mode = lineSplit[1];
            String remotePath = lineSplit[2];
            if (check_mode(mode)) {
                try {
                    change_file_mode_server(mode, remotePath);
                } catch (IOException e) {
                    currentSession.output = "Something wicked happened";
                }
            } else {
                System.out.println("File mode value need to be integer, Usage: 'chmod [mode] [remote_path]'");
            }
        }
        return currentSession;
    }

    private boolean check_mode(String mode) {
        try {
            Integer.parseInt(mode);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void change_file_mode_server(String mode, String remotePath) throws IOException {
        ftpClient.sendSiteCommand("chmod " + mode + " " + remotePath);
        show_Message_fromServer();
        FTPFile[] listFiles = ftpClient.listFiles(remotePath);
        for (FTPFile file : listFiles) {
            System.out.println(file);
        }
    }
}
