package com.example.restapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.restapplication.databinding.FragmentHomeBinding;
import com.example.restapplication.ui.menus.MenuItem;
import com.example.restapplication.ui.LastItemBottomOffsetDecoration;
import com.example.restapplication.backendlink.APIService;
import com.example.restapplication.backendlink.RetrofitClient;
import com.example.restapplication.backendlink.FoodItemResponse;
import com.example.restapplication.ui.home.HomeViewModel;
import com.example.restapplication.Refreshable;
import com.example.restapplication.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.lang.Runnable;

public class HomeFragment extends Fragment implements Refreshable{

    private FragmentHomeBinding binding;
    private HomeAdapter favoritesAdapter; // simple adapter for favourites
    private HomeViewModel homeViewModel;
    private boolean hasFav = false;
    private Runnable refreshCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // ViewModel scoped to this fragment only
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void refresh(Runnable onComplete){
        homeViewModel.refresh();
		this.refreshCallback = onComplete;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance){
    	super.onViewCreated(view, savedInstance);

		homeViewModel.setSession(requireActivity());
		homeViewModel.setHomeText(requireActivity()); // be careful (due to dependency on inflation of activity_main, etc.)
		
		if(!hasFav){ // sync only once after login
 			homeViewModel.fetchFavorites();
			hasFav = true;
		}
	
		// Welcome text
        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), text -> {	
			String welcomeText = "Meow! Welcome back, " + text + "!";				
			textView.setText(welcomeText);
		});

		// RecyclerView setup
        binding.recyclerFavourites.setLayoutManager(new LinearLayoutManager(getContext()));
        favoritesAdapter = new HomeAdapter(new ArrayList<>());
        binding.recyclerFavourites.setAdapter(favoritesAdapter);

        binding.recyclerFavourites.addItemDecoration(new LastItemBottomOffsetDecoration(requireContext(), 64));

		// Observe favourites list from HomeViewModel
        homeViewModel.getFavoriteItems().observe(getViewLifecycleOwner(), favourites -> {
            // Update adapter whenever favourites change
            if (favourites.isEmpty()) {
                   	binding.emptyFavourites.setVisibility(View.VISIBLE);
		  			binding.authorsCard.setVisibility(View.VISIBLE);
                   	binding.recyclerFavourites.setVisibility(View.GONE);
            } else {
                   	binding.emptyFavourites.setVisibility(View.GONE);
		   			binding.authorsCard.setVisibility(View.GONE);
                   	binding.recyclerFavourites.setVisibility(View.VISIBLE);
                   	favoritesAdapter.setData(favourites);
            }

	    	if(refreshCallback != null){
				refreshCallback.run();
				refreshCallback = null; // prevent double run
	    	}
        });

		view.post(() -> { // to wait for activity_main to inflate properly, so this post gets queued in the meantime
			if(binding == null) return;

			View svAuthors = binding.authorScroll;
			SwipeRefreshLayout swipeRefresh = requireActivity().findViewById(R.id.swipe_refresh);
		
			svAuthors.setOnTouchListener((v,event) -> {
				switch(event.getActionMasked()){
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
						v.getParent().requestDisallowInterceptTouchEvent(true);
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						v.getParent().requestDisallowInterceptTouchEvent(false);
						break;
				}
				return false;
			});

			swipeRefresh.setOnChildScrollUpCallback((parent, child) -> {
				return svAuthors.canScrollVertically(-1);
			});

			swipeRefresh.setOnRefreshListener(() -> {
				HomeFragment.this.refresh(() -> swipeRefresh.setRefreshing(false));
				// Cutomizable further
			});

			for(int i = 1; i < binding.authors.getChildCount(); i++){
				View child = binding.authors.getChildAt(i);
				if(child instanceof LinearLayout){
					((TextView)((LinearLayout) child).getChildAt(1)).setMovementMethod(LinkMovementMethod.getInstance());
				}	
			}
		});
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
