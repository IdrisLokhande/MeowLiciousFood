package com.example.restapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import android.text.method.LinkMovementMethod;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restapplication.databinding.ActivityLoginBinding;
import com.example.restapplication.backendlink.User;
import com.example.restapplication.backendlink.LoRResponse;
import com.example.restapplication.backendlink.APIService;
import com.example.restapplication.backendlink.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	// Set up SessionManager
	session = new SessionManager(LoginActivity.this);

        // Inflate the layout using View Binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        binding.password.setTransformationMethod(new CustomPasswordTransformation());
 
	// For Register
	binding.textRegister.setOnClickListener(v -> {
    		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
    		startActivity(intent);
	});

	// For Freepik Linking
	binding.textFreepik.setMovementMethod(LinkMovementMethod.getInstance());

        // Set up login button click listener
        binding.loginButton.setOnClickListener(v -> {
            // Authentication logic in server.js
	    String username = binding.username.getText().toString().trim();
    	    String password = binding.password.getText().toString().trim();

            User user = new User(username, password);
            APIService api = RetrofitClient.getInstance().create(APIService.class);

	    api.loginUser(user).enqueue(new Callback<LoRResponse>() {
	    	@Override
                public void onResponse(Call<LoRResponse> call, Response<LoRResponse> response){
                	if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
				session.saveUser(username, response.body().getUserId());
				// Navigate to MainActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Prevent going back to login screen
			}else if(response.body() != null){
				Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
			}
                }
 
                @Override
                public void onFailure(Call<LoRResponse> call, Throwable t){
                	Toast.makeText(LoginActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                        Log.e("Retrofit", "onFailure: ", t);
                }
            });
        });
    }
}
