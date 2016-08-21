package com.bumbacea.server;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Response {
    protected HashMap<String, String> headers;
    public Integer statusCode = 200;
    public String protocol = "HTTP/1.1";
    protected String content = "";


    public Response(int statusCode) {
        this.statusCode = statusCode;
        this.headers = new HashMap<>();
    }

    public Response(int statusCode, String reason) {
        this(statusCode);
        this.headers.put("X-Reason", reason);
    }

    public Response(String content) {
        this(200);
        this.content = content;
    }

    public void write(OutputStream outputStream) throws IOException {
        this.writeHeaders(outputStream);
        this.writeContent(outputStream);
    }

    protected void writeContent(OutputStream outputStream) throws IOException {
        if (!this.content.equals(null)) {
            outputStream.write(this.content.getBytes());
        }
    }

    protected void writeHeaders(OutputStream outputStream) throws IOException {
        this.headers.put("Date", this.getResponseDate());
        this.headers.put("Status", this.statusCode.toString());

        //main header
        String mainHeaderLine = this.protocol + " " + this.statusCode.toString() + "\r\n";
        outputStream.write(mainHeaderLine.getBytes());

        for (Map.Entry<String, String> line : this.headers.entrySet()) {
            outputStream.write((line.getKey() + ":" + line.getValue() + "\r\n").getBytes());
        }

        outputStream.write("\r\n".getBytes());
    }

    private String getResponseDate() {
        return getDateFormat().format(new Date());
    }

    protected DateFormat getDateFormat() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df;
    }


}
