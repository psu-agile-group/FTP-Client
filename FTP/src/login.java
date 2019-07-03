
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
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
        public static void main(String[] args) {
            // Variable from the command line argument

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
                //Determine if a reply code is a positive completion response.
                // All codes beginning with a 2 are positive completion responses
                //True if a reply code is a positive completion response, false if not.
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
            } catch (IOException e) {
                System.out.println("Oops! Something wrong happened");
                e.printStackTrace();
            }
        }
    }

