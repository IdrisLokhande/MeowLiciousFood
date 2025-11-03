package com.example.restapplication.ui.menus.cart;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.example.restapplication.ui.menus.MenuItem;

public class CartManager {
        public interface CartListener{
        	void onCartChanged(int itemCount, double total);
        }

	private static final List<CartItem> cartItems = new ArrayList<>();
        private static final List<CartListener> listeners = new ArrayList<>();

        public static CartManager get() {
		return new CartManager();
	}

	public static synchronized void addItem(MenuItem item){
		for(CartItem cartItem : cartItems){
			if(cartItem.getMenuItem().getId().equals(item.getId())){ // .equals(), not ==
				cartItem.incrQty();
                                notifyListeners();
				return;
			}
		}
		cartItems.add(new CartItem(item));
                notifyListeners();
	}    

	public static synchronized void removeItem(MenuItem item){
		cartItems.removeIf(cartItem -> cartItem.getMenuItem().getId() == item.getId());
                notifyListeners();
	}

	public static synchronized List<CartItem> getCartItems(){
		return new ArrayList<>(cartItems);
	}

	public static synchronized double getTotal(){
		double total = 0;
		for(CartItem cartItem : cartItems){
			total += cartItem.getMenuItem().getPrice() * cartItem.getQty();
		}
		return total;
	}
	
	public static synchronized void clearCart(){
		cartItems.clear();
                notifyListeners();
	}

        public static synchronized int getItemCount(){
        	int count = 0;
                for(CartItem cartItem : cartItems){
			count += cartItem.getQty();
                }
                return count;
        }

        public static synchronized void removeListener(CartListener l){
        	listeners.remove(l);
        }
   
        public static synchronized void addListener(CartListener l){
        	listeners.add(l);
        }
 
        private static void notifyListeners(){
        	int count = getItemCount();
                double total = getTotal();
                // iterate over a snapshot to avoid ConcurrentModificationException
                List<CartListener> snapshot;
                synchronized (CartManager.class) {
                	snapshot = new ArrayList<>(listeners);
                }
		for(CartListener l : snapshot){
                	try{
				l.onCartChanged(count,total);
			}catch (Exception ignored){
				// listener must not break cart updates
			}
                }
        }
}
