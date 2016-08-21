package com.bumbacea.server;

import java.util.regex.Pattern;

public class RouteMatcherRegex extends RouteMatch {
    protected Pattern pattern;

    public RouteMatcherRegex(String path, String method) {
        super(path, method);
        pattern = Pattern.compile(path);
    }

    @Override
    public boolean match(Request r) {
        if (!r.getMethod().equals(this.method)) {
            return false;
        }

        return pattern.matcher(r.getPath()).matches();
    }
}
