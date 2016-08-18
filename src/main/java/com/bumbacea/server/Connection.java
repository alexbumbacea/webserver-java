package com.bumbacea.server;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection implements Runnable {
    private final Socket clientSocket;
    private static final Logger logger = Logger.getLogger( Connection.class.getName() );


    public Connection(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            this.processRequest();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    private void processRequest() {
        List<String> headers = new ArrayList<String>();
        try {
            Request req = this.getRequest(headers);
            Response res = this.handleRequest(req);
            res.write(this.clientSocket.getOutputStream());

            logger.log(Level.FINE, "Replied with " + res.statusCode + " on " + req.path );
            this.clientSocket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    private Request getRequest(List<String> headers) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while(true) {
            String headerAsString = reader.readLine();
            if (headerAsString.isEmpty()) {
                break;
            } else {
                headers.add(headerAsString);
            }
        }

        return Request.fromString(headers);
    }

    private Response handleRequest(Request r) {
        switch (r.method) {
            case Request.METHOD_GET:
            case Request.METHOD_HEAD:
                return this.reply(r);
            default:
                return new Response(405);
        }
    }

    private Response reply(Request r) {
        File f = new File(Webserver.basePath.getPath() + '/' + r.getPath());

        if (r.path.contains("/../")) {
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

        return new Response(f, r.method);
    }
}
