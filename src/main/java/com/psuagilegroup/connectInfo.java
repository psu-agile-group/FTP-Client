package com.psuagilegroup;

import java.io.*;

public class connectInfo {
    String server;
    int port;
    String user;
    String pass;

    public connectInfo() {
    }

    public connectInfo(String server, int port, String user, String pass) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public void saveInfo() throws IOException {
        BufferedWriter bw = null;
        FileWriter fw = null;

        //output the information to the file
        try {
            fw = new FileWriter(".FTPClient");
            bw = new BufferedWriter(fw);

            bw.write(this.server);
            bw.newLine();
            bw.write(Integer.toString(this.port));
            bw.newLine();
            bw.write(this.user);
            bw.newLine();
            bw.write(this.pass);
            bw.close();
        } catch (IOException ex) {
            throw new IOException("Error to write to the file.");
        }
    }

    public void saveInfo(String server, int port, String user, String pass) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
        try {
            this.saveInfo();
        } catch (IOException EX) {
            //throw new IOException("Error to write to the file.");
            System.out.println(EX);
        }
    }

    public void readInfo() {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(".FTPClient");
            br = new BufferedReader(fr);

            this.server = br.readLine();
            this.port = Integer.parseInt(br.readLine());
            this.user = br.readLine();
            this.pass = br.readLine();
            br.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}


