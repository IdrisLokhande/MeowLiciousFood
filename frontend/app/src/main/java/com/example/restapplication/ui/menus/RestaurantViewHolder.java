package com.example.restapplication.ui.menus;

import android.view.View;
import android.widget.TextView;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.restapplication.R;
import com.example.restapplication.ui.menus.MenuItem;

import java.util.ArrayList;
import java.util.List;

class RestaurantViewHolder extends RecyclerView.ViewHolder {
    TextView textRestaurantName;

    public RestaurantViewHolder(View itemView) {
        super(itemView);

        // Grab the TextView from the header layout that displays the restaurant name
        textRestaurantName = itemView.findViewById(R.id.textRestaurantName);
    }
    
    // Bind the data (a MenuItem representing a restaurant) to this header row
    public void bind(MenuItem restaurantItem) {
        // Show the restaurant's name in the header
        textRestaurantName.setText(restaurantItem.getRestaurantName());

        // When the header row is clicked, expand or collapse its dishes
        itemView.setOnClickListener(v -> {
             // Get the current adapter position of this header
             // This is safer than using the 'position' passed earlier,
             // because the list may have changed (items inserted/removed)
             int currentPos = getBindingAdapterPosition();

             // Only proceed if this ViewHolder is still valid in the adapter
             if (currentPos != RecyclerView.NO_POSITION) {
                // Ask the adapter to toggle the dishes for this restaurant
                // The adapter handles inserting/removing the dish rows below this header
                ((MenuAdapter) getBindingAdapter()).toggleDishes(currentPos, restaurantItem.getRestaurantName());
		Log.d("HeaderClick", String.valueOf(restaurantItem.isExpanded()));
             }
        });
    }
}