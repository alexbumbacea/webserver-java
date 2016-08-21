package com.bumbacea.server;


import com.bumbacea.server.controller.AbstractController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection implements Runnable {
    private Socket clientSocket;
    private ServerConfig serverConfig = new ServerConfig();

    private static final Logger logger = Logger.getLogger( Connection.class.getName() );

    public Connection(Socket clientSocket, ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.clientSocket = clientSocket;

    }

    public void run() {
        try {
            Request req = this.getRequest();
            Response res = this.handleRequest(req);
            res.write(this.clientSocket.getOutputStream());
            logger.log(Level.FINE, "Replied with " + res.statusCode + " on " + req.path + " for client " + clientSocket.getRemoteSocketAddress().toString());
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        try {
            //try close connection if something goes wrong
            this.clientSocket.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private Request getRequest() throws IOException {
        List<String> headers =  new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while(true) {
            String headerAsString = reader.readLine();
            if (headerAsString.isEmpty()) {
                break;
            } else {
                headers.add(headerAsString);
            }
        }

        return new Request(headers);
    }

    private Response handleRequest(Request r) {
        try {
            //dynamic urls
            for (Map.Entry<RouteMatch, AbstractController> route : serverConfig.getMappings().entrySet()) {
                if (route.getKey().match(r)) {
                    return route.getValue().handle(r);
                }
            }

            return serverConfig.getDefaultController().handle(r);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new Response(500);
        }
    }
}
