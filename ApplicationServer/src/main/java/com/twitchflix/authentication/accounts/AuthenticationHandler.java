package com.twitchflix.authentication.accounts;

import com.twitchflix.App;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


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

    /**
     * Check if an access token is valid for a user
     *
     * @return
     */
    public boolean isValid(UUID userID, String accessToken) {
        if (this.connections.containsKey(userID)) {

            ActiveConnection activeConnection = this.connections.get(userID);

            if (!activeConnection.isValid()) {
                return false;
            }

            return activeConnection.getAccessToken().equals(accessToken);
        }

        return false;
    }

    public ActiveConnection getActiveConnection(UUID userID) {
        return this.connections.getOrDefault(userID, null);
    }

    void removeActiveConnection(UUID userID) {
        this.connections.remove(userID);
    }

    public Map<UUID, ActiveConnection> getConnections() {
        return this.connections;
    }

    ActiveConnection generateActiveConnection(UUID owner) {
        ActiveConnection activeConnection = new ActiveConnection(owner, DEFAULT_VALID_TIME);

        this.connections.put(owner, activeConnection);

        System.out.println(this.connections);

        return activeConnection;
    }

}

