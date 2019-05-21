package com.twitchflix.authentication.accounts;

import com.twitchflix.App;
import com.twitchflix.authentication.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Path("auth")
public class AuthenticationHandler {

    /**
     * Each key is valid for 60 minutes without activity. (A new key is generated on every request to maintain security)
     * <p>
     * After those 60 minutes have passed, a new key must be generated for the user, requiring re authentication
     */
    private static final long DEFAULT_VALID_TIME = 3600L * 1000;

    private ConcurrentHashMap<UUID, ActiveConnection> connections;

    public AuthenticationHandler() {
        connections = new ConcurrentHashMap<>();
    }

    /**
     * Attempts to authenticate the user
     *
     * @param email    The user's email
     * @param password The salted + hashed password
     */
    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleAuthenticationRequest(@FormParam("email") String email,
                                                @FormParam("password") String password) {

        OwnUser accountInformation = App.getUserDatabase().getAccountInformationOwnAccount(email);

        if (accountInformation == null) {
            return Response
                    .status(404)
                    .entity("User not found")
                    .build();
        }

        if (Arrays.equals(password.getBytes(StandardCharsets.UTF_8), accountInformation.getPassword())) {

            return Response
                    .ok()
                    .entity(generateActiveConnection(accountInformation.getUserID()))
                    .build();

        }

        return Response
                .status(400)
                .entity("Password is not correct")
                .build();
    }

    @POST
    @Path("logout")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logOut(@FormParam("email") String email,
                           @FormParam("accesstoken") String accessToken) {

        User user = App.getUserDatabase().getAccountInformation(email);

        if (isValid(user.getUserID(), accessToken.getBytes(StandardCharsets.UTF_8))) {

            connections.remove(user.getUserID());

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

    @POST
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAccount(@FormParam("email") String email,
                                    @FormParam("firstname") String firstName,
                                    @FormParam("lastName") String lastName,
                                    @FormParam("password") String password,
                                    @FormParam("salt") String salt) {

        if (App.getUserDatabase().existsAccountWithEmail(email)) {
            Response.status(400)
                    .entity("Account with that email already exists")
                    .build();
        }

        OwnUser ownUser = new OwnUser(firstName, lastName, email, password, salt);

        App.getAsync().submit(() -> App.getUserDatabase().createAccount(ownUser));

        App.getUserDatabase().createAccount(ownUser);

        return Response.ok()
                .entity(generateActiveConnection(ownUser.getUserID()))
                .build();
    }

    @POST
    @Path("refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response refreshConnection(@FormParam("userid") UUID userID,
                                      @FormParam("password") String password,
                                      @FormParam("accesstoken") String access_token) {

        ActiveConnection activeConnection = App.getAuthenticationHandler().getActiveConnection(userID);

        if (!Arrays.equals(activeConnection.getAccessTokenBytes(), access_token.getBytes(StandardCharsets.UTF_8))) {
            return Response.status(400)
                    .entity("Wrong access token")
                    .build();
        }

        if (checkPassword(userID, password)) {

            return Response.ok()
                    .entity(activeConnection.refreshToken())
                    .build();

        }

        return Response.status(400)
                .entity("Wrong authentication")
                .build();
    }

    public boolean checkPassword(UUID userID, String hashed_password) {

        OwnUser accountInformation = App.getUserDatabase().getAccountInformationOwnAccount(userID);

        if (accountInformation == null) {
            throw new NullPointerException("User not found");
        }

        return Arrays.equals(hashed_password.getBytes(StandardCharsets.UTF_8), accountInformation.getPassword());
    }

    /**
     * Creates an OAuth Google session
     */
    public ActiveConnection createOAuthConnection(UUID userID) {
        return generateActiveConnection(userID);
    }

    public boolean isValid(UUID userID, String accessToken) {
        return isValid(userID, accessToken.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Check if an access token is valid for a user
     *
     * @return
     */
    public boolean isValid(UUID userID, byte[] accessToken) {
        if (this.connections.containsKey(userID)) {

            ActiveConnection activeConnection = this.connections.get(userID);

            if (!activeConnection.isValid()) {
                return false;
            }

            return Arrays.equals(activeConnection.getAccessTokenBytes(), accessToken);
        }

        return false;
    }

    public ActiveConnection getActiveConnection(UUID userID) {
        return this.connections.getOrDefault(userID, null);
    }

    private ActiveConnection generateActiveConnection(UUID owner) {
        return new ActiveConnection(owner, DEFAULT_VALID_TIME);
    }

}

