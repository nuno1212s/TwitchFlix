package com.twitchflix.authentication.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.twitchflix.App;
import com.twitchflix.authentication.User;
import com.twitchflix.authentication.accounts.ActiveConnection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

/**
 * Handles the OAuth2 google sign in
 * <p>
 * https://developers.google.com/identity/sign-in/android/sign-in
 * https://developers.google.com/identity/sign-in/android/backend-auth
 */

public class OAuth2Handler {

    private String CLIENT_ID;

    private HttpTransport transport;

    private JsonFactory factory;

    private GoogleIdTokenVerifier verifier;

    public OAuth2Handler() throws GeneralSecurityException, IOException {

        File fileFromResource = App.getFileManager().getFileFromResource("config.json");

        GenericJson genericJson = App.getFileManager().readFile(fileFromResource);

        CLIENT_ID = (String) genericJson.get("CLIENT_ID");

        transport = GoogleNetHttpTransport.newTrustedTransport();

        factory = new JacksonFactory();

        verifier = new GoogleIdTokenVerifier.Builder(transport, factory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    public GoogleIdToken verify(String google_id) {
        try {
            return verifier.verify(google_id);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isValid(UUID userID, String google_id) throws GeneralSecurityException, IOException {

        GoogleIdToken token = verifier.verify(google_id);

        if (token != null) {
            GoogleIdToken.Payload userInformation = token.getPayload();

            User user = App.getUserDatabase().getAccountInformation(userInformation.getEmail());

            if (user != null) {

                return user.getUserID().equals(userID);
            }

        }

        return false;
    }

}
