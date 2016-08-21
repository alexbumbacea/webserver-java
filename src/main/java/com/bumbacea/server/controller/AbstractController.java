package com.bumbacea.server.controller;

import com.bumbacea.server.Request;
import com.bumbacea.server.Response;

public abstract class AbstractController {
    public abstract Response handle(Request req);
}
