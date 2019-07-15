package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;

public class rnCommand extends Command {
    public rnCommand(FTPClient ftpClient)
    {
        super(ftpClient);
    }

    @Override
    public String help(){
        return "rn\t\tRename file locally.";
    }
    @Override
    public FTPSession run( FTPSession currentSession,  String[] lineSplit )
    {
        //TODO
        if (lineSplit.length != 3) {
            System.out.println("The format for renaming the file: 'rn old_file_name new_file_name'");
        }
        else {
            String old = lineSplit[1];
            String new_file = lineSplit[2];
            try {
                rename_file_local(old, new_file);
            }catch(IOException e){
                currentSession.output = "Something wicked happened locally.";
            }
        }
        return currentSession;
    }

    private void rename_file_local (String old_name, String new_name) throws IOException {

        File old_file = new File(old_name);
        File new_file = new File(new_name);

        boolean return_value = old_file.renameTo(new_file);
        if (return_value){
            System.out.println("Locally, " + old_name + " was successfully renamed to: " + new_name);
        }
        else {
            System.out.println("Oop, something is wrong");
        }
    }

}
