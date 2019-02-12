package com.nunoneto.authentication;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationHandler {

    private Map<UUID, byte[]> accessTokens;

    public AuthenticationHandler() {
        accessTokens = new ConcurrentHashMap<>();
    }



}
