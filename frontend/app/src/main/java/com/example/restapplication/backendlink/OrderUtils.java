package com.example.restapplication.backendlink;

import java.util.UUID;

public class OrderUtils{
	public static String generateOrderId() {
		return "ORD-" + UUID.randomUUID().toString();
	}
}