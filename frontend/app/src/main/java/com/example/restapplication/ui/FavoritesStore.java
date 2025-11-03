package com.example.restapplication.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import com.example.restapplication.ui.menus.MenuItem;

public class FavoritesStore{
	private static final FavoritesStore instance = new FavoritesStore();
	private final MutableLiveData<List<MenuItem>> favorites = new MutableLiveData<>();

	public static FavoritesStore getInstance(){
		return instance;
	}

	public LiveData<List<MenuItem>> getFavorites(){
		return favorites;
	}

	public void setFavorites(List<MenuItem> favs){
		favorites.setValue(favs);
	}
}