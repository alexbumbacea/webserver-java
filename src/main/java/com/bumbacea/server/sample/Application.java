package com.bumbacea.server.sample;

import com.bumbacea.server.ServerConfig;
import com.bumbacea.server.Webserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;

public class Application {
    public static void main(String [] args) throws IOException, ClassNotFoundException, InterruptedException {

        //setup logger to display in console
        FileInputStream configFile = new FileInputStream("logger.properties");
        LogManager.getLogManager().readConfiguration(configFile);

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(args[1]);
        serverConfig.setPath(args[0]);
        serverConfig.addMapping("GET", "/test.dynamic", new SampleController());
        serverConfig.addMapping("GET", "/test.dynamic2", new SampleController());

        Webserver server = new Webserver(serverConfig);

        server.start();
        while (server.isAlive()) {
            Thread.sleep(3000);
        }
        System.out.println("Exiting");

    }
}
