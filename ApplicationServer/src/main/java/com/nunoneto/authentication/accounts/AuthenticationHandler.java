package com.nunoneto.authentication.accounts;

import com.nunoneto.App;
import com.nunoneto.authentication.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
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
    public ActiveConnection handleAuthenticationRequest(String email, String password) {

        OwnUser accountInformation = App.getUserDatabase().getAccountInformationOwnAccount(email);

        if (accountInformation == null) {
            throw new WebApplicationException("User not found", 404);
        }

        if (Arrays.equals(password.getBytes(), accountInformation.getPassword())) {

            return generateActiveConnection(accountInformation.getUserID());

        }

        throw new WebApplicationException("Wrong password", 400);
    }

    @POST
    @Path("logout")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean logOut(String email, String accessToken) {

        User user = App.getUserDatabase().getAccountInformation(email);

        if (isValid(user.getUserID(), Base64.getDecoder().decode(accessToken))) {

            connections.remove(user.getUserID());

            return true;

        } else {

            throw new WebApplicationException("Wrong access token.", 403);

        }
    }

    @POST
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ActiveConnection registerAccount(String email, String firstName, String lastName, String password, String salt) {

        if (App.getUserDatabase().existsAccountWithEmail(email)) {
            throw new WebApplicationException("Account with that email already exists", 400);
        }

        OwnUser ownUser = new OwnUser(firstName, lastName, email, password, salt);

        App.getUserDatabase().createAccount(ownUser);

        return generateActiveConnection(ownUser.getUserID());
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

    private ActiveConnection generateActiveConnection(UUID owner) {
        return new ActiveConnection(owner, DEFAULT_VALID_TIME);
    }

}

class ActiveConnection {

    private UUID owner;

    private long createdTime, validFor;

    private byte[] accessToken;

    public ActiveConnection(UUID owner, long validFor) {
        this.owner = owner;
        this.validFor = validFor;

        this.createdTime = System.currentTimeMillis();

        this.accessToken = new BigInteger(256, new SecureRandom()).toByteArray();
    }

    public ActiveConnection refreshToken() {

        this.accessToken = new BigInteger(256, new SecureRandom()).toByteArray();

        this.createdTime = System.currentTimeMillis();

        return this;
    }

    public UUID getOwner() {
        return owner;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public boolean isValid() {
        return getCreatedTime() + getValidFor() <= System.currentTimeMillis();
    }

    public long getValidFor() {
        return validFor;
    }

    public byte[] getAccessTokenBytes() {
        return accessToken;
    }

    public String getAccessToken() {
        return Base64.getEncoder().encodeToString(accessToken);
    }
}