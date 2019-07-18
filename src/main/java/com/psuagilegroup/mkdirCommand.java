package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class mkdirCommand extends Command {

    public mkdirCommand(FTPClient ftpClient)
    {
        super(ftpClient);
    }

    @Override
    public String help(){
        return "mkdir\tMake directory.";
    }
    @Override
    public FTPSession run( FTPSession currentSession,  String[] args )
    {
        create_dir_on_server(args[1]);
        return currentSession;
    }

    private  void create_dir_on_server(String lineSplit)
    {
        ftpClient.enterLocalActiveMode();

        try {
            String dirToCreate = lineSplit;
            boolean success = ftpClient.makeDirectory(dirToCreate);
            show_Message_fromServer();
            if (success) {
                System.out.println("Successfully created directory: " + dirToCreate);
            } else {
                System.out.println("Failed to create directory. See server's reply.");
            }
        } catch (IOException ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        }
    }


}
