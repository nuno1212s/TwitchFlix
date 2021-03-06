package com.twitchflix.applicationclient.authentication;

public interface AuthRequests {

    ActiveConnection requestConnection(String email, String hashed_password);

    ActiveConnection requestConnection(String google_token_id);

    ActiveConnection refreshActiveConnection(ActiveConnection activeConnection);

    void destroyConnection(ActiveConnection activeConnection);

    ActiveConnection registerAccount(String email, String first_name, String last_name, String hashed_password, String salt);

    boolean accountExistsWithEmail(String email);

}
