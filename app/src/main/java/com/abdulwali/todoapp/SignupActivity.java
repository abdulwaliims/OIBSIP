package com.abdulwali.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    Button btnSignup;
    TextView tvGoToLogin;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Go back to Login Screen
        tvGoToLogin.setOnClickListener(v -> {
            finish(); // Just close this screen and go back
        });

        // Register Logic
        btnSignup.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // 1. Check for empty fields
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Check password length (min 8 characters)
            if (password.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Check if email already exists
            if (dbHelper.checkEmailExists(email)) {
                Toast.makeText(this, "Email already registered. Please login.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. Save to Database
            boolean isRegistered = dbHelper.registerUser(name, email, password);
            if (isRegistered) {
                Toast.makeText(this, "Account Created Successfully! Please Login.", Toast.LENGTH_LONG).show();
                finish(); // Go back to Login Screen
            } else {
                Toast.makeText(this, "Registration Failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}