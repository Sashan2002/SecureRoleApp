package com.example.secureroleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UserDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome, tvCurrentName, tvCurrentId, tvCurrentDesc;
    private EditText etName, etUserId, etDescription;
    private ImageView ivProfileImage, ivCurrentImage;
    private Button btnSelectImage, btnUploadData;
    private ProgressBar progressBar;

    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private Uri selectedImageUri;
    private String encodedImage = "";

    private static final int IMAGE_PICK_REQUEST = 1001;
    private static final String BASE_URL = "http://172.20.10.7/secure_role_app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        initViews();
        setupToolbar();

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        setupClickListeners();
        loadUserProfile();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvCurrentName = findViewById(R.id.tv_current_name);
        tvCurrentId = findViewById(R.id.tv_current_id);
        tvCurrentDesc = findViewById(R.id.tv_current_desc);
        etName = findViewById(R.id.et_name);
        etUserId = findViewById(R.id.et_user_id);
        etDescription = findViewById(R.id.et_description);
        ivProfileImage = findViewById(R.id.iv_profile_image);
        ivCurrentImage = findViewById(R.id.iv_current_image);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnUploadData = findViewById(R.id.btn_upload_data);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("User Dashboard");
            }
        }
    }


    private void setupClickListeners() {
        String userName = sharedPreferences.getString("user_name", "User");
        tvWelcome.setText("Welcome, " + userName + "!");

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnUploadData.setOnClickListener(v -> uploadUserData());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivProfileImage.setImageURI(selectedImageUri);

            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] bytes = getBytes(inputStream);
                encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
            } catch (Exception e) {
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void uploadUserData() {
        String name = etName.getText().toString().trim();
        String userId = etUserId.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty() || userId.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);
        btnUploadData.setEnabled(false);

        String userSessionId = sharedPreferences.getString("user_id", "");

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "upload_user_data.php",
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.optBoolean("success", false);
                        String message = jsonResponse.optString("message", "Unknown response");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (success) {
                            loadUserProfile(); // Refresh the profile
                            clearForm();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(android.view.View.GONE);
                    btnUploadData.setEnabled(true);
                },
                error -> {
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(android.view.View.GONE);
                    btnUploadData.setEnabled(true);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_session_id", userSessionId);
                params.put("name", name);
                params.put("user_id", userId);
                params.put("description", description);
                params.put("image", encodedImage);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void loadUserProfile() {
        String userSessionId = sharedPreferences.getString("user_id", "");

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "get_user_profile.php",
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.optBoolean("success", false);

                        if (success && jsonResponse.has("data")) {
                            JSONObject data = jsonResponse.getJSONObject("data");
                            tvCurrentName.setText("Name: " + data.optString("name", "N/A"));
                            tvCurrentId.setText("ID: " + data.optString("user_id_custom", "N/A"));
                            tvCurrentDesc.setText("Description: " + data.optString("description", "N/A"));

                            String imageFile = data.optString("image", "");
                            if (!imageFile.isEmpty()) {
                                String imageUrl = BASE_URL + "uploads/" + imageFile;
                                Glide.with(this).load(imageUrl).into(ivCurrentImage);
                            }
                        } else {
                            tvCurrentName.setText("No profile data found");
                            tvCurrentId.setText("");
                            tvCurrentDesc.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle silently for now
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_session_id", userSessionId);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void clearForm() {
        etName.setText("");
        etUserId.setText("");
        etDescription.setText("");
        ivProfileImage.setImageResource(R.drawable.ic_image_placeholder);
        encodedImage = "";
        selectedImageUri = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
