package com.nunoneto.videohandler;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("videos")
public class VideoRestHandler {

    @GET
    @Path("search")
    public void search(@QueryParam("videoName") String videoName) {



    }

}
