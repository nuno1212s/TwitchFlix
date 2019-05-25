package com.twitchflix.authentication;

import com.twitchflix.App;
import com.twitchflix.rest.models.LoginModel;
import jdk.nashorn.internal.objects.annotations.Getter;

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
            return Response.status(404)
                    .entity("User with that email has not been found")
                    .build();
        }

        return Response.ok()
                .entity(UserData.fromUserEmail(accountInformation))
                .build();
    }

    @POST
    @Path("rquser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestUserData(LoginModel model) {

        if (App.getAuthenticationHandler().isValid(model.getUserID(), model.getAccessToken())) {

            User user = App.getUserDatabase().getAccountInformation(model.getUserID());

            if (user == null) {
                return Response.status(404)
                        .entity("User has not been found")
                        .build();
            }

            return Response.ok().entity(UserData.fromActiveConnection(user)).build();

        }

        return Response.status(400).entity("Connection is not valid").build();
    }

    @GET
    @Path("test")
    public Response test() {

        return Response.ok().entity("TESTE OK").build();

    }

}
