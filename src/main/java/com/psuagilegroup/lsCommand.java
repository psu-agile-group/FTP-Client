package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;

public class lsCommand extends Command{
    public lsCommand(FTPClient ftpClient)
    {
        super(ftpClient);
    }

    @Override
    public String help(){
        return "ls\t\tDisplays directories and files in the current directory.";
    }
    @Override
    public FTPSession run( FTPSession currentSession,  String[] lineSplit )
    {

        if(lineSplit.length==1) {
            list_files_fromLocal(currentSession,"");
        } else {
            for (int i = 1; i < lineSplit.length; ++i) {
                list_files_fromLocal(currentSession, lineSplit[i]);
            }
        }

        return currentSession;
    }

    private void list_files_fromLocal(FTPSession currentSession, String localPath){
        System.out.println(currentSession.local_directory+"/"+localPath+": ");
        File localDir = new File(currentSession.local_directory+"/"+localPath);
        File[] listFiles = localDir.listFiles();
        if(listFiles !=null) {
            for (File file : listFiles) {
                if(file.isDirectory()) {
                    System.out.print(file.getName() + "/\t");
                }else {
                    System.out.print(file.getName() + "\t");
                }
            }
        }
        System.out.println("\n");
    }

}
