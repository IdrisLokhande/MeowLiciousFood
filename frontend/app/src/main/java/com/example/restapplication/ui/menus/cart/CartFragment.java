package com.example.restapplication.ui.menus.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.restapplication.R;
import com.example.restapplication.SessionManager;
import com.example.restapplication.backendlink.APIService;
import com.example.restapplication.backendlink.RetrofitClient;
import com.example.restapplication.backendlink.OrderRequest;
import com.example.restapplication.backendlink.OrderUtils;
import com.example.restapplication.backendlink.OrderItemRequest;
import com.example.restapplication.backendlink.OrderResponse;
import com.example.restapplication.databinding.FragmentCartBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Locale;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartAdapter adapter;
    private final CartManager.CartListener cartListener = this::onCartChanged;
    private SessionManager session;

    private void setupRecyclerView(){
    	adapter = new CartAdapter(requireContext());
        binding.cartList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.cartList.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
	session = new SessionManager(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView + Adapter
        setupRecyclerView();

        // Register listener
        CartManager.addListener(cartListener);

        // Checkout button
        setupChkoutBtn();

        // Initial population
        onCartChanged(CartManager.getItemCount(), CartManager.getTotal());
    }

    private void onCartChanged(int itemCount, double total) {
        // update adapter list and totals
        List<CartItem> items = CartManager.getCartItems();
        adapter.setItems(items);
        binding.totalAmountText.setText(String.format(Locale.getDefault(), "₹%.2f", total));
        toggleEmptyState(itemCount == 0);
    }

    private void toggleEmptyState(boolean isEmpty) {
        binding.cartEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.cartList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.cartSummaryBar.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void setupChkoutBtn(){
    	binding.checkoutButton.setOnClickListener(v -> {
        	int count = CartManager.getItemCount();
                if(count == 0){
     			Toast.makeText(requireContext(), getString(R.string.cart_empty_message), Toast.LENGTH_SHORT).show();
                        return;
                }
		placeOrder();

                // More workflow left
        });
    }

    private void placeOrder() {
    	String orderId = OrderUtils.generateOrderId();
  	int userId = session.getUserId();
	String orderDate = LocalDate.now().toString();
	double total = CartManager.getTotal();

	List<OrderItemRequest> items = new ArrayList<>();
	for(CartItem item : CartManager.getCartItems()){
		double subtotal = item.getMenuItem().getPrice() * item.getQty();
		items.add(new OrderItemRequest(orderId, item.getMenuItem().getId(), item.getQty(), subtotal));
	}

	OrderRequest orderRequest = new OrderRequest(orderId, userId, orderDate, total, items);

	APIService api = RetrofitClient.getInstance().create(APIService.class);
	api.placeOrder(orderRequest).enqueue(new Callback<OrderResponse>() {
		@Override
		public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response){
			if(response.isSuccessful()){
				String message = "Order Placed! Total: ₹" + String.format(Locale.getDefault(), "%.2f", total);
				Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		public void onFailure(Call<OrderResponse> call, Throwable t){
			Log.e("Order", "Failed to place order: " + t.getMessage());
			Toast.makeText(requireContext(), "Network Error!", Toast.LENGTH_SHORT).show();
		}	

	});
    }

    /*
    private List<OrderRequest.OrderItem> toOrderItems(List<CartItem> cartItems) {
    	List<OrderRequest.OrderItem> orderItems = new ArrayList<>();
    	for (CartItem item : cartItems) {
        	orderItems.add(new OrderRequest.OrderItem(
            		item.productId,
            		item.quantity,
            		item.price
        	));
    	}
    	return orderItems;
    }
    */

    @Override
    public void onDestroyView() {
        CartManager.removeListener(cartListener);
        binding = null;
        super.onDestroyView();
    }
}