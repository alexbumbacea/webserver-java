package com.bumbacea.server;

import java.util.regex.Pattern;

/**
 * Created by abumbacea on 21/08/16.
 */
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

        if (pattern.matcher(r.getPath()).matches()) {
            return true;
        }

        return super.match(r);
    }
}
