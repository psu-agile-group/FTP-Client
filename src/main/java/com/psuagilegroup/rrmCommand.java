package com.psuagilegroup;


import org.apache.commons.net.ftp.FTPClient;
import java.io.IOException;

public class rrmCommand extends Command{
    public rrmCommand(FTPClient ftpClient) {
        super(ftpClient);
    }

    @Override
    public String help(){
        return "Delete File From Remote Server.";
    }

    @Override
    public FTPSession run(FTPSession currentSession, String[] lineSplit){
        //TODO
        if(lineSplit.length != 2){
            System.out.println("The format for deleting: rrm 'the path of file_name'");
        } else {
            String fileName = lineSplit[1];
            try {
                delete_file(fileName);
            } catch(IOException e){
                currentSession.output = "Something wicked happened locally.";
            }
        }
        return currentSession;
    }

    private void delete_file(String file_name) throws IOException{
        String fileName = file_name;
        if((fileName.charAt(0)) == '/'){
            System.out.println("This is a direct path");
            String [] pathFile = file_name.split("/");

            for(int i = 0; i<pathFile.length-1; i++){
                boolean checkDir = ftpClient.changeWorkingDirectory(pathFile[i]);
                if(!checkDir){
                    System.out.println("Invalid old directory file: " + pathFile[i]);
                }
            }

            boolean success = ftpClient.deleteFile(fileName);
            if(success){
                System.out.println("The file was deleted successfully.");
            } else {
                System.out.println("Could not delete the file, it may not exist.");
            }
        } else {
            boolean success = ftpClient.deleteFile(fileName);
            if(success){
                System.out.println("The file was deleted successfully.");
            } else {
                System.out.println("Could not delete the file, it may not exist.");
            }
        }
    }
}
