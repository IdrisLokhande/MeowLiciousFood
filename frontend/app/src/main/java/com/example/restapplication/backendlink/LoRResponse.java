package com.example.restapplication.backendlink;

public class LoRResponse {
    private boolean success;
    private String message;
    private Integer uid;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Integer getUserId() {
	return uid;
    }
}
