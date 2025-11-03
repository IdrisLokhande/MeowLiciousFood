package com.example.restapplication.ui.menus;

import android.graphics.Bitmap;

public class MenuItem {
    // Type constants
    public static final int TYPE_RESTAURANT = 0;
    public static final int TYPE_DISH = 1;
   
    // Flags
    private boolean isFavourite = false;
    private boolean isExpanded = false;

    // Others
    private String id; // unique identifier only for dish
    private String name;
    private String description;
    private double price;
    private Bitmap imageRes;
    private String restaurantName;
    private int type;

    // Constructor for restaurant header
    public MenuItem(String restaurantName) {
        this.type = TYPE_RESTAURANT;
        this.restaurantName = restaurantName;
    }

    // Constructor for dish item
    public MenuItem(String id, String name, String description, double price, Bitmap imageRes, String restaurantName) {
        this.type = TYPE_DISH;
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageRes = imageRes;
        this.restaurantName = restaurantName;
    }

    // Getters -- getId(), getName(), getDescription(), getPrice(), getImagesResId(), getRestaurantName(), getType()
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Bitmap getImageRes() {
        return imageRes;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public int getType() {
        return type;
    }

    // Setters -- setName(...), setDescription(...), setPrice(...), setImageRes(...), setRestaurantName(...), setExpanded(...), setFavourite(...) 
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImageRes(Bitmap imageRes) {
        this.imageRes = imageRes;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
   
    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }
  
    public void setFavourite(boolean favourite) {
        this.isFavourite = favourite;
    }

    // Checks -- isExpanded(), isFavourite()
    public boolean isExpanded() {
        return isExpanded;
    }
    
    public boolean isFavourite() {
        return isFavourite;
    }
}
