//package com.example.secureroleapp;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import org.json.JSONException;
//import org.json.JSONObject;
//import java.util.HashMap;
//import java.util.Map;
//
//public class LoginActivity extends AppCompatActivity {
//    private EditText etEmail, etPassword;
//    private Button btnLogin;
//    private TextView tvRegister;
//    private ProgressBar progressBar;
//    private RequestQueue requestQueue;
//    private SharedPreferences sharedPreferences;
//
//    // Change this to your localhost IP address
//
//    private static final String BASE_URL = "http://172.20.10.7/secure_role_app/";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        initViews();
//        setupClickListeners();
//
//        requestQueue = Volley.newRequestQueue(this);
//        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
//    }
//
//    private void initViews() {
//        etEmail = findViewById(R.id.et_email);
//        etPassword = findViewById(R.id.et_password);
//        btnLogin = findViewById(R.id.btn_login);
//        tvRegister = findViewById(R.id.tv_register);
//        progressBar = findViewById(R.id.progress_bar);
//    }
//
//    private void setupClickListeners() {
//        btnLogin.setOnClickListener(v -> loginUser());
//        tvRegister.setOnClickListener(v -> {
//            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//        });
//    }
//
//    private void loginUser() {
//        String email = etEmail.getText().toString().trim();
//        String password = etPassword.getText().toString().trim();
//
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//        btnLogin.setEnabled(false);
//
//        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "login.php",
//                response -> {
//                    try {
//                        JSONObject jsonResponse = new JSONObject(response);
//                        boolean success = jsonResponse.getBoolean("success");
//
//                        if (success) {
//                            // Save user session
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString("user_id", jsonResponse.getString("user_id"));
//                            editor.putString("user_email", jsonResponse.getString("email"));
//                            editor.putString("user_role", jsonResponse.getString("role"));
//                            editor.putString("user_name", jsonResponse.getString("name"));
//                            editor.apply();
//
//                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
//
//                            // Redirect based on role
//                            String role = jsonResponse.getString("role").trim();
//                            Intent intent;
//                            if ("admin".equalsIgnoreCase(role)) {
//                                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
//                            } else if ("user".equalsIgnoreCase(role)) {
//                                intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
//                            } else {
//                                Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            startActivity(intent);
//                            finish();
//
//                        } else {
//                            Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {
//                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
//                    }
//                    progressBar.setVisibility(View.GONE);
//                    btnLogin.setEnabled(true);
//                },
//                error -> {
//                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                    btnLogin.setEnabled(true);
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("email", email);
//                params.put("password", password);
//                return params;
//            }
//        };
//
//        requestQueue.add(request);
//    }
//}

package com.example.secureroleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    // ⚠️ IMPORTANT: Replace with your actual IP address
    private static final String BASE_URL = "http://172.20.10.7/secure_role_app/";
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Debug: Log the BASE_URL
        Log.d(TAG, "BASE_URL: " + BASE_URL);
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d(TAG, "Attempting login with email: " + email);

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        String loginUrl = BASE_URL + "login.php";
        Log.d(TAG, "Login URL: " + loginUrl);

        StringRequest request = new StringRequest(Request.Method.POST, loginUrl,
                response -> {
                    Log.d(TAG, "Server response: " + response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        Log.d(TAG, "Login success: " + success);

                        if (success) {
                            // Save user session
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_id", jsonResponse.getString("user_id"));
                            editor.putString("user_email", jsonResponse.getString("email"));
                            editor.putString("user_role", jsonResponse.getString("role"));
                            editor.putString("user_name", jsonResponse.getString("name"));
                            editor.apply();

                            Log.d(TAG, "User session saved");

                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Redirect based on role
                            String role = jsonResponse.getString("role");
                            Log.d(TAG, "User role: " + role);

                            Intent intent;
                            if ("admin".equals(role)) {
                                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                Log.d(TAG, "Redirecting to Admin Dashboard");
                            } else {
                                intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                                Log.d(TAG, "Redirecting to User Dashboard");
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            String message = jsonResponse.getString("message");
                            Log.e(TAG, "Login failed: " + message);
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Response data: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                Log.d(TAG, "Sending params: " + params.toString());
                return params;
            }
        };

        requestQueue.add(request);
    }
}