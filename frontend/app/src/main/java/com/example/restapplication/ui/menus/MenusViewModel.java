package com.example.restapplication.ui.menus;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.lang.Runnable;
import java.lang.Thread;

import com.example.restapplication.backendlink.APIService;
import com.example.restapplication.backendlink.RetrofitClient;
import com.example.restapplication.backendlink.FoodItemResponse;
import com.example.restapplication.ui.FavoritesStore;
import com.example.restapplication.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenusViewModel extends ViewModel {

    private final MutableLiveData<List<MenuItem>> menuList = new MutableLiveData<>();
    private final MediatorLiveData<List<MenuItem>> mediate = new MediatorLiveData<>();

    public MenusViewModel() {
        mediate.addSource(menuList, list -> applyFavorites(list));
		List<MenuItem> items = MenusCache.getCache(); // preload
		if(!items.isEmpty()){
			menuList.setValue(items);
		}	
    }

    private void applyFavorites(List<MenuItem> list){
        List<MenuItem> favs = FavoritesStore.getInstance().getFavorites().getValue();
		if(list == null) return;
	
		for(MenuItem item : list){
			boolean isFav = favs!= null && favs.stream().anyMatch(f -> f.getId().equals(item.getId()));
			if(item.getType() == MenuItem.TYPE_DISH) item.setFavourite(isFav);
		}
	
		mediate.setValue(new ArrayList<>(list));
    }

    public void refresh(){
        fetchMenuItems(); // async
    }
    /* Old Code
    private void loadMenuItems(MenuItem menuItem) {
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("D1", "Tuna Temptation Plate", "Purrfectly Shredded Tuna with Creamy Dip", 299, R.drawable.juicy_tuna, "Billalaal Restaurant"));
        items.add(new MenuItem("D2", "Meowzarella Sticks", "Cheesey Seasoned Sticks", 99, R.drawable.moz_sticks, "Billalaal Restaurant"));
        items.add(new MenuItem("D3", "Purr-ger Deluxe", "Large Chicken Burger with Tasty Filling", 159, R.drawable.chicken_burger, "KittyGoGo"));
        items.add(new MenuItem("D4", "Chicken Popcorn", "Juicy Bread-Coated Bite-Sized Chicken Pieces", 59, R.drawable.chicken_popcorn, "MeowWay"));
        items.add(new MenuItem("D5", "Paw-neer Tikka", "Purrfectly Spiced Indian Delight with Bite-Sized Paneer Pieces", 119, R.drawable.paneer_tikka, "MeowWay"));
        items.add(new MenuItem("D6", "Paw-neer Pizza", "Deliciously Baked Pizza with Melted Paneer Toppings", 149, R.drawable.paneer_pizza, "Meowzza Hut"));
        items.add(new MenuItem("D7", "Veg Pizza", "Medium-Sized Pizza with Standard Vegetable Toppings", 99, R.drawable.veg_pizza, "Meowzza Hut"));

        // Add more items as needed
        menuList.setValue(items);
    }
    */

    public LiveData<List<MenuItem>> getMenuList() {
        return mediate;
    }

    private void fetchMenuItems(){
		APIService api = RetrofitClient.getInstance().create(APIService.class);

		api.getFoodItems().enqueue(new Callback<List<FoodItemResponse>>() {
		@Override
		public void onResponse(Call<List<FoodItemResponse>> call, Response<List<FoodItemResponse>> response){
                        if(response.isSuccessful() && response.body() != null){
                        	List<MenuItem> items = new ArrayList<>();
				for(FoodItemResponse item : response.body()){
					items.add(new MenuItem(item.getFoodId(), item.getFoodName(), item.getFoodDescription(), item.getFoodPrice(), item.getImg(), item.getRestaurantName()));
				}	
                        	menuList.setValue(items);		
			}
		}

		@Override
		public void onFailure(Call<List<FoodItemResponse>> call, Throwable t){
			Log.e("MenusViewModel", "Failed to fetch menus: " + t.getMessage());
		}
		});
    }
}
