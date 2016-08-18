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
    /**
     * Maximum time to wait for request from client once was connected (in ms)
     */
    public static int HTTP_SOCKET_TIMEOUT = 5000;


    /**
     * Number of parallel connections accepted in the same time
     */
    public static int POOL_SIZE = 20;


    public static File basePath;

    protected ServerSocket socket;
    private ExecutorService executor;

    private final int port;
    private static final Logger logger = Logger.getLogger(Connection.class.getName());
    private boolean shouldStop;

    public Webserver(int port, String path) {
        this.port = port;
        basePath = new File(path);
        this.checkPathIsValid();

        this.executor = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    private void checkPathIsValid() {
        if (!basePath.exists()) {
            throw new RuntimeException("Base path not available");
        }

        if (!basePath.isDirectory()) {
            throw new RuntimeException("Mentioned path is not a directory");
        }
    }

    public void run() {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Socket clientSocket;
        while (!this.shouldStop && !socket.isClosed()) {
            try {
                clientSocket = socket.accept();
            } catch (SocketException e) {
                //socket closed ...
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //so we don't let someone keep a connection busy too long
            try {
                clientSocket.setSoTimeout(HTTP_SOCKET_TIMEOUT);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            logger.finer("Received new connection from " + clientSocket.getInetAddress().getHostAddress());
            executor.submit(new Connection(clientSocket));
        }
    }

    @Override
    public void interrupt() {
        //should stop, do not accept new connections
        this.executor.shutdown();
        logger.finer("Waiting for all threads to finish execution");
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
        logger.finer("Server closed");


        super.interrupt();
    }

    public void shouldStop(boolean state) {
        this.shouldStop = state;
    }
}
