package com.bumbacea.server;

import java.util.HashMap;
import java.util.List;

public class Request {
    public final static String METHOD_GET = "GET";
    public final static String METHOD_HEAD = "HEAD";

    protected String method = METHOD_GET;
    protected String protocol = "HTTP/1.1";
    protected String path = "/";
    protected HashMap<String, String> headers = new HashMap<String, String>();

    public static Request fromString(List<String> headers)
    {
        //parsed according to https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
        Request request = new Request();
        String[] firstLine = headers.get(0).split(" ");
        request.method = firstLine[0];


        int separator = firstLine[1].indexOf("?");
        if (separator == -1) {
            request.path = firstLine[1];
        } else {
            request.path = firstLine[1].substring(0, separator);
        }

        request.protocol = firstLine[2];

        headers.remove(0);

        for (String line : headers) {
            String[] lineSplit= line.split(":");
            request.headers.put(lineSplit[0], lineSplit[1]);
        }

        return request;
    }

    public String getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPath() {
        return path;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }
}
