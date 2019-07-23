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
 * Unit test for simple PutCommand.
 */
public class PutCommandTest
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
    public PutCommandTest( String testName )
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
        commands.put("put", new putCommand(ftpClient));

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
        return new TestSuite( PutCommandTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testPutCommand()
    {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        String finalOutput = "";


        // local file does not exist
        String [] testStr1 = {"put", "UnitTest/file1.html", "UnitTest"};
        commands.get("put").run(currentSession, testStr1);
        assertTrue(outContent.toString().contains(" - local: \"UnitTest/file1.html\" is invalid."));
        finalOutput += outContent; outContent.reset();

        // remote folder does not exist
        String [] testStr2 = {"put", "UnitTest/file.html", "UnitTest1"};
        commands.get("put").run(currentSession, testStr2);
        assertTrue(outContent.toString().contains(" - remote folder: \"UnitTest1\" is invalid."));
        finalOutput += outContent; outContent.reset();

        // put single file pass
        ByteArrayInputStream in1 = new ByteArrayInputStream("y".getBytes());
        System.setIn(in1);
        String [] testStr3 = {"put", "UnitTest/file.html", "UnitTest"};
        commands.get("put").run(currentSession, testStr3);
        assertTrue(outContent.toString().contains(" - from : [UnitTest/file.html] to [UnitTest/file.html], upload ok."));
        finalOutput += outContent; outContent.reset();

        // put multiple files pass
        ByteArrayInputStream in2 = new ByteArrayInputStream("yes".getBytes());
        System.setIn(in2);
        String [] testStr4 = {"put", "UnitTest/file.html", "UnitTest/file2.html", "UnitTest"};
        commands.get("put").run(currentSession, testStr4);
        assertTrue(outContent.toString().contains(" - from : [UnitTest/file.html] to [UnitTest/file.html], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/file2.html] to [UnitTest/file2.html], upload ok."));
        finalOutput += outContent; outContent.reset();

        // put folder pass
        ByteArrayInputStream in3 = new ByteArrayInputStream("yes".getBytes());
        System.setIn(in3);
        String [] testStr5 = {"put", "UnitTest/folder", "UnitTest"};
        commands.get("put").run(currentSession, testStr5);
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/hi test/README_2.md] to [UnitTest/folder/hi test/README_2.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/README.md] to [UnitTest/folder/README.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/README00 1.md] to [UnitTest/folder/README00 1.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/test22/README_3.md] to [UnitTest/folder/test22/README_3.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/test22/README_4 - Copy.md] to [UnitTest/folder/test22/README_4 - Copy.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/test22/test 44/README_6 - Copy.md] to [UnitTest/folder/test22/test 44/README_6 - Copy.md], upload ok."));
        assertTrue(outContent.toString().contains(" - from : [UnitTest/folder/test22/test33/README_5.md] to [UnitTest/folder/test22/test33/README_5.md], upload ok."));
        finalOutput += outContent; outContent.reset();

        // optionally, reset System.in to its original
        System.setIn(System.in);
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.out.println(finalOutput);
    }
}