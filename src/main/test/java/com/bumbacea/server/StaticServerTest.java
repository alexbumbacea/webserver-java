package com.bumbacea.server;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by abumbacea on 21/08/16.
 */
public class StaticServerTest extends BaseTest {
    private Webserver webServer;

    @Before
    public void setUp() {
        this.port = 8888;
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(port);
        serverConfig.setPath("/tmp");
        webServer = new Webserver(serverConfig);
        ServerConfig.POOL_SIZE = 3;
        webServer.start();
    }

    @Test
    public void testForbiddenFolderListing() throws IOException, HttpException {
        String path = "/";
        String method = "GET";
        HttpResponse response = getHttpResponse(path, method);

        Assert.assertEquals(403, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testForbiddenFolderListing2ConsecutiveRequests() throws IOException, HttpException {
        //test that server is not closed after first request
        String path = "/";
        String method = "GET";
        HttpResponse response = getHttpResponse(path, method);
        Assert.assertEquals(403, response.getStatusLine().getStatusCode());
        HttpResponse response2 = getHttpResponse(path, method);
        Assert.assertEquals(403, response2.getStatusLine().getStatusCode());
    }

    @Test
    public void testParallelConnections() throws IOException, HttpException {
        Socket s1 = new Socket("127.0.0.1", port);
        Socket s2 = new Socket("127.0.0.1", port);
        s1.getOutputStream().write("a".getBytes());
        s2.getOutputStream().write("a".getBytes());
        s1.close();
        s2.close();
    }

    @Test
    public void testFileFound() throws IOException, HttpException {
        String method = "GET";

        PrintWriter writer = new PrintWriter("/tmp/webtest.txt", "UTF-8");
        String text = "The first line\nThe second line";
        writer.println(text);
        writer.close();


        HttpResponse response = getHttpResponse("/webtest.txt", method);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testFileFoundWithQueryString() throws IOException, HttpException {
        String method = "GET";

        PrintWriter writer = new PrintWriter("/tmp/webtest.txt", "UTF-8");
        String text = "The first line\nThe second line";
        writer.println(text);
        writer.close();

        HttpResponse response = getHttpResponse("/webtest.txt?blabla", method);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }
    @After
    public void tearDown()
    {
        webServer.interrupt();
    }

}
