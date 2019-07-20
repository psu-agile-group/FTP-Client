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

    private static void shell(FTPClient ftpClient, connectInfo save) throws IOException {
        FTPSession currentSession = new FTPSession();
        currentSession.save = save;

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String command;

        HashMap<String, Command> commands = new HashMap<>();
        commands.put("", new emptyCommand(ftpClient));
        commands.put("logout", new logoutCommand(ftpClient));
        commands.put("get", new getCommand(ftpClient));
        commands.put("cd", new cdCommand(ftpClient));
        commands.put("ls", new lsCommand(ftpClient));
        commands.put("rls", new rlsCommand(ftpClient));
        commands.put("rrn", new rrnCommand(ftpClient));
        commands.put("rn", new rnCommand(ftpClient));
        commands.put("mkdir", new mkdirCommand(ftpClient));
        commands.put("put", new putCommand(ftpClient));
        commands.put("rmdir", new rmdirCommand(ftpClient));
        commands.put("login", new loginCommand(ftpClient));

        commands.get("login").run(currentSession, new String[0]);
        while (true) {
            System.out.print("FTP Shell:" + currentSession.remote_directory + " >> ");
            command = console.readLine();
            String[] lineSplit = command.split(" ");

            if (commands.containsKey(lineSplit[0])) {
                currentSession = commands.get(lineSplit[0]).run(currentSession, lineSplit);
                System.out.println(currentSession.output);

                // Clear the output after printing
                currentSession.output = "";
            } else if (command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye");
                System.exit(0);
            } else if (lineSplit[0].equalsIgnoreCase("help")) {
                //TODO
                for (Map.Entry<String, Command> entry : commands.entrySet()) {
                    System.out.println(entry.getValue().help());
                }
            } else {
                System.out.println("[" + lineSplit[0] + "] is not recognized as an internal or external command\n");
            }
        }
    }

    public static void main(String[] args) {
        // Variable from the command line argument
        // ftpupload.net 21 epiz_24139835 OkmvEHWbl4HFJ8a
        connectInfo save = new connectInfo();
        if( args.length < 4 ){
             save.readInfo();
        }else {
            save.saveInfo(args[0], Integer.parseInt(args[1]), args[2], args[3]);
            try {
                save.saveInfo();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        FTPClient ftpClient = new FTPClient();


        try {
            System.out.println();
            shell(ftpClient,save);

        } catch (IOException e) {
            System.out.println("Oops! Something wrong happened here");
            e.printStackTrace();
        }
    }
}
