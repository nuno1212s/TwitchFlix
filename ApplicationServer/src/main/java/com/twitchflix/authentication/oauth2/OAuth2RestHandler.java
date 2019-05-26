package com.twitchflix.authentication.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.twitchflix.App;
import com.twitchflix.authentication.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Path("oauth")
public class OAuth2RestHandler {

    @POST
    @Path("authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(com.twitchflix.rest.models.GoogleIdToken idToken) throws GeneralSecurityException, IOException {

        GoogleIdToken token = App.getAuth2Handler().verify(idToken.getIdToken());

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


}
