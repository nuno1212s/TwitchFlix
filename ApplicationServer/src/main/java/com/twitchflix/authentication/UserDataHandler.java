package com.twitchflix.authentication;

import com.twitchflix.App;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("userdata")
public class UserDataHandler {

    @GET
    @Path("rquserbyemail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestUserData(@HeaderParam("email") String email) {

        User accountInformation = App.getUserDatabase().getAccountInformation(email);

        if (accountInformation == null) {
            return Response.status(400)
                    .entity("User with that email has not been found")
                    .build();
        }

        return Response.ok()
                .entity(UserData.fromUser(accountInformation))
                .build();
    }

    @GET
    @Path("test")
    public Response test() {

        return Response.ok().entity("TESTE OK").build();

    }

}
