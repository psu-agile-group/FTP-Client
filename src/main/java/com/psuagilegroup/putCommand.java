package com.psuagilegroup;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.Arrays;

public class putCommand extends Command {

    public putCommand(FTPClient ftpClient)
    {
        super(ftpClient);
    }

    @Override
    public String help(){
        return "put\t\tPut file / files / folder to server (relative path).";
    }
    @Override
    public FTPSession run( FTPSession currentSession,  String[] lineSplit )
    {
        if (lineSplit.length < 3) {
            currentSession.output = "put: missing file operand\nUsage: put [local_file(s), folder(s), ...] [remote_folder]\n";

        }else {
            String remoteDir = lineSplit[lineSplit.length - 1];
            String[] localPaths = Arrays.copyOfRange(lineSplit, 1, lineSplit.length - 1);
            try{
                put_files_toServer_wrapper(remoteDir, localPaths);
            }catch (IOException e) {
                currentSession.output = "Exception: " + e.getMessage();
            }
        }
        return currentSession;
    }

    private boolean check_remote_dir_exists(String remotePath) throws IOException {
        String currentWD = ftpClient.printWorkingDirectory();
        if (!ftpClient.changeWorkingDirectory(remotePath)){
            return false;
        }

        if (!ftpClient.changeWorkingDirectory(currentWD)){
            throw new IOException("failed to switch remote working directory back.");
        }
        return true;
    }

    static int overwrite_files = 0;
    private void put_files_toServer_wrapper(String remoteDir, String[] localFiles) throws IOException {
        // 1. check local files / folders are valid
        for (String localPath : localFiles) {
            File file = new File(localPath);
            if (!file.exists()) {
                System.out.println(" - local: \"" + localPath + "\"" + " is invalid.");
                return;
            }
        }

        // 2. check remoteDir is valid
        if (!check_remote_dir_exists(remoteDir)) {
            System.out.print(" - remote folder: \"" + remoteDir + "\"" + " is invalid.");
            return;
        }

        // 3. put files, create folders recursively until all files were uploaded
        overwrite_files = 0;
        ftpClient.enterLocalPassiveMode();
        put_files_toServer(remoteDir, localFiles);
    }

    private void put_files_toServer(String remoteDir, String[] localFiles) throws IOException {
        for (String localPath : localFiles) {
            File file = new File(localPath);
            String remoteFilePath = remoteDir + '/' + file.getName();
            if (file.isFile()) { // file(s) in argument list
                put_file_toServer(localPath, remoteFilePath);
            } else if (file.isDirectory()) { // folder(s) in argument list
                boolean success = check_remote_dir_exists(remoteFilePath) || ftpClient.makeDirectory(remoteFilePath);
                if (success) {
                    put_folder_toServer(localPath, remoteFilePath);
                } else {
                    System.out.println(" - failed to create remote directory : " + remoteFilePath);
                }
            }
        }
    }

    private void put_folder_toServer(String localDir, String remoteDir) throws IOException{
        File fDir = new File(localDir);
        File[] fList = fDir.listFiles(); // get all file & folders in localDir
        if (fList != null && fList.length > 0) {
            for (File fItem : fList) {
                String localPath = localDir + '/' + fItem.getName();
                String remotePath = remoteDir + '/' + fItem.getName();
                if (fItem.isFile()) { // file(s) in folder
                    put_file_toServer(localPath, remotePath);
                } else if (fItem.isDirectory()) { // folder(s) in folder
                    boolean success = check_remote_dir_exists(remotePath) || ftpClient.makeDirectory(remotePath);
                    if (success) {
                        put_folder_toServer(localPath, remotePath); // recursively upload
                    } else {
                        System.out.println(" - failed to create remote directory : " + remotePath);
                    }
                }
            }
        }
    }

    private void put_file_toServer(String localPath, String remotePath) throws IOException{
        // ask if overwrite
        String[] names = ftpClient.listNames(remotePath);
        if (names.length == 1) { // file exists
            if (overwrite_files == 2) { // "no" to skip all duplicated files
                System.out.println(" - uploading [ " + localPath + " ] is skipped.");
                return;
            } else if (overwrite_files == 0) { // "y/n" to check each duplicated file
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                System.out.print(" - remote file [" + remotePath + "] exists, overwrite? ('y/n'-one, 'yes/no'-all!)");
                String cmd = console.readLine();
                if (cmd.toUpperCase().equals("NO")) {
                    System.out.println(" - uploading [ " + localPath + " ] is skipped.");
                    overwrite_files = 2;
                    return;
                }else if (cmd.toUpperCase().equals("YES")) {
                    overwrite_files = 1;
                }else if (cmd.toUpperCase().equals("N")) {
                    System.out.println(" - uploading [ " + localPath + " ] is skipped.");
                    return;
                }//else if (cmd.toUpperCase().equals("Y")) { overwrite_files = 0; }
            } // else if (overwrite_files == 1) {} // "yes" to overwrite all duplicated files
        }

        // upload it
        File file = new File(localPath);
        InputStream input = new FileInputStream(file);
        //ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
        System.out.print(" - from : [" + localPath +"] to [" + remotePath + "]");
        boolean success = ftpClient.storeFile(remotePath, input);
        if (success) {
            System.out.println(", upload ok.");
        } else {
            System.out.println(", upload failed.");
        }
    }
}
