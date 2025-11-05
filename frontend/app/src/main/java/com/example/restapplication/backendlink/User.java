package com.example.restapplication.backendlink;

public class User {
    private int uid;
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getUserId(){
		return uid;
    }
}
