package com.twitchflix.authentication;

import com.twitchflix.App;
import com.twitchflix.rest.models.LoginModel;
import com.twitchflix.rest.models.UpdateNameModel;
import jdk.nashorn.internal.objects.annotations.Getter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

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

    @GET
    @Path("rquserbyuuid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestUserDataUUID(@HeaderParam("uuid") String uuid) {

        User accountInformation = App.getUserDatabase().getAccountInformation(UUID.fromString(uuid));

        if (accountInformation == null) {
            return Response.status(404)
                    .entity("User with that uuid has not been found")
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

    @POST
    @Path("updateFirstname")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateFirstName(UpdateNameModel model) {

        if (App.getAuthenticationHandler().isValid(model.getUserID(), model.getAccessToken())) {

            User user = App.getUserDatabase().getAccountInformation(model.getUserID());

            if (user == null) {
                return Response.status(404)
                        .entity("User has not been found")
                        .build();
            }



        }

        return Response.status(400).entity("Connection is not valid").build();
    }

    @GET
    @Path("test")
    public Response test() {

        return Response.ok().entity("TESTE OK").build();

    }

}
