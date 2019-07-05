import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class login {

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

        while (true) {
            System.out.print("\nFTP Shell >> ");
            command = console.readLine();
            String[] lineSplit = command.split(" ");

            if (command.equals("")) {
                continue;

            }else if(lineSplit[0].equals("get")) {
                if (lineSplit.length < 2) {
                    //print_usage("get");
                    System.out.println("need remote file name!");
                    continue;
                }

                get_file_fromServer(ftpClient, lineSplit[1]);

            }else if(lineSplit[0].equals("ls")) {
                list_files_fromServer(ftpClient);

            }else if(lineSplit[0].equals("cd")) {
                //TODO
                cd_directories_fromServer(ftpClient, command);

            }else if(command.equalsIgnoreCase("exit")||command.equalsIgnoreCase("quit"))  {
                System.out.println("Goodbye");
                System.exit(0);
            }else if(lineSplit[0].equalsIgnoreCase("help")) {
                //TODO
                System.out.println("ls\t\tDisplays directories and files in the current directory.\n" +
                        "cd\t\tChanges the current directory.\n" +
                        "exit\tExit FTP Shell.");

            }else {
                System.out.println("[" + lineSplit[0] + "] is not recognized as an internal or external command");
            }

        }
    }

    private static void list_files_fromServer(FTPClient ftpClient){
        ftpClient.enterLocalPassiveMode();
        try{
            FTPFile[] listFiles = ftpClient.listFiles();
            if (listFiles != null) {
                for (FTPFile file : listFiles) {
                    System.out.print(file.getName() + '\t');
                }
            }
        }catch (IOException e) {
            System.out.println("Oops! Something wrong happened: " + e);
        }
        System.out.println();
    }

    private static void cd_directories_fromServer(FTPClient ftpClient, String lineSplit){
        //TODO
        System.out.println(lineSplit);
    }

    private static void get_file_fromServer(FTPClient ftpClient, String remotePath){
        ftpClient.enterLocalPassiveMode();
        try{
            String[] names = ftpClient.listNames(remotePath);
            if (names.length == 1) { //check file exists

                // use same name for local file name
                String localPath = remotePath;
                int index = localPath.lastIndexOf('/');
                if (index != -1) {
                    localPath = localPath.substring(index + 1);
                }

                OutputStream local = new FileOutputStream(localPath);
                ftpClient.retrieveFile(remotePath, local);
                local.close();
                System.out.print("saved to " + localPath);
            } else {
                System.out.print("\"" + remotePath + "\"" + " does not exist.");
            }
        }catch (IOException e) {
            System.out.println("Oops! Something wrong happened: " + e);
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

            shell(ftpClient);

        } catch (IOException e) {
            System.out.println("Oops! Something wrong happened");
            e.printStackTrace();
        }
    }
}