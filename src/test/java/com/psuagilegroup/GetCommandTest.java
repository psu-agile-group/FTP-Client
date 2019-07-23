package com.psuagilegroup;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * Unit test for simple GetCommand.
 */
@RunWith(JUnit4.class)
public class GetCommandTest
{
    private FTPClient ftpClient;
    private FTPSession currentSession;
    private HashMap<String, Command> commands;

    private String finaOutString;
    private String testString[];
    private PrintStream originalOut;
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
        commands.put("get", new getCommand(ftpClient));

        // login before test
        commands.get("login").run(currentSession, new String[0]);
        testString = new String[]{"cd", "htdocs"};
        commands.get("cd").run(currentSession, testString);

        // make sure cd to htdocs works
        assertEquals("", currentSession.output);

        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        finaOutString = "";
    }

    @After
    public void cleanup() {
        // optionally, reset System.in to its original
        System.setOut(originalOut);
        System.out.println(finaOutString);
    }

    @Test
    public void testGetCommand()
    {
        // test command argument list
        testString = new String[]{"get", "UnitTest/file1.html", "UnitTest"};
        commands.get("get").run(currentSession, testString);
        assertTrue(outContent.toString().contains("missing file operand"));
        finaOutString += outContent;
        outContent.reset();

        // remote file does not exist
        testString = new String[]{"get", "UnitTest/file300.html"};
        commands.get("get").run(currentSession, testString);
        assertTrue(outContent.toString().contains("\"UnitTest/file300.html\" does not exist."));
        finaOutString += outContent;
        outContent.reset();

        // get single file pass
        testString = new String[]{"get", "UnitTest/file.html"};
        commands.get("get").run(currentSession, testString);
        assertTrue(outContent.toString().contains("saved to file.html"));
        finaOutString += outContent;
        outContent.reset();
    }
}