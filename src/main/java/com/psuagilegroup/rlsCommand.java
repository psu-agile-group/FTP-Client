package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

public class rlsCommand extends Command {

    public rlsCommand(FTPClient ftpClient) {
        super(ftpClient);
    }

    @Override
    public String help() {
        return "rls\t\tDisplays directories and files in the remote directory.";
    }

    @Override
    public FTPSession run(FTPSession currentSession, String[] lineSplit) {
        if (lineSplit.length == 1) {
            list_files_fromServer("");
        } else {
            if (lineSplit[1].equals("-l")) {
                if (lineSplit.length == 2) {
                    long_list_files_fromServer("");
                } else {
                    for (int i = 2; i < lineSplit.length; ++i) {
                        long_list_files_fromServer(lineSplit[i]);
                    }
                }
            } else {
                for (int i = 1; i < lineSplit.length; ++i) {
                    list_files_fromServer(lineSplit[i]);
                }
            }
        }
        return currentSession;
    }

    private void list_files_fromServer(String remotePath) {
        ftpClient.enterLocalPassiveMode();
        try {
            FTPFile[] listFiles = ftpClient.listFiles(remotePath);
            if (listFiles != null) {
                for (FTPFile file : listFiles) {
                    if (file.isDirectory()) {
                        System.out.print(file.getName() + "/\t");
                    } else {
                        System.out.print(file.getName() + "\t");
                    }
                }
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("Oops! Something wrong happened: " + e);
        }
    }

    private void long_list_files_fromServer(String remotePath) {
        ftpClient.enterLocalPassiveMode();
        try {
            FTPFile[] listFiles = ftpClient.listFiles(remotePath);
            for (FTPFile file : listFiles) {
                if (file.isDirectory()) {
                    System.out.println(file + "/");
                } else {
                    System.out.println(file);
                }
            }
        } catch (IOException e) {
            System.out.println("Oops! Something wrong happened: " + e);
        }
    }
}
