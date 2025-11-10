package com.example.restapplication.ui.menus.cart;

import com.example.restapplication.ui.menus.MenuItem;

public class CartItem {
	private MenuItem menuItem;
        private int qty;

	public CartItem(MenuItem menuItem){
		this.menuItem = menuItem;
		this.qty = 1;
	}    

	// Getters
	public MenuItem getMenuItem() {
		return menuItem;
	}
	public int getQty() {
		return qty;
	}

	// Setters
	public void incrQty() {
		qty++;
	}
	public void decrQty() {
		if(qty > 1) qty++;
	}
}
