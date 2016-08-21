package com.bumbacea.server;

public class RouteMatch {
    protected String method;
    private String path;

    public RouteMatch(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public boolean match(Request r) {
        return r.getMethod().equals(method) && r.getPath().equals(path);
    }
}
