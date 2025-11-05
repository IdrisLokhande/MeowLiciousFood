package com.example.restapplication.backendlink;

import com.google.gson.annotations.SerializedName;

public class FavoritesRequest {
    @SerializedName("uid")
    private int userId;

    @SerializedName("fid")
    private String foodId;

    public FavoritesRequest(int userId, String foodId){
		this.userId = userId;
		this.foodId = foodId;
    } 
}
