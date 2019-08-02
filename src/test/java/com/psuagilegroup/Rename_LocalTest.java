package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.io.*;
import java.nio.file.FileSystemException;

public class Rename_LocalTest {
    private PrintStream sysOut;
    private ByteArrayOutputStream testOut;
    private ByteArrayInputStream testIn;

    @Mock
    FTPClient fc;
    FTPSession session = new FTPSession();

    @InjectMocks
    Command command = new rnCommand(fc);

    @Before
    public void setup(){
        sysOut = System.out;
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @After
    public void cleanup() throws IOException{
        System.setOut(sysOut);
    }

    @Test
    public void NOT_ENOUGH_ARGUMENT_FAIL_TEST() {
        String args [] = new String[]{"rn","test2.txt"};

        // Run the command
        command.run(session, args);
        Assert.assertEquals("The format for renaming the file: 'rn old_file_name new_file_name"
                ,session.output);
    }

    @Test
    public void RENAME_LOCAL_FAILS_TEST(){
        // Setup Mocks
        String args [] = new String[]{"rn", "nofile.txt", "test2.txt"};

        // Run the command
        command.run(session, args);
        Assert.assertEquals("Oop, something is wrong",
                testOut.toString());

    }

    @Test
    public void RENAME_LOCAL_PASS_TEST() throws IOException {
        String args [] = new String[]{"rn", "test4.txt", "test3.txt"};
        // Run the command
        command.run(session, args);
        File filetoRenamed = new File("test3.txt");
        boolean isRenamed = filetoRenamed.renameTo(new File("test4.txt"));
        if (!isRenamed) {
            throw new FileSystemException("test3.txt");
        }
    }
}
