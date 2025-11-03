package com.example.restapplication.ui.menus;

import java.util.List;
import java.util.ArrayList;

public class MenusCache{
	private static final List<MenuItem> cachedMenuItems = new ArrayList<>();
	private static boolean preloaded = false;

	public static void setCache(List<MenuItem> items){
		if(!preloaded){
			cachedMenuItems.addAll(items);
			preloaded = true;
		}
	}

	public static boolean isPreloaded(){
		return preloaded;
	}

	public static List<MenuItem> getCache(){
		return cachedMenuItems;
	}
}