package com.example.restapplication.ui.home;

import android.util.Log;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.restapplication.ui.menus.MenuItem;
import com.example.restapplication.backendlink.APIService;
import com.example.restapplication.backendlink.RetrofitClient;
import com.example.restapplication.backendlink.FoodItemResponse;
import com.example.restapplication.ui.FavoritesStore;
import com.example.restapplication.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();
    private SessionManager session;

    public LiveData<List<MenuItem>> getFavoriteItems() {
        return FavoritesStore.getInstance().getFavorites();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void refresh(){
    	fetchFavorites();
    }

    public void fetchFavorites(){
	APIService api = RetrofitClient.getInstance().create(APIService.class);
	api.getFavorites(session.getUserId()).enqueue(new Callback<List<FoodItemResponse>>(){
		@Override
		public void onResponse(Call<List<FoodItemResponse>> call, Response<List<FoodItemResponse>> response){
			if(response.isSuccessful() && response.body() != null){
				List<MenuItem> favs = response.body().stream().map(item -> new MenuItem(item.getFoodId(), item.getFoodName(), item.getFoodDescription(), item.getFoodPrice(), item.getImg(), item.getRestaurantName())).collect(Collectors.toList());

				FavoritesStore.getInstance().setFavorites(favs);
			}
		}

		@Override
		public void onFailure(Call<List<FoodItemResponse>> call, Throwable t){
			Log.e("HomeViewModel", "Could not fetch favs", t);
		}
	});
    }

    public void updateFavorites(MenuItem item) {
        List<MenuItem> current = new ArrayList<>(FavoritesStore.getInstance().getFavorites().getValue());
        if (item.isFavourite()) {
            // add if not already present
            if (current.stream().noneMatch(f -> f.getId().equals(item.getId()))) {
                current.add(item);
            }
        } else {
            // remove if unfavourited
            current.removeIf(f -> f.getId().equals(item.getId()));
        }
        FavoritesStore.getInstance().setFavorites(current);
    }

    public void setHomeText(Context context){
   	mText.setValue(session.getUsername() != null ? session.getUsername() : "Friend");
    }

    public void setSession(Context context){
	if(context != null){
		session = new SessionManager(context);
	}
    }

    public HomeViewModel() {
	// nothing as of now
    }
}