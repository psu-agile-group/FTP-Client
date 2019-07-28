package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTP;

import java.io.*;
import java.util.Arrays;

public class getCommand extends Command{
    public getCommand(FTPClient ftpClient)
    {
        super(ftpClient);
    }

    @Override
    public String help(){
        return "Get multiple file from remote server.";
    }
    @Override
    public FTPSession run( FTPSession currentSession,  String[] lineSplit )
    {
        if (lineSplit.length < 3) {
            System.out.println("The format of getting multiple file is: get [remote_file(s), folder(s), ...][local_folder]\n");
        }else {
            String localDir = lineSplit[lineSplit.length - 1];
            String[] remotePaths = Arrays.copyOfRange(lineSplit, 1, lineSplit.length - 1);
            try{
                get_files_fromServer_wrapper(localDir, remotePaths);
            }catch (IOException e) {
                currentSession.output = "Something wicked happened locally.";
            }
        }
        return currentSession;
    }

    private boolean check_local_dir_exists(String localPath) throws IOException{
        File file = new File(localPath);
        boolean exists = file.exists() && file.isDirectory();
        if(exists){
            return true;
        } else {
            return false;
        }
    }

    static int overwrite_files = 0;
    private void get_files_fromServer_wrapper(String localDir, String[] remoteFiles) throws IOException{
        // Check remote files / folders are valid
        for(String remotePath : remoteFiles){
            FTPFile ftpFile = ftpClient.mlistFile(remotePath);
            int type = ftpFile.getType();
            if(type != FTPFile.DIRECTORY_TYPE && type != FTPFile.FILE_TYPE){  // Directory type
                System.out.println(" - remote: \"" + remotePath + "\"" + " is invalid");
            }
        }

        // Check localDir is valid
        if(!check_local_dir_exists(localDir)){
            System.out.println(" - local folder: \"" + localDir + "\"" + " is invalid");
            return;
        }

        overwrite_files = 0;
        ftpClient.enterRemotePassiveMode();
        get_files_fromServer(localDir, remoteFiles);
    }

    private void get_files_fromServer(String localDir, String[] remoteFiles) throws IOException {
        if(remoteFiles != null && remoteFiles.length > 0){
           for(String file : remoteFiles){
               FTPFile ftpFile = ftpClient.mlistFile(file);
               int type = ftpFile.getType();
               if(type == FTPFile.DIRECTORY_TYPE){  // Directory type
                   String localDir2 = localDir + '/' + ftpFile.getName();
                   boolean success = check_local_dir_exists(localDir2) || (new File(localDir2)).mkdirs();
                   if(success){
                       get_folder_fromServer(file, localDir2);
                   } else {
                       System.out.println(" - failed to create local directory: " + localDir2);
                   }
               } else if(type == FTPFile.FILE_TYPE){  // File type
                   get_file_fromServer(file, localDir + '/' + file);
               }
           }
        }
    }

    private void get_folder_fromServer(String remoteDir, String localDir) throws IOException {
        ftpClient.enterLocalPassiveMode();
        FTPFile[] fList = ftpClient.listFiles(remoteDir); // get all file & folders in remoteDir
        if(fList != null && fList.length > 0){
            for(FTPFile fItem : fList){
                String itemName = fItem.getName();
                if (itemName.equals(".") || itemName.equals(".."))  // no need for . or ..
                    continue;

                String remotePath = remoteDir + '/' + itemName;
                String localPath = localDir + '/' + itemName;
                if(fItem.isFile()){ // file(s) in folder
                    get_file_fromServer(remotePath, localPath);
                } else if(fItem.isDirectory()){ // folder(s) in folder
                    boolean success = check_local_dir_exists(localPath) || (new File(localPath)).mkdirs();
                    if(success){
                        get_folder_fromServer(remotePath, localPath);
                    } else {
                        System.out.println(" - failed to create local directory: " + localPath);
                    }
                }
            }
        }
    }

    private void get_file_fromServer(String remotePath, String localPath) throws IOException {
        // Ask if overwrite
        File fLocal = new File(localPath);
        if(fLocal.exists()){ // File exists
            if(overwrite_files == 2) { // "no" to skip all duplicated files
                System.out.println(" - downloading [ " + remotePath + " ] is skipped");
                return;
            } else if(overwrite_files == 0) { // "y/n" to check each duplicated file
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                System.out.print(" - Local file [" + remotePath + "] exists, overwrite? ('y/n'-one, 'yes/no'-all!)");
                String cmd = console.readLine();
                if(cmd.toUpperCase().equals("NO")){
                    System.out.println(" - downloading [ " + remotePath + " ] is skipped");
                    overwrite_files = 2;
                    return;
                } else if(cmd.toUpperCase().equals("YES")){
                    overwrite_files = 1;
                } else if(cmd.toUpperCase().equals("N")){
                    System.out.println(" - downloading [ " + localPath + " ] is skipped");
                    return;
                }
            }
        }

        // Download it
        ftpClient.enterLocalPassiveMode();
        OutputStream local = new FileOutputStream(localPath);
        boolean success = ftpClient.retrieveFile(remotePath, local);
        local.close();
        System.out.print(" - from: [" + remotePath + "] to [" + localPath + "]");
        if(success){
            System.out.println(", download ok.");
        } else {
            System.out.println(", download failed.");
        }
    }
}
