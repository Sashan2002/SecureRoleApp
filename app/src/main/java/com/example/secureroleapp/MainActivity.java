package com.example.secureroleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            String userRole = sharedPreferences.getString("user_role", "");
            if ("admin".equals(userRole)) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            } else {
                startActivity(new Intent(this, UserDashboardActivity.class));
            }
            finish();
        } else {
            // Show login screen
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.contains("user_id") &&
                !sharedPreferences.getString("user_id", "").isEmpty();
    }
}