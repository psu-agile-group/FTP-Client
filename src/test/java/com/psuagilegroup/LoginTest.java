package com.psuagilegroup;

import org.apache.commons.net.ftp.FTPClient;
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
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginTest {


    @Mock
    FTPClient fc = mock(FTPClient.class);

    FTPSession session = new FTPSession();

    @InjectMocks
    Command command = new loginCommand(fc);

    // Setup Mock returns
    connectInfo save = new connectInfo( "127.0.0.1", 21, "mocuser", "mocpass");
    String username = "mocuser";
    String password = "mocpass";
    String server   = "127.0.0.1";
    int port = 21;

    @Before
    public void setup() throws IOException{

        session.output = "";
        session.remote_directory = "";
        session.local_directory = "";
        session.save = save;

        // Setup Mocked Server calls
        doThrow(SocketException.class).doNothing().when(fc).connect(server, port);
        when(fc.login(username, password)).thenReturn(true);
        when(fc.getReplyCode()).thenReturn(221);
    }


    @Test
    public void loginTest(){
        command.run(session, new String[0]);
    }

}
