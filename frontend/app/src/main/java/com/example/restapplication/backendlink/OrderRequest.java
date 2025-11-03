package com.example.restapplication.backendlink;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OrderRequest {
    @SerializedName("oid")
    private String orderId;
    @SerializedName("uid")
    private int userId;
    @SerializedName("orderdate")
    private String orderDate;
    private double total;
    private List<OrderItemRequest> items;

    public OrderRequest(String orderId, int userId, String orderDate, double total, List <OrderItemRequest> items) {
	this.orderId = orderId;
	this.userId = userId;
	this.orderDate = orderDate;
	this.total = total;
	this.items = items;       
    }
}
