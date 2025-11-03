package com.example.restapplication.ui.menus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.restapplication.databinding.FragmentMenusBinding;
import com.example.restapplication.ui.home.HomeViewModel;
import com.example.restapplication.Refreshable;
import com.example.restapplication.ui.LastItemBottomOffsetDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import java.lang.Runnable;

public class MenusFragment extends Fragment implements Refreshable {

    private FragmentMenusBinding binding;
    private MenuAdapter menuAdapter;
    private MenusViewModel menusViewModel;
    private HomeViewModel homeViewModel;
    private Runnable refreshCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMenusBinding.inflate(inflater, container, false);
        return binding.getRoot(); // returns fragment_menus.xml root after inflation
    }

    @Override
    public void refresh(Runnable onComplete){
    	menusViewModel.refresh();
	this.refreshCallback = onComplete;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menusViewModel = new ViewModelProvider(this).get(MenusViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Set up RecyclerView
        binding.recyclerMenus.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.recyclerMenus.addItemDecoration(new LastItemBottomOffsetDecoration(requireContext(), 64));

	menuAdapter = new MenuAdapter(requireActivity(), new ArrayList<>(), new LinkedHashMap<>(), menusViewModel);
	binding.recyclerMenus.setAdapter(menuAdapter);

        // Observe menu list
        // Imagine getMenuList() [MediatorLiveData] as a radio station.
        // The ViewModel is the reporter who decides what news (data) is playing.
        // The Fragment is a radio listener.
        // "Turn on the radio while this Fragment's view exists [getViewLifecycleOwner()].
        // whenever the reporter changes the news, call my code with the new news."
        menusViewModel.getMenuList().observe(getViewLifecycleOwner(), items -> {
            Map<String, List<MenuItem>> groupedMap = new LinkedHashMap<>();
            for (MenuItem item : items) {
                String restaurant = item.getRestaurantName();
                if (!groupedMap.containsKey(restaurant)) {
                    groupedMap.put(restaurant, new ArrayList<>());
                }
                groupedMap.get(restaurant).add(item);
            } // Create updated grouping that has new restaurants with items

            List<MenuItem> groupedItems = new ArrayList<>();
            for (String restaurant : groupedMap.keySet()) {
                MenuItem header = new MenuItem(restaurant); // TYPE_RESTAURANT constructor
                groupedItems.add(header);
                // Adapter will expand the headers later
            }   // Headers list that will show at first

            menuAdapter.updateData(groupedItems, groupedMap);
            menuAdapter.setOnFavouriteToggleListener(item -> homeViewModel.updateFavorites(item));
            
	    if(refreshCallback != null){
		refreshCallback.run();
		refreshCallback = null; // prevent double run
	    }
        });
    }

    @Override
    public void onDestroyView() { // rips View Tree out but keeps Fragment instance
        super.onDestroyView();
        binding = null;
    }
}
