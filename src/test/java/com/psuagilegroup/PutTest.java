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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PutTest {
    private PrintStream sysOut;
    private ByteArrayOutputStream testOut;
    private ByteArrayInputStream testIn;

    @Mock
    FTPClient fc;
    FTPSession session = new FTPSession();

    @InjectMocks
    Command command = new putCommand(fc);

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
    public void putTestBadArguments() throws IOException{
        // Setup Mocks
        String[] args = new String[]{"put", "file"};
        // Run the command
        command.run(session, args);
        // Test it
        Assert.assertTrue(session.output.indexOf("put: missing file operand") == 0);
    }

    @Test
    public void putTestLocalFileNotExist() throws IOException{
        // Setup Mocks
        String[] args = new String[]{"put", "UnitTest/fileNE", "remoteDir"};
        // Run the command
        command.run(session, args);
        // Test it
        Assert.assertTrue(testOut.toString().indexOf(" - local: \"UnitTest/fileNE\" is invalid.") == 0);
    }

    @Test
    public void putTestRemoteDirNotExist() throws IOException{
        // Setup Mocks
        String[] args = new String[]{"put", "UnitTest/file.txt", "remoteDir"};
        when(fc.changeWorkingDirectory(any(String.class))).thenReturn(false);
        // Run the command
        command.run(session, args);
        // Test it
        Assert.assertTrue(testOut.toString().indexOf(" - remote folder: \"remoteDir\" is invalid.") == 0);
    }

    @Test
    public void putTestFileSuccess() throws IOException{
        // Setup Mocks
        String[] args = new String[]{"put", "UnitTest/file.txt", "remoteDir"};
        when(fc.changeWorkingDirectory(any(String.class))).thenReturn(true);
        when(fc.listNames(any(String.class))).thenReturn(new String[]{});
        when(fc.storeFile(any(String.class), any(InputStream.class))).thenReturn(true);
        // Run the command
        command.run(session, args);
        // Test it
        Assert.assertTrue(testOut.toString().indexOf(" - from : [UnitTest/file.txt] to [remoteDir/file.txt], upload ok.") == 0);
    }

    @Test
    public void putTestFileOverwriteSuccess() throws IOException{
        // Setup Mocks
        String[] args = new String[]{"put", "UnitTest/file.txt", "remoteDir"};
        when(fc.changeWorkingDirectory(any(String.class))).thenReturn(true);
        when(fc.listNames(any(String.class))).thenReturn(new String[]{""});
        when(fc.storeFile(any(String.class), any(InputStream.class))).thenReturn(true);
        testIn = new ByteArrayInputStream("y".getBytes());
        System.setIn(testIn);
        // Run the command
        command.run(session, args);
        // Test it
        Assert.assertTrue(testOut.toString().contains(" - from : [UnitTest/file.txt] to [remoteDir/file.txt], upload ok."));
    }

    @Test
    public void putTestFileOverwriteDenied() throws IOException{
        // Setup Mocks
        String[] args = new String[]{"put", "UnitTest/file.txt", "remoteDir"};
        when(fc.changeWorkingDirectory(any(String.class))).thenReturn(true);
        when(fc.listNames(any(String.class))).thenReturn(new String[]{""});
        when(fc.storeFile(any(String.class), any(InputStream.class))).thenReturn(true);
        testIn = new ByteArrayInputStream("n".getBytes());
        System.setIn(testIn);
        // Run the command
        command.run(session, args);
        // Test it
        Assert.assertTrue(testOut.toString().contains("- uploading [ UnitTest/file.txt ] is skipped."));
    }

    @Test
    public void putTestFolderFailed() throws IOException{
        // Setup Mocks
        String[] args = new String[]{"put", "UnitTest/folder1", "remoteDir"};
        when(fc.changeWorkingDirectory(any(String.class))).thenReturn(true);
        when(fc.makeDirectory(any(String.class))).thenReturn(false);
        when(fc.listNames(any(String.class))).thenReturn(new String[]{});
        // Run the command
        command.run(session, args);
        // Test it
        Assert.assertTrue(testOut.toString().contains(" to [remoteDir/folder1/file1.txt], upload failed."));
        Assert.assertTrue(testOut.toString().contains(" to [remoteDir/folder1/foler2/file2.txt], upload failed."));
    }

    @Test
    public void putTestFolderSuccess() throws IOException{
        // Setup Mocks
        String[] args = new String[]{"put", "UnitTest/folder1", "remoteDir"};
        when(fc.changeWorkingDirectory(any(String.class))).thenReturn(true);
        when(fc.makeDirectory(any(String.class))).thenReturn(true);
        when(fc.listNames(any(String.class))).thenReturn(new String[]{});
        when(fc.storeFile(any(String.class), any(InputStream.class))).thenReturn(true);
        // Run the command
        command.run(session, args);
        // Test it
        Assert.assertTrue(testOut.toString().contains(" to [remoteDir/folder1/file1.txt], upload ok."));
        Assert.assertTrue(testOut.toString().contains(" to [remoteDir/folder1/foler2/file2.txt], upload ok."));
    }

    @Test
    public void putTestFolderOverwriteSuccess() throws IOException{
        // Setup Mocks
        String[] args = new String[]{"put", "UnitTest/folder1", "remoteDir"};
        when(fc.changeWorkingDirectory(any(String.class))).thenReturn(true);
        when(fc.makeDirectory(any(String.class))).thenReturn(true);
        when(fc.listNames(any(String.class))).thenReturn(new String[]{""});
        when(fc.storeFile(any(String.class), any(InputStream.class))).thenReturn(true);
        testIn = new ByteArrayInputStream("yes".getBytes());
        System.setIn(testIn);
        // Run the command
        command.run(session, args);
        // Test it
        Assert.assertTrue(testOut.toString().contains(" to [remoteDir/folder1/file1.txt], upload ok."));
        Assert.assertTrue(testOut.toString().contains(" to [remoteDir/folder1/foler2/file2.txt], upload ok."));
    }

    @Test
    public void putTestFolderOverwriteDenied() throws IOException{
        // Setup Mocks
        String[] args = new String[]{"put", "UnitTest/folder1", "remoteDir"};
        when(fc.changeWorkingDirectory(any(String.class))).thenReturn(true);
        when(fc.makeDirectory(any(String.class))).thenReturn(true);
        when(fc.listNames(any(String.class))).thenReturn(new String[]{""});
        when(fc.storeFile(any(String.class), any(InputStream.class))).thenReturn(true);
        testIn = new ByteArrayInputStream("no".getBytes());
        System.setIn(testIn);
        // Run the command
        command.run(session, args);
        // Test it
        Assert.assertTrue(testOut.toString().contains("- uploading [ UnitTest/folder1/file1.txt ] is skipped."));
        Assert.assertTrue(testOut.toString().contains("- uploading [ UnitTest/folder1/foler2/file2.txt ] is skipped."));
    }

}
