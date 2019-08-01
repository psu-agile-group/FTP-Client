package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.*;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RlsTest {
    private PrintStream sysOut;
    private ByteArrayOutputStream testOut;
    private ByteArrayInputStream testIn;

    @Mock
    FTPClient fc;
    FTPSession session = new FTPSession();
    FTPFile mockFtpDirectory = new FTPFile();
    FTPFile mockFtpFiles = new FTPFile();

    @InjectMocks
    Command command = new rlsCommand(fc);

    @Before
    public void setup() throws IOException{
        sysOut = System.out;
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
        mocksetup();
    }

    private void mocksetup(){
        mockFtpDirectory.setName("htdocs");
        mockFtpDirectory.setType(FTPFile.DIRECTORY_TYPE);
        //when(mockFtpDirectory.isFile()).thenReturn(false);
        //when(mockFtpDirectory.isDirectory()).thenReturn(true);
        mockFtpDirectory.setPermission(0,2,true);
        mockFtpDirectory.setPermission(1,2,true);
        mockFtpDirectory.setPermission(2,2,true);

        mockFtpFiles.setName("index1.html");
        mockFtpFiles.setType(FTPFile.FILE_TYPE);
        //when(mockFtpFiles.isFile()).thenReturn(true);
        //when(mockFtpFiles.isDirectory()).thenReturn(false);
        mockFtpFiles.setPermission(0,2,true);
        mockFtpFiles.setPermission(1,2,true);
        mockFtpFiles.setPermission(2,2,true);

    }

    @After
    public void cleanup() throws IOException{
        System.setOut(sysOut);
    }

    @Test
    public void rlsTestNotFound() throws IOException{

        String[] args = new String[]{"rls", "123.html"};
        when(fc.listFiles(any(String.class))).thenReturn(new FTPFile[]{mockFtpFiles, mockFtpDirectory});

        command.run(session, args);

        Assert.assertFalse(testOut.toString().contains("123.html"));
    }

    @Test
    public void rlsTestListFile() throws IOException{

        String[] args = new String[]{"rls", "index.html"};
        when(fc.listFiles(any(String.class))).thenReturn(new FTPFile[]{mockFtpFiles});

        command.run(session, args);

        Assert.assertTrue(testOut.toString().contains("index1.html"));
    }

    @Test
    public void rlsTestListDirectory() throws IOException{

        String[] args = new String[]{"rls", "htdocs"};
        when(fc.listFiles(any(String.class))).thenReturn(new FTPFile[]{mockFtpDirectory});

        command.run(session, args);

        Assert.assertTrue(testOut.toString().contains("htdocs/"));
    }

    @Test
    public void rlsTestWitNoOption() throws IOException{

        String[] args = new String[]{"rls"};
        when(fc.listFiles(any(String.class))).thenReturn(new FTPFile[]{mockFtpFiles, mockFtpDirectory});

        command.run(session, args);

        Assert.assertTrue(testOut.toString().contains("index1.html") && testOut.toString().contains("htdocs/") );
    }

    @Test
    public void rlsTestWitLongListingOption() throws IOException{

        String[] args = new String[]{"rls", "-l"};
        when(fc.listFiles(any(String.class))).thenReturn(new FTPFile[]{mockFtpFiles, mockFtpFiles});

        command.run(session, args);

        Assert.assertTrue(testOut.toString().contains("null"));
    }
}