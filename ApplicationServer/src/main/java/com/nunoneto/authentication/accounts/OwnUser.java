package com.nunoneto.authentication.accounts;

import com.nunoneto.authentication.User;

public class OwnUser extends User {

    private byte[] password;

    private String salt;

    private boolean confirmed;

    public OwnUser(String name, String lastName, String email, String hashed_password, String salt) {
        super(name, lastName, email);

        this.password = hashed_password.getBytes();
        this.salt = salt;

        this.confirmed = false;
    }

}
