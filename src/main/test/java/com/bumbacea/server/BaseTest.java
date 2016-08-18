package com.bumbacea.server;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.*;
import org.junit.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class BaseTest {

    private Webserver webServer;
    private int port = 8888;

    @Before
    public void setUp() {
        webServer = new Webserver(port, "/tmp");
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

    // inspired from apache http client demo pages
    private HttpResponse getHttpResponse(String path, String method) throws IOException, HttpException {
        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new RequestContent())
                .add(new RequestTargetHost())
                .add(new RequestConnControl())
                .add(new RequestUserAgent("Test/1.1"))
                .add(new RequestExpectContinue(true)).build();
        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

        HttpCoreContext coreContext = HttpCoreContext.create();
        HttpHost host = new HttpHost("127.0.0.1", port);
        coreContext.setTargetHost(host);

        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);

        Socket socket = new Socket("127.0.0.1", port);


        conn.bind(socket);

        BasicHttpRequest request = new BasicHttpRequest(method, path);

        httpexecutor.preProcess(request, httpproc, coreContext);
        HttpResponse response = httpexecutor.execute(request, conn, coreContext);
        httpexecutor.postProcess(response, httpproc, coreContext);
        return response;
    }

    @After
    public void teadDown()
    {
        webServer.interrupt();
    }


}
