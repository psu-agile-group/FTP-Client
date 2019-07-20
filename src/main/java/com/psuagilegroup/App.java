package com.psuagilegroup;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
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
        commands.put("",       new emptyCommand(ftpClient));
        commands.put("logout", new logoutCommand(ftpClient));
        commands.put("get",    new getCommand(ftpClient));
        commands.put("cd",     new cdCommand(ftpClient));
        commands.put("ls",     new lsCommand(ftpClient));
        commands.put("rls",    new rlsCommand(ftpClient));
        commands.put("rrn",    new rrnCommand(ftpClient));
        commands.put("rn",     new rnCommand(ftpClient));
        commands.put("mkdir",  new mkdirCommand(ftpClient));
        commands.put("put",    new putCommand(ftpClient));
        commands.put("rmdir",    new rmdirCommand(ftpClient));
        commands.put("rrm",    new rrmCommand(ftpClient));

        while (true) {
            System.out.print("FTP Shell:" + currentSession.remote_directory + " >> ");
            command = console.readLine();
            String[] lineSplit = command.split(" ");

            if(commands.containsKey(lineSplit[0])) {
                currentSession = commands.get(lineSplit[0]).run(currentSession, lineSplit);
                System.out.println(currentSession.output);

                // Clear the output after printing
                currentSession.output = "";
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
