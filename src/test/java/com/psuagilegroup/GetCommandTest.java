package com.psuagilegroup;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.net.ftp.FTPClient;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.io.ByteArrayInputStream;

/**
 * Unit test for simple GetCommand.
 */
public class GetCommandTest
        extends TestCase
{
    FTPClient ftpClient;
    FTPSession currentSession;
    HashMap<String, Command> commands;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public GetCommandTest( String testName )
    {
        super( testName );

        // init ftpClient
        ftpClient = new FTPClient();

        // init currentSession
        currentSession = new FTPSession();
        currentSession.save = new connectInfo("ftpupload.net", 21, "epiz_24139835", "OkmvEHWbl4HFJ8a");

        // init useful commands
        commands = new HashMap<>();
        commands.put("login", new loginCommand(ftpClient));
        commands.put("logout", new logoutCommand(ftpClient));
        commands.put("cd", new cdCommand(ftpClient));
        commands.put("ls", new lsCommand(ftpClient));
        commands.put("rls", new rlsCommand(ftpClient));
        // my command to test
        commands.put("get", new getCommand(ftpClient));

        // login before test
        commands.get("login").run(currentSession, new String[0]);
        String [] testStr = {"cd", "htdocs"};
        commands.get("cd").run(currentSession, testStr);
        assertEquals("", currentSession.output);

    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( GetCommandTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testGetCommand()
    {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        String finalOutput = "";


        // test command argument list
        String [] testStr1 = {"get", "UnitTest/file1.html", "UnitTest"};
        commands.get("get").run(currentSession, testStr1);
        assertTrue(outContent.toString().contains("missing file operand"));
        finalOutput += outContent; outContent.reset();

        // remote file does not exist
        String [] testStr2 = {"get", "UnitTest/file300.html"};
        commands.get("get").run(currentSession, testStr2);
        assertTrue(outContent.toString().contains("file300.html"));
        assertTrue(outContent.toString().contains("does not exist."));
        finalOutput += outContent; outContent.reset();

        // get single file pass
        String [] testStr3 = {"get", "UnitTest/file.html"};
        commands.get("get").run(currentSession, testStr3);
        assertTrue(outContent.toString().contains("saved to file.html"));
        finalOutput += outContent; outContent.reset();

        // optionally, reset System.in to its original
        System.setIn(System.in);
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.out.println(finalOutput);
    }
}