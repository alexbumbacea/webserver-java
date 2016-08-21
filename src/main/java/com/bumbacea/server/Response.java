package com.bumbacea.server;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Response {
    private HashMap<String, String> headers;
    public Integer statusCode = 200;
    public String protocol = "HTTP/1.1";

    private File file;
    private String requestedMethod;

    public Response(int statusCode) {
        this.statusCode = statusCode;
        initResponse();
    }

    public Response(File file, String method) {
        this.file = file;
        this.requestedMethod = method;
        initResponse();
    }

    public Response(int statusCode, String reason) {
        this.statusCode = statusCode;
        initResponse();
        this.headers.put("X-Reason", reason);
    }

    private void initResponse() {
        this.headers = new HashMap<>();
        this.headers.put("Allow", "GET,HEAD");
        this.headers.put("Connection", "close");
    }

    public void write(OutputStream outputStream) throws IOException {
        this.writeHeaders(outputStream);

        if (this.statusCode == 200 && this.requestedMethod.equals(Request.METHOD_GET)) {
            this.writeContent(outputStream);
        }
    }

    protected void writeContent(OutputStream outputStream) throws IOException {
        InputStream fileStream = new FileInputStream(this.file.getCanonicalPath());

        //buffer response in order not to overload memory
        int bytesRead;
        byte[] buffer = new byte[1024];
        while ((bytesRead = fileStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    protected void writeHeaders(OutputStream outputStream) throws IOException {
        //main header
        String mainHeaderLine = this.protocol + " " + this.statusCode.toString() + "\n";
        outputStream.write(mainHeaderLine.getBytes());
        this.prepareHeaders();

        for (Map.Entry<String, String> line : this.headers.entrySet()) {
            outputStream.write((line.getKey() + ":" + line.getValue() + "\n").getBytes());
        }

        outputStream.write("\n\r".getBytes());
    }

    protected void prepareHeaders() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.headers.put("Date", df.format(new java.util.Date()));
        this.headers.put("Status", this.statusCode.toString());
        if (this.statusCode != 200) {
            return ;
        }

        this.headers.put("Last-Modified", df.format(new java.util.Date(this.file.lastModified())));
        this.headers.put("Content-Disposition", "attachment; filename=\"" + this.file.getName() +"\"");
        this.headers.put("Content-Length", String.valueOf(this.file.length()));

    }
}
