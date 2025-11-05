package com.example.restapplication.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restapplication.R;
import com.example.restapplication.ui.menus.MenuItem;
import com.example.restapplication.ui.LastItemOffsetProvider;
import com.example.restapplication.ui.menus.cart.CartManager;

import java.util.List;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.FavViewHolder> implements LastItemOffsetProvider {
    private List<MenuItem> favourites; // Flat list of favorited items  

    public HomeAdapter(List<MenuItem> favourites) {
        this.favourites = favourites;
    }

    public void setData(List<MenuItem> newFavourites) {
        this.favourites = newFavourites; // update
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                		.inflate(R.layout.item_menu, parent, false); // inflate item_menu in Home Fragment
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        // bind data to each row; each row = a holder, and holder contains views
        MenuItem item = favourites.get(position);
        holder.textDishName.setText(item.getName());
        holder.textDishDescription.setText(item.getDescription());
        holder.textDishPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice()));
        holder.imageDish.setImageBitmap(item.getImageRes());
		holder.imageTrolley.setOnClickListener(v -> {
           	CartManager.addItem(item);
			Toast.makeText(holder.itemView.getContext(), item.getName() + " added to cart!", Toast.LENGTH_SHORT).show();

            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() ->
            	v.animate().scaleX(1f).scaleY(1f).setDuration(100)
			); // enlarge then resume to normal, in 100 ms each
	    });

        holder.imageFavorite.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }

    @Override
    public boolean shouldApplyBottomOffset(int position){
    	return true;
    }

    static class FavViewHolder extends RecyclerView.ViewHolder {
        TextView textDishName, textDishDescription, textDishPrice;
        ImageView imageDish, imageFavorite, imageTrolley;

        FavViewHolder(View itemView) {
            super(itemView);
            textDishName = itemView.findViewById(R.id.textDishName);
            textDishDescription = itemView.findViewById(R.id.textDishDescription);
            textDishPrice = itemView.findViewById(R.id.textDishPrice);
            imageDish = itemView.findViewById(R.id.imageDish);
            imageFavorite = itemView.findViewById(R.id.imageFavorite);
            imageTrolley = itemView.findViewById(R.id.imageTrolley);
        }
    }
}
