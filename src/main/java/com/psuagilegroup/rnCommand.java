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
                rename_file_local(OLD,NEW);
            }catch(IOException e){
                currentSession.output = "Something wicked happened locally.";
            }
        }
        return currentSession;
    }

    private void rename_file_local (File OLD, File NEW) throws IOException {

        boolean return_value = OLD.renameTo(NEW);
        if (return_value){
            System.out.println("Locally, " + OLD + " was successfully renamed to: " + NEW);
        }
        else {
            System.out.print("Oop, something is wrong");
        }
    }

}
