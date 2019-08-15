package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class rrnCommand extends Command {
    public rrnCommand(FTPClient ftpClient) {
        super(ftpClient);
    }

    @Override
    public String help() {
        return "rrn   \t\tRename file on server.";
    }

    @Override
    public FTPSession run(FTPSession currentSession, String[] lineSplit) {
        //TODO
        if (lineSplit.length != 3) {
            System.out.println("The format for renaming the file: 'rrn old_file_name new_file_name'");
        } else {
            String old = lineSplit[1];
            String new_file = lineSplit[2];
            try {
                rename_file_server(old, new_file);
            } catch (IOException e) {
                currentSession.output = "Something wicked happened";
            }
        }
        return currentSession;
    }

    private void rename_file_server(String old_name, String new_name) throws IOException {
        String oldFile = old_name;
        String newFile = new_name;

        // Enter the whole direct path. For example: /htdocs/index2.html , change to /htdocs/index4.html
        if ((oldFile.charAt(0)) == '/') {
            System.out.println("This is direct path");
            String[] pathElements = old_name.split("/");
            String[] pathElementsNew = new_name.split("/");
            // Each element is a directory, check if the directory is valid before renaming
            for (int i = 0; i < pathElements.length - 1; ++i) {
                boolean checkDir = ftpClient.changeWorkingDirectory(pathElements[i]);
                if (!checkDir) {
                    System.out.println("Invalid old directory/file: " + pathElements[i]);
                }
            }
            for (int i = 0; i < pathElementsNew.length - 1; ++i) {
                boolean checkDir = ftpClient.changeWorkingDirectory(pathElementsNew[i]);
                if (!checkDir) {
                    System.out.println("Invalid new directory/file: " + pathElementsNew[i]);
                }
            }
            // Valid directories, renaming the file
            boolean success = ftpClient.rename(oldFile, newFile);
            if (success) {
                System.out.println("Direct Path: " + oldFile + " was successfully renamed to: " + newFile);
            } else {
                System.out.println("Failed to rename: " + oldFile);
            }
        }
        //Relative Path. You must change the working directory to /htdocs to use this.
        else {
            boolean success = ftpClient.rename(oldFile, newFile);
            if (success) {
                System.out.println(oldFile + " was successfully renamed to: " + newFile);
            } else {
                System.out.println("Failed to rename: " + oldFile);
            }
        }
    }
}
