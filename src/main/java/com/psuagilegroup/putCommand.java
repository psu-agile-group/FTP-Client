package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

public class putCommand extends Command {

    public putCommand(FTPClient ftpClient)
    {
        super(ftpClient);
    }

    @Override
    public String help(){
        return "put\t\tPut a file to server.";
    }
    @Override
    public FTPSession run( FTPSession currentSession,  String[] lineSplit )
    {
        if (lineSplit.length != 3) {
            //print_usage("put");
            currentSession.output = "put: missing file operand\nUsage: put [local_file] [remote_file]\n";

        }else {
            put_file_toServer( lineSplit[1], lineSplit[2]);
        }
        return currentSession;
    }

    private void put_file_toServer( String localPath, String remotePath){
        ftpClient.enterLocalPassiveMode();
        try{
            // check local file exists
            File inFile = new File(localPath);
            if (inFile.exists() == false) {
                System.out.println("file does not exist!");
                return;
            }

            // ask if overwrite
            String[] names = ftpClient.listNames(remotePath);
            if (names.length == 1) { //check file exists
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("file exists, overwrite? ('y' to continue)");
                String cmd = console.readLine();
                if (cmd.charAt(0) != 'y'){
                    System.out.println("uploading is cancelled.");
                    return;
                }
            }

            // upload it
            InputStream input = new FileInputStream(inFile);
            boolean sucess = ftpClient.storeFile(remotePath, input);
            if (sucess) {
                System.out.print("\"" + remotePath + "\"" + " is uploaded ok.");
            } else {
                System.out.print("\"" + remotePath + "\"" + " is uploaded failed.");
            }

        }catch (IOException e) {
            System.out.println("Oops! Something wrong happened: " + e);
        }
        System.out.println("\n");
    }

}
