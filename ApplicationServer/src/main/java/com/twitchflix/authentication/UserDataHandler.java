package com.twitchflix.authentication;

import com.twitchflix.App;
import com.twitchflix.rest.models.LoginModel;
import com.twitchflix.rest.models.UpdateNameModel;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.UUID;

@Path("userdata")
public class UserDataHandler {

    @GET
    @Path("rquserbyemail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestUserData(@HeaderParam("email") String email) {

        User accountInformation = App.getUserDatabase().getAccountInformation(email);

        if (accountInformation == null) {
            return Response.status(404)
                    .entity("User with that email has not been found")
                    .build();
        }

        return Response.ok()
                .entity(UserData.fromUserEmail(accountInformation))
                .build();
    }

    @GET
    @Path("rquserbyuuid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestUserDataUUID(@HeaderParam("uuid") String uuid) {

        User accountInformation = App.getUserDatabase().getAccountInformation(UUID.fromString(uuid));

        if (accountInformation == null) {
            return Response.status(404)
                    .entity("User with that uuid has not been found")
                    .build();
        }

        return Response.ok()
                .entity(UserData.fromActiveConnection(accountInformation))
                .build();
    }

    @POST
    @Path("rquser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestUserData(LoginModel model) {

        if (App.getAuthenticationHandler().isValid(model.getUserID(), model.getAccessToken())) {

            User user = App.getUserDatabase().getAccountInformation(model.getUserID());

            if (user == null) {
                return Response.status(404)
                        .entity("User has not been found")
                        .build();
            }

            return Response.ok().entity(UserData.fromActiveConnection(user)).build();

        }

        return Response.status(400).entity("Connection is not valid").build();
    }

    @POST
    @Path("updateFirstName")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateFirstName(UpdateNameModel model) {

        return getResponse(model, true);
    }

    @POST
    @Path("updateLastName")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateLastName(UpdateNameModel model) {

        return getResponse(model, false);
    }

    @POST
    @Path("uploadPhoto")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadUserPhoto(@FormDataParam("image") FormDataBodyPart image,
                                    @FormDataParam("user") FormDataBodyPart userData) {

        userData.setMediaType(MediaType.APPLICATION_JSON_TYPE);

        LoginModel userLoginData = userData.getValueAs(LoginModel.class);

        image.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        InputStream stream = image.getValueAs(InputStream.class);

        if (App.getAuthenticationHandler().isValid(userLoginData.getUserID(), userLoginData.getAccessToken())) {
            try {
                User accountInformation = App.getUserDatabase().getAccountInformation(userLoginData.getUserID());

                return saveUserPhoto(accountInformation, stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Response.status(Response.Status.FORBIDDEN).build();

    }

    /**
     * Update the last name or the first name
     *
     * @param model     The necessary data to update the users name
     * @param firstName Whether it's the first name or the last
     * @return The response to the request
     */
    private Response getResponse(UpdateNameModel model, boolean firstName) {
        if (App.getAuthenticationHandler().isValid(model.getUserID(), model.getAccessToken())) {

            User user = App.getUserDatabase().getAccountInformation(model.getUserID());

            if (user == null) {
                return Response.status(404)
                        .entity("User has not been found")
                        .build();
            }

            if (firstName)
                user.setFirstName(model.getName());
            else
                user.setLastName(model.getName());

            App.getAsync().submit(() ->
                    App.getUserDatabase().updateAccount(user));

        }

        return Response.status(400).entity("Connection is not valid").build();
    }

    private Response saveUserPhoto(User user, InputStream photoStream) throws IOException {
        File userPhotoDataFolder = new File(App.DATA_FOLDER + App.USER_PHOTOS + "/");

        File userPhotoFile = new File(userPhotoDataFolder, user.getUserID().toString() + ".png");

        if (!userPhotoDataFolder.exists()) {
            userPhotoDataFolder.mkdirs();
        }

        if (!userPhotoFile.exists()) {
            userPhotoFile.createNewFile();
        }

        try (OutputStream outputStream = new FileOutputStream(userPhotoFile)) {

            byte[] buffer = new byte[1024];

            int length;

            while ((length = photoStream.read(buffer)) > 0) {

                outputStream.write(buffer, 0, length);
                outputStream.flush();

            }

        }

        photoStream.close();

        user.setPhotoLink("https://" + App.SERVER_IP + "/" + App.USER_PHOTOS + "/" + user.getUserID().toString() + ".png");

        return Response.ok().entity(UserData.fromActiveConnection(user)).build();
    }

    @GET
    @Path("test")
    public Response test() {

        System.out.println("TESTE");

        return Response.ok().entity("TESTE OK").build();

    }

}
