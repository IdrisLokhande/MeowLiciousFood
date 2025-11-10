package com.example.restapplication.ui.menus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restapplication.databinding.ItemMenuBinding;
import com.example.restapplication.ui.LastItemOffsetProvider;
import com.example.restapplication.ui.menus.cart.CartManager;
import com.example.restapplication.ui.menus.MenusViewModel;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Locale;

import com.example.restapplication.R;
import com.example.restapplication.SessionManager;
import com.example.restapplication.backendlink.APIService;
import com.example.restapplication.backendlink.RetrofitClient;
import com.example.restapplication.backendlink.FavoritesRequest;
import com.example.restapplication.backendlink.FavoritesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// RecyclerView Adapter that displays a mixed list of restaurant headers and dish items
// Uses two view types: TYPE_RESTAURANT (header row) and TYPE_DISH (menu item row)
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements LastItemOffsetProvider {

    // Listener interface to decouple long‑click handling from the adapter
    public interface OnDishLongClickListener {
    	void onDishLongClick(int position);
    }

    public interface OnFavouriteToggleListener {
    	void onFavouriteToggled(MenuItem item);
    }

    private List<MenuItem> menuList;
    private Map<String, List<MenuItem>> groupedMap;
    private OnFavouriteToggleListener listener;
    private SessionManager session;
    private final Context context;
    private MenusViewModel menusViewModel;	  

    // menuList = the flattened list of headers + dishes currently displayed
    // groupedMap = original grouping of restaurant -> list of dishes
    public MenuAdapter(Context context, List<MenuItem> menuList, Map<String, List<MenuItem>> groupedMap, MenusViewModel menusViewModel) {
        this.menuList = menuList;
        this.groupedMap = groupedMap;
		this.context = context;
		this.session = new SessionManager(context);
		this.menusViewModel = menusViewModel;
    }

    public void setOnFavouriteToggleListener(OnFavouriteToggleListener listener) {
        this.listener = listener;
    }

    public void updateData(List<MenuItem> list, Map<String,List<MenuItem>> groupedMap){
    	this.groupedMap = groupedMap;
        this.menuList.clear();
		this.menuList.addAll(list);
		notifyDataSetChanged(); // should try other alternatives instead of this
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate different layouts depending on whether this row is a restaurant header or a dish item
        if (viewType == MenuItem.TYPE_RESTAURANT) {
            // Header row
            View view = inflater.inflate(R.layout.item_restaurant_header, parent, false);
            return new RestaurantViewHolder(view);
        } else {
            // Dish row
            ItemMenuBinding binding = ItemMenuBinding.inflate(inflater, parent, false); // item_menu.xml
            return new MenuViewHolder(binding, this::handleDishLongClick);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MenuItem item = menuList.get(position);

        // Bind data to the correct type of ViewHolder
        if (holder instanceof RestaurantViewHolder) {
            // Let RestaurantViewHolder handle its own binding logic through bind(...)
            ((RestaurantViewHolder) holder).bind(item);
        } else if (holder instanceof MenuViewHolder) {
            ((MenuViewHolder) holder).binding.textDishName.setText(item.getName());
            ((MenuViewHolder) holder).binding.textDishDescription.setText(item.getDescription());
            ((MenuViewHolder) holder).binding.textDishPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice()));
            ((MenuViewHolder) holder).binding.imageDish.setImageBitmap(item.getImageRes());

            // Add to Cart
	    	((MenuViewHolder) holder).binding.imageTrolley.setOnClickListener(v -> {
           		CartManager.addItem(item);
                Toast.makeText(holder.itemView.getContext(), item.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
  
                v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() ->
                	v.animate().scaleX(1f).scaleY(1f).setDuration(100)
		 		);
	    	});
	
            // Show/Hide Favorite
            ((MenuViewHolder) holder).binding.imageFavorite.setVisibility(item.isFavourite() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Return the type of row (header vs dish) so RecyclerView knows which ViewHolder to create
        return menuList.get(position).getType();
    }

    @Override
    public boolean shouldApplyBottomOffset(int position){
    	return getItemViewType(position) == MenuItem.TYPE_DISH || (getItemViewType(position) == MenuItem.TYPE_RESTAURANT && (!menuList.get(position).isExpanded()));
    }

    public void toggleDishes(int clickedPosition, String restaurantName) {
       // If expanding, insert that restaurant's dishes right after the header (top to bottom)
       // If collapsing, remove only that restaurant's dishes until the next header (top to bottom)
       MenuItem headerItem = menuList.get(clickedPosition);
       boolean expand = !headerItem.isExpanded();
       headerItem.setExpanded(expand);

       if (expand) {
           List<MenuItem> dishes = groupedMap.get(restaurantName);
           menuList.addAll(clickedPosition + 1, dishes);
           notifyItemRangeInserted(clickedPosition + 1, dishes.size());
           notifyItemChanged(clickedPosition);
           notifyItemChanged(menuList.size() - 1);
       } else {
           int removedCount = 0;
           int i = clickedPosition + 1;
           while (i < menuList.size() && menuList.get(i).getType() == MenuItem.TYPE_DISH) {
               // Remove consecutive dish rows belonging to this restaurant
               // Stop when we hit another header or run out of items
               if (restaurantName.equals(menuList.get(i).getRestaurantName())) {
                   menuList.remove(i);
                   removedCount++;
               } else {
                   break;
               }
           }
           notifyItemRangeRemoved(clickedPosition + 1, removedCount);
           notifyItemChanged(menuList.size() - 1);
       }
    }

    private void handleDishLongClick(int position) {
       // Toggle the favourite state of a dish when long‑pressed
       // and refresh just that row to update the heart icon
       MenuItem item = menuList.get(position);

       boolean isFavorite = item.isFavourite();
       APIService api = RetrofitClient.getInstance().create(APIService.class);
       FavoritesRequest favRequest = new FavoritesRequest(session.getUserId(), item.getId());

       if (item.getType() == MenuItem.TYPE_DISH && !isFavorite) {	   
	   		api.recordFavorite(favRequest).enqueue(new Callback<FavoritesResponse>(){
	   		@Override
			public void onResponse(Call<FavoritesResponse> call, Response<FavoritesResponse> response){
				if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
					item.setFavourite(!item.isFavourite());
           				notifyItemChanged(position);
          
           				if(listener != null){
                 			listener.onFavouriteToggled(item);
           				}				
				}else if(response.body() != null){
					Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
	
			@Override
			public void onFailure(Call<FavoritesResponse> call, Throwable t){
				Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
			}
	   		});
       }else if (item.getType() == MenuItem.TYPE_DISH && isFavorite) {	   
	   		api.deleteFavorite(favRequest).enqueue(new Callback<FavoritesResponse>(){
	   		@Override
			public void onResponse(Call<FavoritesResponse> call, Response<FavoritesResponse> response){
				if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
					item.setFavourite(!item.isFavourite());
           			notifyItemChanged(position);
          
           			if(listener != null){
                 			listener.onFavouriteToggled(item);
           			}				
				}else if(response.body() != null){
					Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
	
			@Override
			public void onFailure(Call<FavoritesResponse> call, Throwable t){
				Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
			}
	   		});
       }
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ItemMenuBinding binding;
        // This ViewHolder (wrapper around row's Views) represents a single dish row in the list
        // It also listens for a long‑press (press and hold) on that row
        // When a long‑press happens, it doesn’t handle the action itself
        // Instead, it passes the event (along with the dish’s position in the list)
        // back to the adapter’s OnDishLongClickListener, so the adapter can decide
        // what to do (like toggling favourite, showing a Toast, etc)
        public MenuViewHolder(ItemMenuBinding binding, OnDishLongClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnLongClickListener(v -> {
                 // getBindingAdapterPosition() ensures we get the current position
                 // (accounts for changes in the list). Ignore if NO_POSITION
                 int pos = getBindingAdapterPosition();
                 if (pos != RecyclerView.NO_POSITION) {
                     listener.onDishLongClick(pos);
                 }
                 return true; // consume the long press
            });
        }
    }
}
