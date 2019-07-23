package com.psuagilegroup;


import org.apache.commons.net.ftp.FTPClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class getCommand extends Command
{
    public getCommand(FTPClient ftpClient)
    {
        super(ftpClient);
    }

    @Override
    public String help(){
        return "get\t\tGet a file from server.";
    }
    @Override
    public FTPSession run( FTPSession currentSession, String[] lineSplit )
    {

        if (lineSplit.length != 2) {
            //print_usage("get");
            System.out.println("get: missing file operand\nUsage: get [filename]\n");
        }else {
            get_file_fromServer(lineSplit[1]);
        }
        return currentSession;
    }

    private void get_file_fromServer( String remotePath){
        ftpClient.enterLocalPassiveMode();
        try{
            String[] names = ftpClient.listNames(remotePath);
            if (names.length == 1) { //check file exists

                // use same name for local file name
                String localPath = remotePath;
                int index = localPath.lastIndexOf('/');
                if (index != -1) {
                    localPath = localPath.substring(index + 1);
                }

                OutputStream local = new FileOutputStream(localPath);
                ftpClient.retrieveFile(remotePath, local);
                local.close();
                System.out.print("saved to " + localPath);
            } else {
                System.out.print("\"" + remotePath + "\"" + " does not exist.");
            }
        }catch (IOException e) {
            System.out.println("Oops! Something wrong happened: " + e);
        }
        System.out.println("\n");
    }
}
