package com.bumbacea.server.sample;

import com.bumbacea.server.Webserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;

public class Application {
    public static void main(String [] args) throws IOException, ClassNotFoundException, InterruptedException {

        //setup logger to display in console
        FileInputStream configFile = new FileInputStream("logger.properties");
        LogManager.getLogManager().readConfiguration(configFile);


        Webserver server1 = new Webserver(Integer.parseInt(args[1]), args[0]);

        server1.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });

        server1.start();
        while (server1.isAlive()) {
            Thread.sleep(3000);
        }
        System.out.println("Exiting");

    }
}
