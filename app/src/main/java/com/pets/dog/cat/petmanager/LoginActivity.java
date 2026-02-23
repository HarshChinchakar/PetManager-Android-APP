package com.pets.dog.cat.petmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private LinearLayout llCreateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Link to UI
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        llCreateProfile = findViewById(R.id.ll_create_profile);

        // 2. Handle Login Button Click
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                // --- CHECK CREDENTIALS LOCALLY ---
                SharedPreferences prefs = getSharedPreferences("PetAppPrefs", MODE_PRIVATE);

                String savedEmail = prefs.getString("saved_email", null);
                String savedPassword = prefs.getString("saved_password", null);

                if (savedEmail != null && savedEmail.equals(email) && savedPassword.equals(pass)) {
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, ProfilesActivity.class);
                    startActivity(intent);
                    finish(); // Close login screen
                } else {
                    Toast.makeText(this, "Invalid Email or Password. Have you created a profile?", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 3. Handle Create Profile Click (This was the missing piece!)
        llCreateProfile.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateProfileActivity.class);
            startActivity(intent);
        });
    }
}