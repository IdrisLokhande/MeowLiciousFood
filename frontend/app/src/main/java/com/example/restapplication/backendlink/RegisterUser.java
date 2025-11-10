package com.example.restapplication.backendlink;

import com.google.gson.annotations.SerializedName;

public class RegisterUser {
    @SerializedName("email")
    private String email;
    @SerializedName("username")
    private String username;
    @SerializedName("fname")
    private String firstName;
    @SerializedName("lname")
    private String lastName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private String address;
    @SerializedName("password")
    private String password;

    public RegisterUser(String email, String username, String firstName, String lastName, String phone, String address, String password) {
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }
}
