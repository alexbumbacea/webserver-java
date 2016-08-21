package com.bumbacea.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Webserver extends Thread {


    private static final Logger logger = Logger.getLogger(Connection.class.getName());
    protected ServerConfig serverConfig;
    protected ServerSocket socket;
    private ExecutorService executor;

    public Webserver(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.checkPathIsValid();
        this.executor = new ThreadPoolExecutor(ServerConfig.POOL_SIZE, ServerConfig.POOL_SIZE, ServerConfig.KEEP_ALIVE_TIME_POOL, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        this.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private void checkPathIsValid() {
        File basePath = new File(serverConfig.getPath());
        if (!basePath.exists()) {
            throw new RuntimeException("Base path not available");
        }

        if (!basePath.isDirectory()) {
            throw new RuntimeException("Mentioned path is not a directory");
        }
    }

    public void run() {
        try {
            socket = new ServerSocket(serverConfig.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Socket clientSocket;
        while (!socket.isClosed()) {
            try {
                clientSocket = socket.accept();
            } catch (SocketException e) {
                logger.warning("Socket exception:" + e.getMessage());
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //so we don't let someone keep a connection busy too long
            try {
                clientSocket.setSoTimeout(ServerConfig.HTTP_SOCKET_TIMEOUT);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            logger.fine("Received new connection from " + clientSocket.getInetAddress().getHostAddress());
            executor.submit(new Connection(clientSocket, serverConfig));
        }
    }

    @Override
    public void interrupt() {
        //should stop, do not accept new connections
        this.executor.shutdown();
        logger.fine("Waiting for all threads to finish execution");
        try {
            this.executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.fine("Server closed");


        super.interrupt();
    }
}
