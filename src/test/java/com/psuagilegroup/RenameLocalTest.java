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
import java.io.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
    public void setup() throws IOException{
        sysOut = System.out;
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @After
    public void cleanup() throws IOException{
        System.setOut(sysOut);
    }


    @Test
    public void RENAME_LOCAL_FAILS_TEST() throws IOException{
        // Setup Mocks
    //    when(fc.login(save.user, save.pass)).thenReturn(true);
        String old = "text.txt";
        String NEW = "text1.txt";
        String[] args = new String[3];
        args[0] = "rn";
        args[1] = old;
        args[2] = NEW;
        // Run the command
        command.run(session, args);
        Assert.assertEquals("Oop, something is wrong",
                testOut.toString());

    }



}
