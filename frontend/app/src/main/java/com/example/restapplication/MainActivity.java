package com.example.restapplication;

import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.core.view.ViewCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restapplication.backendlink.APIService;
import com.example.restapplication.backendlink.RetrofitClient;
import com.example.restapplication.backendlink.FoodItemResponse;
import com.example.restapplication.databinding.ActivityMainBinding;
import com.example.restapplication.ui.menus.cart.CartManager;
import com.example.restapplication.ui.menus.MenusCache;
import com.example.restapplication.ui.menus.MenuItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding; 
    private SessionManager session; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

	session = new SessionManager(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_menus, R.id.navigation_payments)
                .build();
        NavController navController = Navigation.findNavController(
    		findViewById(R.id.nav_host_fragment_activity_main)
	);

	navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
    		if (destination.getId() == R.id.cart_fragment) {
        		// Hide floating cart button
        		findViewById(R.id.navCart).setVisibility(View.GONE);
    		} else {
        		// Show it again
        		findViewById(R.id.navCart).setVisibility(View.VISIBLE);
    		}
	});

	if(!MenusCache.isPreloaded()){
		APIService api = RetrofitClient.getInstance().create(APIService.class);
		api.getFoodItems().enqueue(new Callback<List<FoodItemResponse>>() {
			@Override
			public void onResponse(Call<List<FoodItemResponse>> call, Response<List<FoodItemResponse>> response){
                        	if(response.isSuccessful() && response.body() != null){
                        		List<MenuItem> items = new ArrayList<>();
					for(FoodItemResponse item : response.body()){
						items.add(new MenuItem(item.getFoodId(), item.getFoodName(), item.getFoodDescription(), item.getFoodPrice(), item.getImg(), item.getRestaurantName()));
					}	
                        		MenusCache.setCache(items);		
				}
			}

			@Override
			public void onFailure(Call<List<FoodItemResponse>> call, Throwable t){
				Log.e("MainActivity", "Failed to preload menus: " + t.getMessage());
			}
		});
    	}

	Fragment navHostFrag = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
	Fragment current = navHostFrag.getChildFragmentManager().getPrimaryNavigationFragment();

        // Cart
        binding.navCart.setOnClickListener(v -> {
		navController.navigate(R.id.cart_fragment); // navigate to cart fragment
	});

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration); // sets (<-) button
        NavigationUI.setupWithNavController(binding.navView, navController);

	// To clean up janky UI
	binding.navView.setOnItemSelectedListener(item -> {
		Integer cur = navController.getCurrentDestination() != null ? navController.getCurrentDestination().getId() : null;
		if(cur != null && cur == R.id.cart_fragment){
			navController.popBackStack();
		}
		
		// do not disrupt normal flow
		boolean handled  = NavigationUI.onNavDestinationSelected(item, navController);
		return handled;
	});
    }

    @Override
    public boolean onSupportNavigateUp() {
    	NavController navController = Navigation.findNavController(
        	findViewById(R.id.nav_host_fragment_activity_main)
    	);
    	return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Meow! Leaving already?")
            .setPositiveButton("Yes", (dialog, which) -> {
                finishAffinity(); // Closes all activities by triggering onDestroy()
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss(); // Close the dialog
            })
            .show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
	session.clear();
	session = null;
    }
}