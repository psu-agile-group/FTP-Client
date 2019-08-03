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
            currentSession.output ="The format for renaming the file: 'rn old_file_name new_file_name";
        }
        else {
            String old = lineSplit[1];
            String new_file = lineSplit[2];
            try {
                File OLD = new File(old);
                File NEW = new File(new_file);
                boolean check = rename_file_local(OLD,NEW);
                if (check){
                    currentSession.output = "True";
                }
                else{
                    currentSession.output = "False";
                }
            }catch(IOException e){
                currentSession.output = "Something wicked happened locally.";
            }
        }
        return currentSession;
    }

    private boolean rename_file_local (File old_name, File new_name) throws IOException {

        boolean return_value = old_name.renameTo(new_name);
        if (return_value){
            System.out.print("Locally, " + old_name + " was successfully renamed to: " + new_name);
            return true;
        }
        else {
            System.out.print("Oop, something is wrong");
            return false;
        }
    }

}
