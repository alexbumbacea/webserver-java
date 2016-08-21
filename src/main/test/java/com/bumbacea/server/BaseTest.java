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

public abstract class BaseTest {
    protected int port;

    // inspired from apache http client demo pages
    protected HttpResponse getHttpResponse(String path, String method) throws IOException, HttpException {
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
        socket.setSoTimeout(10000);


        conn.bind(socket);

        BasicHttpRequest request = new BasicHttpRequest(method, path);

        httpexecutor.preProcess(request, httpproc, coreContext);
        HttpResponse response = httpexecutor.execute(request, conn, coreContext);
        httpexecutor.postProcess(response, httpproc, coreContext);
        socket.close();
        return response;
    }

}
