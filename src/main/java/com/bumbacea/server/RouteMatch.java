package com.bumbacea.server;

public class RouteMatch {
    private String path;
    private String method;

    public RouteMatch(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public boolean match(Request r) {
        return r.getMethod().equals(method) && r.getPath().equals(path);
    }
}
