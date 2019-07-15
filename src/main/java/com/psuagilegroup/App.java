package com.psuagilegroup;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class App {

    private static void show_Message_fromServer(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (int i = 0; i < replies.length; ++i) {
                System.out.println("SERVER: " + replies[i]);
            }
        }
    }
    private static void shell(FTPClient ftpClient) throws IOException {
        FTPSession currentSession = initSession(ftpClient);

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String command;

        HashMap<String, Command> commands = new HashMap<>();
        commands.put("", new emptyCommand(ftpClient));
        commands.put("logout", new logoutCommand(ftpClient));
        commands.put("get", new getCommand(ftpClient));
        commands.put("cd", new cdCommand(ftpClient));
        commands.put("ls", new lsCommand(ftpClient));
        commands.put("rls", new rlsCommand(ftpClient));

        while (true) {
            System.out.print("FTP Shell:" + currentSession.remote_directory + " >> ");
            command = console.readLine();
            String[] lineSplit = command.split(" ");

            if(commands.containsKey(lineSplit[0])) {
                currentSession = commands.get(lineSplit[0]).run(currentSession, lineSplit);
                System.out.println(currentSession.output);

                // Clear the output after printing
                currentSession.output = "";
                continue;
            }else if(lineSplit[0].equals("put")) {
                if (lineSplit.length != 3) {
                    //print_usage("put");
                    System.out.println("put: missing file operand\nUsage: put [local_file] [remote_file]\n");
                    continue;
                }
                put_file_toServer(ftpClient, lineSplit[1], lineSplit[2]);

            }else if (lineSplit[0].equals("mkdir")){
                create_dir_on_server(ftpClient, command);
            }else if(lineSplit[0].equals("rrn")) {
                //TODO
                if (lineSplit.length != 3) {
                    System.out.println("The format for renaming the file: 'rrn old_file_name new_file_name'");
                }
                else {
                    String old = lineSplit[1];
                    String new_file = lineSplit[2];
                    rename_file_server(ftpClient,old, new_file);
                }
            }else if(lineSplit[0].equals("rn")) {
                //TODO
                if (lineSplit.length != 3) {
                    System.out.println("The format for renaming the file: 'rn old_file_name new_file_name'");
                }
                else {
                    String old = lineSplit[1];
                    String new_file = lineSplit[2];
                    rename_file_local(old, new_file);
                }
            }else if(command.equalsIgnoreCase("exit")||command.equalsIgnoreCase("quit"))  {
                System.out.println("Goodbye");
                System.exit(0);
            }else if(lineSplit[0].equalsIgnoreCase("help")) {
                //TODO
                for( Map.Entry<String, Command> entry: commands.entrySet()){
                    System.out.println(entry.getValue().help());
                }
            }else {
                System.out.println("[" + lineSplit[0] + "] is not recognized as an internal or external command\n");
            }
        }
    }

    private static FTPSession initSession( FTPClient ftpClient ){
        FTPSession currentSession = new FTPSession();
        try {
            currentSession.remote_directory = ftpClient.printWorkingDirectory();
            currentSession.local_directory = new java.io.File(".").getCanonicalPath();
        }catch(java.io.IOException e) {
            System.out.println("Oops! Something wrong happened: " + e);
        }
        return currentSession;
    }
    private static void create_dir_on_server(FTPClient ftpClient, String lineSplit)
    {
        ftpClient.enterLocalActiveMode();
        boolean flag = false;
        try {
            flag = ftpClient.changeWorkingDirectory("/htdocs");
            show_Message_fromServer(ftpClient);
            if(flag)
                System.out.println("hit");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String dirToCreate = lineSplit;
            boolean success = ftpClient.makeDirectory(dirToCreate);
            show_Message_fromServer(ftpClient);
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


    private static void put_file_toServer(FTPClient ftpClient, String localPath, String remotePath){
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

    private static void rename_file_server (FTPClient ftpClient, String old_name, String new_name) throws IOException {
        String oldFile = old_name;
        String newFile = new_name;

        // Enter the whole direct path. For example: /htdocs/index2.html , change to /htdocs/index4.html
        if ((oldFile.charAt(0)) == '/') {
            System.out.println("This is direct path");
            String [] pathElements = old_name.split("/");
            String [] pathElementsNew = new_name.split("/");
            // Each element is a directory, check if the directory is valid before renaming
            for (int i = 0; i < pathElements.length-1; ++i){
                boolean checkDir = ftpClient.changeWorkingDirectory(pathElements[i]);
                if (!checkDir){
                    System.out.println("Invalid old directory/file: " + pathElements[i]);
                }
            }
            for (int i = 0; i < pathElementsNew.length-1; ++i){
                boolean checkDir = ftpClient.changeWorkingDirectory(pathElementsNew[i]);
                if (!checkDir){
                    System.out.println("Invalid new directory/file: " + pathElementsNew[i]);
                }
            }
            // Valid directories, renaming the file
            boolean success = ftpClient.rename(oldFile, newFile);
            if (success) {
                System.out.println("Direct Path: " + oldFile + " was successfully renamed to: " + newFile);
            } else {
                System.out.println("Failed to rename: " + oldFile);
            }
        }
        //Relative Path. You must change the working directory to /htdocs to use this.
        else {
            boolean success = ftpClient.rename(oldFile, newFile);
            if (success) {
                System.out.println(oldFile + " was successfully renamed to: " + newFile);
            } else {
                System.out.println("Failed to rename: " + oldFile);
            }
        }
    }

    private static void rename_file_local (String old_name, String new_name) throws IOException {

        //  System.out.println(System.getProperty("user.dir") + "\\" + old_name +": ");
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


    public static void main(String[] args) {
        // Variable from the command line argument
        // ftpupload.net 21 epiz_24139835 OkmvEHWbl4HFJ8a
        String server = args[0];//"193.219.28.2";
        int port = Integer.parseInt(args[1]); //21;
        String user = args[2];//"anonymous";
        String pass = args[3]; //"me@nowhere.com";
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(server, port);
            show_Message_fromServer(ftpClient);
            int replyCode = ftpClient.getReplyCode();

            // FTPReply stores a set of constants for FTP reply codes.
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return;
            }
            boolean success = ftpClient.login(user, pass); // login method in the library
            show_Message_fromServer(ftpClient); // show messg from the server after log in.
            if (!success) {
                System.out.println("Could not login to the server");
            } else {
                System.out.println("LOGGED IN SERVER");
            }
            System.out.println();

            shell(ftpClient);

        } catch (IOException e) {
            System.out.println("Oops! Something wrong happened");
            e.printStackTrace();
        }
    }
}
