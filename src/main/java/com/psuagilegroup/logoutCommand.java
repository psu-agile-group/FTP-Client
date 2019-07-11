package com.psuagilegroup;


import org.apache.commons.net.ftp.FTPClient;

public class logoutCommand extends Command
{
    public logoutCommand(FTPClient ftpClient)
    {
        super(ftpClient);
    }

    @Override
    public Boolean run( String[] args )
    {
        try {
            this.ftpClient.logout();
        }
        catch(java.io.IOException e) {
            System.out.println("Exception caught, not handled");
        }
        System.out.println("Goodbye");
        System.exit(0);
        return true;
    }
}
