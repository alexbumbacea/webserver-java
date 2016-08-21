package com.bumbacea.server;

import java.util.HashMap;
import java.util.List;

public class Request {
    public final static String METHOD_GET = "GET";
    public final static String METHOD_HEAD = "HEAD";

    protected String method = METHOD_GET;
    protected String protocol = "HTTP/1.1";
    protected String path = "/";
    protected HashMap<String, String> queryString = new HashMap<>();
    protected HashMap<String, String> headers = new HashMap<>();

    public Request(List<String> headers) {
        String[] firstLine = headers.get(0).split(" ");
        this.method = firstLine[0];

        int separator = firstLine[1].indexOf("?");
        if (separator == -1) {
            this.path = firstLine[1];
        } else {
            this.path = firstLine[1].substring(0, separator);
            String[] qs = firstLine[1].substring(separator + 1).split("&");
            for (String q : qs) {
                int separatorEqual = q.indexOf("=");
                if (separatorEqual != -1) {
                    this.queryString.put(q.substring(0, separatorEqual), q.substring(separatorEqual + 1));
                } else {
                    this.queryString.put(q, "1");
                }
            }
        }

        this.protocol = firstLine[2];

        headers.remove(0);

        for (String line : headers) {
            String[] lineSplit = line.split(":");
            this.headers.put(lineSplit[0], lineSplit[1]);
        }
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


    public HashMap<String, String> getQueryString() {
        return queryString;
    }
}
