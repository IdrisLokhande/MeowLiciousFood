package com.example.restapplication.backendlink;

import com.google.gson.annotations.SerializedName;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class FoodItemResponse {
    @SerializedName("fid")
    private String foodId;
    @SerializedName("foodname")
    private String foodName;
    @SerializedName("description")
    private String description;
    @SerializedName("price")
    private double price;
    @SerializedName("img")
    private String img;
    @SerializedName("rname")
    private String rname;

    public String getFoodId() {
        return foodId;
    }
   
    public String getFoodName() {
        return foodName;
    }
 
    public String getRestaurantName() {
        return rname;
    }

    public String getFoodDescription() {
        return description;
    }

    public double getFoodPrice() {
        return price;
    }
 
    public Bitmap getImg() {
        byte [] decoded = Base64.decode(img, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
    }
}
