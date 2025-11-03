package com.example.restapplication.backendlink;

public class RegisterUser {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
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
