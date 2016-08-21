package com.bumbacea.server;

import java.io.*;

public class ResponseFile extends Response {
    private File file;
    private String requestedMethod;

    public ResponseFile(File file, String method) {
        super(200);
        this.file = file;
        this.requestedMethod = method;
    }

    @Override
    protected void writeHeaders(OutputStream outputStream) throws IOException {

        this.headers.put("Last-Modified", getDateFormat().format(new java.util.Date(this.file.lastModified())));
        this.headers.put("Content-Disposition", "attachment; filename=\"" + this.file.getName() +"\"");
        this.headers.put("Content-Length", String.valueOf(this.file.length()));

        super.writeHeaders(outputStream);
    }

    @Override
    protected void writeContent(OutputStream outputStream) throws IOException {
        if (this.requestedMethod.equals("GET")) {
            InputStream fileStream = new FileInputStream(this.file.getCanonicalPath());

            //buffer response in order not to overload memory
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = fileStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

    }


}
