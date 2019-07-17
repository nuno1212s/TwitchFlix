package com.twitchflix.authentication.accounts;

import com.twitchflix.App;
import com.twitchflix.authentication.User;
import com.twitchflix.authentication.oauth2.OAuth2Handler;
import com.twitchflix.authentication.oauth2.OAuthUser;
import com.twitchflix.rest.models.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Path("auth")
public class AuthenticationRestHandler {

    /**
     * Attempts to authenticate the user
     *
     * @param login The login model
     */
    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleAuthenticationRequest(EmailLoginModel login) {

        OwnUser accountInformation = App.getUserDatabase().getAccountInformationOwnAccount(login.getEmail());

        if (accountInformation == null) {
            return Response
                    .status(404)
                    .entity("User not found")
                    .build();
        }

        if (Arrays.equals(login.getPassword().getBytes(StandardCharsets.UTF_8), accountInformation.getPassword())) {

            return Response
                    .ok()
                    .entity(App.getAuthenticationHandler()
                            .generateActiveConnection(accountInformation.getUserID()))
                    .build();

        }

        return Response
                .status(400)
                .entity("Password is not correct")
                .build();
    }

    /**
     * Logout of the server
     *
     * @param logout the logout params
     * @return
     */
    @POST
    @Path("logout")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logOut(LoginModel logout) {

        if (App.getAuthenticationHandler().isValid(logout.getUserID(), logout.getAccessToken())) {

            App.getAuthenticationHandler().removeActiveConnection(logout.getUserID());

            return Response.ok()
                    .entity(true)
                    .build();

        } else {

            return Response.status(403)
                    .entity(false)
                    .entity("User is not logged in")
                    .build();

        }
    }

    /**
     * Registers the account
     *
     * @param register The register data
     * @return
     */
    @POST
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(RegisterModel register) {

        if (App.getUserDatabase().existsAccountWithEmail(register.getEmail())) {
            return Response.status(400)
                    .entity("Account with that email already exists")
                    .build();
        }

        OwnUser ownUser = new OwnUser(register.getFirstName(), register.getLastName(),
                register.getEmail(), register.getPassword(), register.getSalt());


        App.getAsync().submit(() ->
                App.getUserDatabase().createAccount(ownUser));

        return Response.ok()
                .entity(App.getAuthenticationHandler().generateActiveConnection(ownUser.getUserID()))
                .build();
    }

    @POST
    @Path("refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response refreshConnection(RefreshConnectionModel refresh) {

        ActiveConnection activeConnection = App.getAuthenticationHandler().getActiveConnection(refresh.getUserID());

        if (!App.getAuthenticationHandler().isValid(refresh.getUserID(), refresh.getAccessToken())) {
            return Response.status(400)
                    .entity("Wrong access token")
                    .build();
        }

        User user = App.getUserDatabase().getAccountInformation(refresh.getUserID());

        if (user instanceof OwnUser) {

            if (App.getAuthenticationHandler().checkPassword(refresh.getUserID(), refresh.getPassword())) {

                return Response.ok()
                        .entity(activeConnection.refreshToken())
                        .build();

            }
        } else if (user instanceof OAuthUser) {

            try {
                if (App.getAuth2Handler().isValid(refresh.getUserID(), refresh.getPassword())) {
                    return Response.ok()
                            .entity(activeConnection.refreshToken())
                            .build();
                }
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }

        }

        return Response.status(400)
                .entity("Wrong authentication")
                .build();
    }

}
