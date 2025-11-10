package com.example.restapplication.backendlink;

import com.google.gson.annotations.SerializedName;

public class LoRResponse {
    private boolean success;
    private String message;
	@SerializedName("uid")
    private Integer uid; // not int because configured to return null in some cases

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
