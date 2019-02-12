package com.nunoneto.authentication.accounts;

import com.nunoneto.App;
import com.nunoneto.authentication.accounts.OwnUser;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationHandler {

    private Map<UUID, byte[]> accessTokens;

    public AuthenticationHandler() {
        accessTokens = new ConcurrentHashMap<>();
    }

    /**
     * Attempts to authenticate the user
     * @param email The user's email
     * @param password The salted + hashed password
     */
    public boolean handleAuthenticationRequest(String email, byte[] password) {

        OwnUser accountInformation = (OwnUser) App.getUserDatabase().getAccountInformation(email);

        if (accountInformation == null) {
            return false;
        }



        return false;
    }

}
