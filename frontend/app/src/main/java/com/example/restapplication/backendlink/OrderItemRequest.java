package com.example.restapplication.backendlink;

import com.google.gson.annotations.SerializedName;

public class OrderItemRequest{
	@SerializedName("oid")
	private String orderId;
	@SerializedName("fid")
	private String foodId;
	@SerializedName("qty")
	private int quantity;
	@SerializedName("subtotal")
	private double subTotal;

	public OrderItemRequest(String orderId, String foodId, int quantity, double subTotal){
		this.orderId = orderId;
		this.foodId = foodId;
		this.quantity = quantity;
		this.subTotal = subTotal;
	}

	public String getOrderId(){
		return orderId;
	}
	public String getFoodId(){
		return foodId;
	}
	public int getQuantity(){
		return quantity;
	}
	public double getSubTotal(){
		return subTotal;
	}
}
