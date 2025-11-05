package com.example.restapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager{
	private SharedPreferences prefs;

	public SessionManager(Context context){
		prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
	}

	public void saveUser(String username, int uid){
		prefs.edit().putString("username", username).putInt("userId", uid).apply(); 
	}

    public void clear(){
		prefs.edit().clear().apply(); 
	}

	public String getUsername(){
		return prefs.getString("username", null);
	}

	public int getUserId(){
		return prefs.getInt("userId", -1);
	}
}
