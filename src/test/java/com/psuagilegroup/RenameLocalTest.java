package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.*;
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
    public void RENAME_LOCAL_FAILS_TEST(){
        // Setup Mocks
    //    when(fc.login(save.user, save.pass)).thenReturn(true);
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
        Assert.assertEquals("Locally, test4.txt was successfully renamed to: test3.txt", testOut.toString());
    }

}
