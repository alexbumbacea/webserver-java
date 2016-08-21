package com.bumbacea.server.controller;

import com.bumbacea.server.Request;
import com.bumbacea.server.Response;
import com.bumbacea.server.ResponseFile;
import com.bumbacea.server.ServerConfig;

import java.io.File;

public class FileServerController extends AbstractController {
    private final ServerConfig serverConfig;

    public FileServerController(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public Response handle(Request req) {

        //static files
        switch (req.getMethod()) {
            case Request.METHOD_GET:
            case Request.METHOD_HEAD:
                return this.reply(req);
            default:
                return new Response(405);
        }
    }


    private Response reply(Request r) {
        File f = new File(serverConfig.getPath() + '/' + r.getPath());

        if (r.getPath().contains("/../")) {
            return new Response(403, "/../ not allowed in path");
        }

        if (!f.exists()) {
            return new Response(404, "File not found");
        }

        if (!f.isFile()) {
            //log
            return new Response(403, "Is not a file");
        }

        if (!f.canRead()) {
            //log
            return new Response(403, "No read permission for webserver");
        }

        return new ResponseFile(f, r.getMethod());
    }


}
