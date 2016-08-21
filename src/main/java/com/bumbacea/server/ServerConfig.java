package com.bumbacea.server;

import com.bumbacea.server.controller.AbstractController;
import com.bumbacea.server.controller.FileServerController;

import java.util.HashMap;

public class ServerConfig {
    public static final int KEEP_ALIVE_TIME_POOL = 120;
    /**
     * Maximum time to wait for request from client once was connected (in ms)
     */
    public static int HTTP_SOCKET_TIMEOUT = 5000;
    /**
     * Number of parallel connections accepted in the same time
     */
    public static int POOL_SIZE = 20;
    private int port;
    private String path;
    private HashMap<RouteMatch, AbstractController> mappings;
    private AbstractController defaultController;

    public ServerConfig(){
        this.mappings = new HashMap<>();
        this.defaultController = new FileServerController(this);
    }

    public String getPath() {
        return path;
    }

    public int getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setPort(String port) {
        this.port = Integer.parseInt(port);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addMapping(String method, String path, AbstractController controller) {
        this.mappings.put(new RouteMatch(path, method), controller);
    }

    public HashMap<RouteMatch, AbstractController> getMappings()
    {
        return this.mappings;
    }

    public AbstractController getDefaultController() {
        return defaultController;
    }
}
