package com.pets.dog.cat.petmanager;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class CreateProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private MaterialButton btnRegister;
    private LinearLayout llBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        etName = findViewById(R.id.et_register_name);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        btnRegister = findViewById(R.id.btn_register);
        llBackToLogin = findViewById(R.id.ll_back_to_login);

        // Handle Registration
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // --- NEW: SAVE CREDENTIALS LOCALLY ---
                // Open the vault named "PetAppPrefs" in private mode
                android.content.SharedPreferences prefs = getSharedPreferences("PetAppPrefs", MODE_PRIVATE);
                android.content.SharedPreferences.Editor editor = prefs.edit();

                // Put the data in the vault
                editor.putString("saved_email", email);
                editor.putString("saved_password", pass);
                editor.putString("saved_name", name);
                editor.apply(); // Locks the vault and saves

                Toast.makeText(this, "Profile Created! You can now log in.", Toast.LENGTH_SHORT).show();

                // Go back to login screen
                finish();
            }
        });

        // Handle Back to Login
        llBackToLogin.setOnClickListener(v -> {
            // Simply close this screen to reveal the Login screen beneath it
            finish();
        });
    }
}