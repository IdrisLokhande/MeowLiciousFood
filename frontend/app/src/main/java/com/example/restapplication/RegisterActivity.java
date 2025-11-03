package com.example.restapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.EditText;
import android.telephony.SmsManager;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

// Compat to support on older Android versions
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.restapplication.databinding.ActivityRegisterBinding;
import com.example.restapplication.backendlink.RegisterUser;
import com.example.restapplication.backendlink.LoRResponse;
import com.example.restapplication.backendlink.APIService;
import com.example.restapplication.backendlink.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
  
    private ActivityRegisterBinding binding;
    private int REQUEST_CODE_SMS = 1001; // Custom Request Code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

	if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
    		ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_SMS);
	}

        EditText passwordField = findViewById(R.id.regPassword);
        passwordField.setTransformationMethod(new CustomPasswordTransformation());

        EditText cpasswordField = findViewById(R.id.conPassword);
        cpasswordField.setTransformationMethod(new CustomPasswordTransformation());

        binding.buttonRegister.setOnClickListener(v -> {
            String password = binding.regPassword.getText().toString().trim();
            String confirmPassword = binding.conPassword.getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                 Toast.makeText(this, "Mrrow! Passwords do not match!", Toast.LENGTH_SHORT).show();
                 return;
            }
	    // Everything else handled in server.js
           
            email = binding.editEmail.getText().toString().trim();
            username = binding.regUsername.getText().toString().trim();
            firstName = binding.editFirstName.getText().toString().trim();
            lastName = binding.editLastName.getText().toString().trim();
            phone = binding.editPhone.getText().toString().trim();
            address = binding.editAddress.getText().toString().trim();
            
            RegisterUser request = new RegisterUser(email, username, firstName, lastName, phone, address, password);
            APIService api = RetrofitClient.getInstance().create(APIService.class);

            // Validate and save user data
	    api.registerUser(request).enqueue(new Callback<LoRResponse>() {
  
            @Override
            public void onResponse(Call<LoRResponse> call, Response<LoRResponse> response) {
                   if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                         sendSms(phone, "Meow! Thank you " + firstName + " " + lastName + " for registering on MeowLiciousFood!");
                         goToLogin();
			 // below snippet in this block gives error mismatch
			 // Toast.makeText(LoginActivity.class, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                   } else if (response.body() != null){
                         Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                   }
            }

            @Override
            public void onFailure(Call<LoRResponse> call, Throwable t) {
                   Toast.makeText(RegisterActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                   Log.e("Retrofit", "onFailure: ", t);
            }
            });
        });
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    	super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    	if (!(requestCode == REQUEST_CODE_SMS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
        	Toast.makeText(this, "Mrrrow! SMS permission denied", Toast.LENGTH_SHORT).show();
    	}
    }

    private void sendSms(String phone, String message) {
    	SmsManager smsManager = SmsManager.getDefault();
    	smsManager.sendTextMessage(phone, null, message, null, null);
    }

    private void goToLogin(){
	Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
    	startActivity(intent);
	finish(); // Closes RegisterActivity so user can't go back to it
    }
}
