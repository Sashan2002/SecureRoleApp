package com.example.secureroleapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private RadioGroup rgRole;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;

    private static final String BASE_URL = "http://172.20.10.7/secure_role_app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();

        requestQueue = Volley.newRequestQueue(this);
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgRole = findViewById(R.id.rg_role);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        RadioButton selectedRole = findViewById(selectedRoleId);
        String role = selectedRole != null ? selectedRole.getText().toString().toLowerCase() : "";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "register.php",
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");

                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                        if (success) {
                            finish(); // Go back to login
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                },
                error -> {
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("role", role);
                return params;
            }
        };

        requestQueue.add(request);
    }
}