package com.example.restapplication.backendlink;

import com.google.gson.annotations.SerializedName;

public class User {
	@SerializedName("uid")
    private int uid;
	@SerializedName("username")
    private String username;
	@SerializedName("password")
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getUserId(){
		return uid;
    }
}
