package com.psuagilegroup;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;

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
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String command;

        HashMap<String, Command> commands = new HashMap<>();
        commands.put("logout", new logoutCommand(ftpClient));
        commands.put("", new emptyCommand(ftpClient));
        commands.put("get", new getCommand(ftpClient));

        while (true) {
            System.out.print("FTP Shell" + ftpClient.printWorkingDirectory() + ">> ");
            command = console.readLine();
            String[] lineSplit = command.split(" ");

            if(commands.containsKey(lineSplit[0])) {
                commands.get(lineSplit[0]).run(lineSplit);
                continue;
            }else if(lineSplit[0].equals("put")) {
                if (lineSplit.length != 3) {
                    //print_usage("put");
                    System.out.println("put: missing file operand\nUsage: put [local_file] [remote_file]\n");
                    continue;
                }
                put_file_toServer(ftpClient, lineSplit[1], lineSplit[2]);

            }else if(lineSplit[0].equals("rls")) {
                if(lineSplit.length==1) {
                    list_files_fromServer(ftpClient, "");
                } else {
                    if(lineSplit[1].equals("-l")) {
                        if(lineSplit.length==2) {
                            long_list_files_fromServer(ftpClient, "");
                        } else {
                            for (int i = 2; i < lineSplit.length; ++i) {
                                long_list_files_fromServer(ftpClient, lineSplit[i]);
                            }
                        }
                    } else {
                        for (int i = 1; i < lineSplit.length; ++i) {
                            list_files_fromServer(ftpClient, lineSplit[i]);
                        }
                    }
                }

            }else if(lineSplit[0].equals("ls")) {
                if(lineSplit.length==1) {
                    list_files_fromLocal("");
                } else {
                    for (int i = 1; i < lineSplit.length; ++i) {
                        list_files_fromLocal(lineSplit[i]);
                    }
                }

            }else if (lineSplit[0].equals("mkdir")){
                create_dir_on_server(ftpClient, command);

            }else if(lineSplit[0].equals("cd")) {
                //FIXME
                if (lineSplit.length == 2) {
                    change_working_directory_on_server(ftpClient, lineSplit[1]);
                } else {
                    System.out.println(ftpClient.printWorkingDirectory()+"\n");
                }

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
                System.out.println("ls\t\tDisplays directories and files in the current directory.\n" +
                        "rls\t\tDisplays directories and files in the remote directory.\n" +
                        "cd\t\tChanges the current directory.\n" +
                        "mkdir\tMake directory.\n" +
                        "rn\t\tRename file.\n" +
                        "get\t\tGet a file from server.\n" +
                        "put\t\tPut a file to server.\n" +
                        "exit\tExit FTP Shell.\n");

            }else {
                System.out.println("[" + lineSplit[0] + "] is not recognized as an internal or external command\n");
            }
        }
    }

    private static void list_files_fromServer(FTPClient ftpClient, String remotePath){
        ftpClient.enterLocalPassiveMode();
        try{
            FTPFile[] listFiles = ftpClient.listFiles(remotePath);
            if (listFiles != null) {
                for (FTPFile file : listFiles) {
                    if(file.isDirectory()) {
                        System.out.print(file.getName() + "/\t");
                    }else {
                        System.out.print(file.getName() + "\t");
                    }
                }
            }
        }catch (IOException e) {
            System.out.println("Oops! Something wrong happened: " + e);
        }
        System.out.println("\n");
    }

    private static void long_list_files_fromServer(FTPClient ftpClient, String remotePath){
        ftpClient.enterLocalPassiveMode();
        try{
            FTPFile[] listFiles = ftpClient.listDirectories(remotePath);
            for (FTPFile file : listFiles) {
                System.out.println(file);
            }
        }catch (IOException e) {
            System.out.println("Oops! Something wrong happened: " + e);
        }
        System.out.println("\n");
    }

    private static void list_files_fromLocal(String localPath){
        System.out.println(System.getProperty("user.dir") + "\\" + localPath+": ");
        File localDir = new File(System.getProperty("user.dir") + "/" + localPath);
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

    private static void change_working_directory_on_server(FTPClient ftpClient, String remotePath){
        try {
            if(remotePath == ".."){
                ftpClient.changeToParentDirectory();
            } else {
                ftpClient.changeWorkingDirectory(remotePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
