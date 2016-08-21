package com.bumbacea.server.sample;

import com.bumbacea.server.controller.AbstractController;
import com.bumbacea.server.Request;
import com.bumbacea.server.Response;

public class SampleController extends AbstractController {

    @Override
    public Response handle(Request req) {
        return new Response(req.getQueryString().get("name"));
    }
}
