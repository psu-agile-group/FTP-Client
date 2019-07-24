package com.psuagilegroup;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * Unit test for simple PutCommand.
 */
@RunWith(JUnit4.class)
public class PutCommandTest
{
    private FTPClient ftpClient;
    private FTPSession currentSession;
    private HashMap<String, Command> commands;

    private String finaOutString;
    private String testString[];
    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayInputStream inContent;
    private ByteArrayOutputStream outContent;

    @Before
    public void init() {
        // init ftpClient
        ftpClient = new FTPClient();

        // init currentSession
        currentSession = new FTPSession();
        currentSession.save = new connectInfo("ftpupload.net", 21, "epiz_24139835", "OkmvEHWbl4HFJ8a");

        // init useful commands
        commands = new HashMap<>();
        commands.put("login", new loginCommand(ftpClient));
        commands.put("cd", new cdCommand(ftpClient));
        // my command to test
        commands.put("put", new putCommand(ftpClient));

        // login before test
        commands.get("login").run(currentSession, new String[0]);
        testString = new String[]{"cd", "htdocs"};
        commands.get("cd").run(currentSession, testString);

        // make sure cd to htdocs works
        assertEquals("", currentSession.output);

        originalIn = System.in;
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        finaOutString = "";
    }

    @After
    public void cleanup() {
        // optionally, reset System.in to its original
        System.setIn(originalIn);
        System.setOut(originalOut);
        System.out.println(finaOutString);
    }

    @Test
    public void testPutCommand()
    {
        // local file does not exist
        testString = new String[]{"put", "UnitTest/file1.html", "UnitTest"};
        commands.get("put").run(currentSession, testString);
        assertTrue(outContent.toString().contains(" - local: \"UnitTest/file1.html\" is invalid."));
        finaOutString += outContent; outContent.reset();

        // remote folder does not exist
        testString = new String[]{"put", "UnitTest/file.html", "UnitTest1"};
        commands.get("put").run(currentSession, testString);
        assertTrue(outContent.toString().contains(" - remote folder: \"UnitTest1\" is invalid."));
        finaOutString += outContent; outContent.reset();

        // put single file pass
        inContent = new ByteArrayInputStream("y".getBytes());
        System.setIn(inContent);
        testString = new String[]{"put", "UnitTest/file.html", "UnitTest"};
        commands.get("put").run(currentSession, testString);
        assertTrue(outContent.toString().contains(" - from : [UnitTest/file.html] to [UnitTest/file.html], upload ok."));
        finaOutString += outContent; outContent.reset();

        // put multiple files pass
        inContent = new ByteArrayInputStream("yes".getBytes());
        System.setIn(inContent);
        testString = new String[]{"put", "UnitTest/file.html", "UnitTest/file2.html", "UnitTest"};
        commands.get("put").run(currentSession, testString);
        assertTrue(outContent.toString().contains(" - from : [UnitTest/file.html] to [UnitTest/file.html], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/file2.html] to [UnitTest/file2.html], upload ok."));
        finaOutString += outContent; outContent.reset();

        // put folder pass
        inContent = new ByteArrayInputStream("yes".getBytes());
        System.setIn(inContent);
        testString = new String[]{"put", "UnitTest/folder", "UnitTest"};
        commands.get("put").run(currentSession, testString);
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/hi test/README_2.md] to [UnitTest/folder/hi test/README_2.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/README.md] to [UnitTest/folder/README.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/README00 1.md] to [UnitTest/folder/README00 1.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/test22/README_3.md] to [UnitTest/folder/test22/README_3.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/test22/README_4 - Copy.md] to [UnitTest/folder/test22/README_4 - Copy.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/test22/test 44/README_6 - Copy.md] to [UnitTest/folder/test22/test 44/README_6 - Copy.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/test22/test33/README_5.md] to [UnitTest/folder/test22/test33/README_5.md], upload ok."));
        finaOutString += outContent; outContent.reset();

    }
}