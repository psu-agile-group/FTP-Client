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
    private FTPFile mockFtpDirectory;
    private FTPFile mockFtpFiles;

    @Mock
    FTPClient fc;
    FTPSession session = new FTPSession();

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
        mockFtpDirectory = new FTPFile();
        mockFtpDirectory.setName("htdocs");
        mockFtpDirectory.setType(FTPFile.DIRECTORY_TYPE);
        mockFtpDirectory.setRawListing("drwxr-xr-x   11 24139835   24139835         4096 Jul 31 22:49 htdocs");

        mockFtpFiles = new FTPFile();
        mockFtpFiles.setName("index1.html");
        mockFtpFiles.setType(FTPFile.FILE_TYPE);
        mockFtpFiles.setRawListing("-rw-r--r--    1 0          2                   0 Jul  3 19:42 index.html");
    }

    @After
    public void cleanup() throws IOException{
        System.setOut(sysOut);
    }

    @Test
    public void rlsTestNotFound() throws IOException{

        String[] args = new String[]{"rls", "123.html"};
        when(fc.listFiles(any(String.class))).thenReturn(new FTPFile[]{});

        command.run(session, args);

        String testStr = testOut.toString().replace("\r", "").replace("\t", "").replace("\n", "");
        Assert.assertTrue(testStr.isEmpty());
    }

    @Test
    public void rlsTestListFile() throws IOException{

        String[] args = new String[]{"rls", "index.html"};
        when(fc.listFiles(any(String.class))).thenReturn(new FTPFile[]{mockFtpFiles});

        command.run(session, args);

        String testStr = testOut.toString().replace("\r", "").replace("\t", "").replace("\n", "");
        Assert.assertTrue(testStr.equals("index1.html"));
    }

    @Test
    public void rlsTestListDirectory() throws IOException{

        String[] args = new String[]{"rls", "htdocs"};
        when(fc.listFiles(any(String.class))).thenReturn(new FTPFile[]{mockFtpDirectory});

        command.run(session, args);

        String testStr = testOut.toString().replace("\r", "").replace("\t", "").replace("\n", "");
        Assert.assertTrue(testStr.equals("htdocs/"));
    }

    @Test
    public void rlsTestWitNoOption() throws IOException{

        String[] args = new String[]{"rls"};
        when(fc.listFiles(any(String.class))).thenReturn(new FTPFile[]{mockFtpFiles, mockFtpDirectory});

        command.run(session, args);

        String testStr = testOut.toString().replace("\r", "").replace("\t", "").replace("\n", "");
        Assert.assertTrue(testOut.toString().contains("index1.html") && testOut.toString().contains("htdocs/") );
        testStr = testStr.replace("index1.html", "");
        Assert.assertTrue(testStr.equals("htdocs/") );
    }

    @Test
    public void rlsTestWitLongListingOption() throws IOException{

        String[] args = new String[]{"rls", "-l"};
        when(fc.listFiles(any(String.class))).thenReturn(new FTPFile[]{mockFtpFiles, mockFtpDirectory});

        command.run(session, args);

        String testStr = testOut.toString().replace("\r", "").replace("\t", "").replace("\n", "");
        Assert.assertTrue(testOut.toString().contains("drwxr-xr-x   11 24139835   24139835         4096 Jul 31 22:49 htdocs/")
                && testOut.toString().contains("-rw-r--r--    1 0          2                   0 Jul  3 19:42 index.html") );
        testStr = testStr.replace("drwxr-xr-x   11 24139835   24139835         4096 Jul 31 22:49 htdocs/", "");
        Assert.assertTrue(testStr.equals("-rw-r--r--    1 0          2                   0 Jul  3 19:42 index.html") );
    }
}