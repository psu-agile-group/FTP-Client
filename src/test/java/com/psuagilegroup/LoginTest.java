package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;
import java.net.SocketException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginTest {


    @Mock
    FTPClient fc;

    FTPSession session = new FTPSession();

    @InjectMocks
    Command command = new loginCommand(fc);


    // Setup Mock returns
    connectInfo save = new connectInfo( "127.0.0.1", 21, "mocuser", "mocpass");

    @Before
    public void setup() throws IOException{

        // Setting up some default information
        session.output = "";
        session.remote_directory = "";
        session.local_directory = "";
        session.save = save;

        // Setup Mocked Server calls
        doNothing().when(fc).connect(save.server, save.port);
        when(fc.login(save.user, save.pass)).thenReturn(true);
        when(fc.getReplyCode()).thenReturn(221);
        when(fc.getReplyStrings()).thenReturn(new String[0]);
        when(fc.printWorkingDirectory()).thenReturn("");
    }


    @Test
    public void loginTest(){
        command.run(session, new String[0]);
        // Test that this worked
        Assert.assertEquals("", session.remote_directory);
        Assert.assertEquals("LOGGED INTO SERVER", session.output);
        Assert.assertTrue(new ReflectionEquals(new connectInfo("127.0.0.1", 21, "mocuser", "mocpass")).matches(save) );
    }

}
