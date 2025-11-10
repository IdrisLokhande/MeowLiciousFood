package com.example.restapplication.ui.menus.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restapplication.R;
import com.example.restapplication.ui.menus.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final LayoutInflater inflater;
    private final List<CartItem> items = new ArrayList<>();

    public CartAdapter(Context ctx) {
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cart_item_row, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
    	CartItem ci = items.get(position);
        MenuItem mi = ci.getMenuItem();    
	    
        // binding
        holder.nameText.setText(mi.getName());
        holder.priceText.setText(String.format(Locale.getDefault(), "₹%.2f", mi.getPrice()));
        holder.quantityText.setText(String.valueOf(ci.getQty()));
        double subtotal = mi.getPrice() * ci.getQty();
        holder.subtotalText.setText(String.format(Locale.getDefault(), "₹%.2f", subtotal));

        // plus button
        holder.plusButton.setOnClickListener(v -> CartManager.addItem(mi));

        // minus button
        holder.minusButton.setOnClickListener(v -> {
			if(ci.getQty() > 1){ // FIX THIS BAD CODE
				CartManager.removeItem(mi);
				for(int i = 0; i < ci.getQty() - 1; i++){
					CartManager.addItem(mi);
				}
			}else{
				CartManager.removeItem(mi);
			}
		});

        // remove button
        holder.removeButton.setOnClickListener(v -> CartManager.removeItem(mi));

        // misc.; listens for clicks anywhere in the entire cart row
        holder.itemView.setOnClickListener(v -> {
   			// no-op for now
		});
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Replace adapter data. Call this from Fragment when CartManager notifies
    public void setItems(@NonNull List<CartItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged(); // should try other alternatives than this
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView priceText;
        final ImageView minusButton;
        final TextView quantityText;
        final ImageView plusButton;
        final TextView subtotalText;
        final ImageView removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            priceText = itemView.findViewById(R.id.priceText);
            minusButton = itemView.findViewById(R.id.minusButton);
            quantityText = itemView.findViewById(R.id.quantityText);
            plusButton = itemView.findViewById(R.id.plusButton);
            subtotalText = itemView.findViewById(R.id.subtotalText);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
