package com.nunoneto.authentication.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nunoneto.App;
import com.nunoneto.authentication.User;
import com.nunoneto.authentication.accounts.ActiveConnection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Handles the OAuth2 google sign in
 *
 * https://developers.google.com/identity/sign-in/android/sign-in
 * https://developers.google.com/identity/sign-in/android/backend-auth
 */
@Path("oauth")
public class OAuth2Handler {

    private static final String CLIENT_ID = "";

    private static final String CLIENT_SECRET = "";

    private static final String REDIRECT_URL = "";

    private HttpTransport transport;

    private JsonFactory factory;

    private GoogleIdTokenVerifier verifier;

    public OAuth2Handler() throws GeneralSecurityException, IOException {

        transport = GoogleNetHttpTransport.newTrustedTransport();

        factory = new JacksonFactory();

        verifier = new GoogleIdTokenVerifier.Builder(transport, factory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    @GET
    @Path("clientID")
    @Produces(MediaType.TEXT_PLAIN)
    public String requestClientID() {
        return CLIENT_ID;
    }

    @POST
    @Path("authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ActiveConnection authenticate(String idToken) throws GeneralSecurityException, IOException {

        GoogleIdToken token = verifier.verify(idToken);

        if (token != null) {
            GoogleIdToken.Payload userInformation = token.getPayload();

            String email = userInformation.getEmail();

            String name = (String) userInformation.get("name");
            String familyName = (String) userInformation.get("family_name");
            String givenName = (String) userInformation.get("given_name");

            if (App.getUserDatabase().existsAccountWithEmail(email)) {
                User accountInformation = App.getUserDatabase().getAccountInformation(email);

                return App.getAuthenticationHandler().createOAuthConnection(accountInformation.getUserID());
            } else {

                User user = new OAuthUser(name, familyName, email);

                App.getAsync().submit(() -> App.getUserDatabase().createAccount(user));

                return App.getAuthenticationHandler().createOAuthConnection(user.getUserID());
            }

        }

        throw new WebApplicationException("Google token ID is not valid");
    }

}
