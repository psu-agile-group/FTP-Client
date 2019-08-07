package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

@RunWith(MockitoJUnitRunner.class)

public class RenameLocalTest {

    private PrintStream sysOut;
    private ByteArrayOutputStream testOut;
    private ByteArrayInputStream testIn;

    @Mock
    FTPClient fc;
    FTPSession session = new FTPSession();

    @InjectMocks
    Command command = new rnCommand(fc);


    @Before
    public void setup() {
        sysOut = System.out;
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @After
    public void cleanup() throws IOException {
        System.setOut(sysOut);
    }

    @Test
    public void NOT_ENOUGH_ARGUMENT_FAIL_TEST() {
        String args[] = new String[]{"rn", "UnitTest/test2.txt"};

        // Run the command
        command.run(session, args);
        Assert.assertEquals("The format for renaming the file: 'rn old_file_name new_file_name", session.output);
    }

    @Test
    public void RENAME_LOCAL_FAILS_TEST() {
        // Setup Mocks
        String args[] = new String[]{"rn", "UnitTest/nofile.txt", "UnitTest/test2.txt"};

        // Run the command
        command.run(session, args);
        Assert.assertEquals("Oop, something is wrong", testOut.toString());

    }

    @Test
    public void RENAME_LOCAL_PASS_TEST() throws IOException {
        // Setup Mocks
        String args1[] = new String[]{"rn", "UnitTest/test4.txt", "UnitTest/test3.txt"};
        String args2[] = new String[]{"rn", "UnitTest/test3.txt", "UnitTest/test4.txt"};

        // Run the command
        command.run(session, args1);
        Assert.assertTrue(testOut.toString().contains("successful"));

        command.run(session, args2);
        Assert.assertTrue(testOut.toString().contains("successful"));
    }

}
