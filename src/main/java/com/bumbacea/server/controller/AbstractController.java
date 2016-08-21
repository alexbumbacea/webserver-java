package com.bumbacea.server.controller;

import com.bumbacea.server.Request;
import com.bumbacea.server.Response;

/**
 * Created by abumbacea on 21/08/16.
 */
public abstract class AbstractController {
    public abstract Response handle(Request req);
}
