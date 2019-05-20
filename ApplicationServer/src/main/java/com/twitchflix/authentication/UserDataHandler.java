package com.twitchflix.authentication;

import com.twitchflix.App;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

@Path("userdata")
public class UserDataHandler {

    @GET
    @Path("rquserbyemail")
    @Produces(MediaType.APPLICATION_JSON)
    public UserData requestUserData(String email) {

        User accountInformation = App.getUserDatabase().getAccountInformation(email);

        if (accountInformation == null) {
            throw new WebApplicationException("User with that email has not been found");
        }

        return UserData.fromUser(accountInformation);
    }

}
