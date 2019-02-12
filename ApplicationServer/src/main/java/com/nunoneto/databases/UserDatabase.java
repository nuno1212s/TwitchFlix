package com.nunoneto.databases;

import com.nunoneto.authentication.accounts.OwnUser;
import com.nunoneto.authentication.User;

import java.util.UUID;

public interface UserDatabase {

    /**
     * Checks if there is an account with the given email
     * @param email
     * @return
     */
    boolean existsAccountWithEmail(String email);

    /**
     * Creates an account for a user
     * @param user
     */
    void createAccount(User user);

    /**
     * Deletes the account with the given ID
     * @param userID
     */
    void deleteAccount(UUID userID);

    /**
     * Gets the account information for an email
     * @param email
     * @return
     */
    User getAccountInformation(String email);

    /**
     * Gets the account information for the account with the ID
     * @param userID
     * @return
     */
    User getAccountInformation(UUID userID);

    /**
     * Get th
     * @param email
     * @return
     */
    OwnUser getAccountInformationOwnAccount(String email);

    OwnUser getAccountInformationOwnAccount(UUID ID);

}
