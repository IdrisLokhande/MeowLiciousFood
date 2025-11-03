package com.example.restapplication.backendlink;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Query;
import retrofit2.http.POST;
import retrofit2.http.GET;

import java.util.List;

public interface APIService {
    @POST("/login")
    Call<LoRResponse> loginUser(@Body User user);
  
    @POST("/register")
    Call<LoRResponse> registerUser(@Body RegisterUser request);

    @POST("/orders/place")
    Call<OrderResponse> placeOrder(@Body OrderRequest orderRequest);
    
    @POST("/favorites/post")
    Call<FavoritesResponse> recordFavorite(@Body FavoritesRequest favRequest);

    @POST("/favorites/delete")
    Call<FavoritesResponse> deleteFavorite(@Body FavoritesRequest favRequest);

    @GET("/favorites/get")
    Call<List<FoodItemResponse>> getFavorites(@Query("uid") int userId);

    @GET("/fooditems/all")
    Call<List<FoodItemResponse>> getFoodItems();
}
