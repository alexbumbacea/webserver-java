package com.bumbacea.server;

import com.bumbacea.server.controller.AbstractController;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by abumbacea on 21/08/16.
 */
public class DynamicPageTest extends BaseTest {
    private Webserver webServer;

    @Before
    public void setUp() {
        this.port = 8888;
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(port);
        serverConfig.setPath("/tmp");
        webServer = new Webserver(serverConfig);
        ServerConfig.POOL_SIZE = 3;
        serverConfig.addMapping("GET", "/test", new AbstractController() {
            @Override
            public Response handle(Request req) {
                return new Response(302);
            }
        });
        webServer.start();
    }

    @Test
    public void testDynamicHandler() throws IOException, HttpException {
        HttpResponse response = getHttpResponse("/test", "GET");
        Assert.assertEquals(302, response.getStatusLine().getStatusCode());
    }


    @Test
    public void testDynamicHandlerWithQueryString() throws IOException, HttpException {
        HttpResponse response = getHttpResponse("/test?demo=test", "GET");
        Assert.assertEquals(302, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testDynamicWithException() throws IOException, HttpException {
        webServer.serverConfig.addMapping("GET", "/err", new AbstractController() {
            @Override
            public Response handle(Request req) {
                throw new RuntimeException("error");
            }
        });
        HttpResponse response = getHttpResponse("/err?demo=test", "GET");
        Assert.assertEquals(500, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testDynamicWithParam() throws IOException, HttpException {
        webServer.serverConfig.addMapping("GET", "/name", new AbstractController() {
            @Override
            public Response handle(Request req) {
                return new Response(req.queryString.get("name"));
            }
        });
        HttpResponse response = getHttpResponse("/name?name=alex", "GET");
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        byte[] bytes = new byte[1024];
        response.getEntity().getContent().read(bytes);
        Assert.assertEquals("alex", new String(bytes, StandardCharsets.UTF_8).trim());
    }

    @After
    public void tearDown() {
        webServer.interrupt();
    }

}
