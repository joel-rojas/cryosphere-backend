package com.cryoingdevs.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IvànAlejandro on 20/10/2018.
 */
@Path("/")
public class MapsServices {
    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("This", "is a test");
        message.put("This", "run");
        return Response.ok(message).build();
    }
}
