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
@Path("oauth")
public class OAuth2Handler {

    private static String CLIENT_ID;

    private static HttpTransport transport;

    private static JsonFactory factory;

    private static GoogleIdTokenVerifier verifier;

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

    @GET
    @Path("clientID")
    @Produces(MediaType.TEXT_PLAIN)
    public Response requestClientID() {
        return Response.ok().entity(CLIENT_ID).build();
    }

    @POST
    @Path("authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(com.twitchflix.rest.models.GoogleIdToken idToken) throws GeneralSecurityException, IOException {

        GoogleIdToken token = verifier.verify(idToken.getIdToken());

        if (token != null) {
            GoogleIdToken.Payload userInformation = token.getPayload();

            String email = userInformation.getEmail();

            String name = (String) userInformation.get("name");
            String familyName = (String) userInformation.get("family_name");
            String givenName = (String) userInformation.get("given_name");

            if (App.getUserDatabase().existsAccountWithEmail(email)) {
                User accountInformation = App.getUserDatabase().getAccountInformation(email);

                return Response.ok()
                        .entity(App.getAuthenticationHandler().createOAuthConnection(accountInformation.getUserID()))
                        .build();
            } else {

                User user = new OAuthUser(name, familyName, email);

                App.getAsync().submit(() -> App.getUserDatabase().createAccount(user));

                return Response.ok()
                        .entity(App.getAuthenticationHandler().createOAuthConnection(user.getUserID()))
                        .build();
            }

        }

        return Response.status(400)
                .entity("Google token ID is not valid")
                .build();
    }

    public static boolean isValid(UUID userID, String google_id) throws GeneralSecurityException, IOException {

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
