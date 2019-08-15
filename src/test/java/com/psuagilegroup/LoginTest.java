package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginTest {


    @Mock
    FTPClient fc;

    FTPSession session = new FTPSession();

    @InjectMocks
    Command command = new loginCommand(fc);


    // Setup Mock returns
    connectInfo save = new connectInfo("127.0.0.1", 21, "mocuser", "mocpass");

    @Before
    public void setup() throws IOException {

        // Setting up some default information
        session.output = "";
        session.remote_directory = "";
        session.local_directory = "";
        session.save = save;

        // Setup Mocked Server calls that are the same
        doNothing().when(fc).connect(save.server, save.port);
        when(fc.getReplyStrings()).thenReturn(new String[0]);
        when(fc.printWorkingDirectory()).thenReturn("");
        when(fc.getReplyCode()).thenReturn(221);
    }


    @Test
    public void loginTest() throws IOException {
        // Setup Mocks
        when(fc.login(save.user, save.pass)).thenReturn(true);

        // Run the command
        command.run(session, new String[0]);

        // Test that this worked
        Assert.assertEquals("", session.remote_directory);
        Assert.assertEquals("LOGGED INTO SERVER", session.output);
        Assert.assertTrue(new ReflectionEquals(new connectInfo("127.0.0.1", 21, "mocuser", "mocpass")).matches(save));
    }

    @Test
    public void loginTestFail() throws IOException {
        // Setup Mocks
        when(fc.login(save.user, save.pass)).thenReturn(false);

        // Run the command
        command.run(session, new String[0]);

        // Test that this worked
        Assert.assertEquals("", session.remote_directory);
        Assert.assertEquals("COULD NOT LOGIN TO SERVER", session.output);
        Assert.assertTrue(new ReflectionEquals(new connectInfo("127.0.0.1", 21, "mocuser", "mocpass")).matches(save));
    }

}
