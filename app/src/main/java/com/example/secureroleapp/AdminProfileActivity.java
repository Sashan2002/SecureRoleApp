package com.example.secureroleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
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

public class AdminProfileActivity extends AppCompatActivity {
    private static final String TAG = "AdminProfileActivity";
    private static final int IMAGE_PICK_REQUEST = 1002;
    private static final String BASE_URL = "http://172.20.10.7/secure_role_app/"; // update if needed

    private TextView tvWelcome;
    private TextView tvCurrentName, tvCurrentCode, tvCurrentDesc;
    private ImageView ivProfileImage, ivCurrentImage;
    private EditText etName, etAdminCode, etDescription;
    private Button btnSelectImage, btnSave;
    private ProgressBar progressBar;

    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private Uri selectedImageUri;
    private String encodedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        initViews();
        setupToolbar();

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        String adminName = sharedPreferences.getString("user_name", "Admin");
        tvWelcome.setText("Welcome, " + adminName + "!");

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnSave.setOnClickListener(v -> uploadAdminData());

        loadAdminProfile();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvCurrentName = findViewById(R.id.tv_current_name);
        tvCurrentCode = findViewById(R.id.tv_current_code);
        tvCurrentDesc = findViewById(R.id.tv_current_desc);
        ivProfileImage = findViewById(R.id.iv_profile_image);
        ivCurrentImage = findViewById(R.id.iv_current_image);
        etName = findViewById(R.id.et_name);
        etAdminCode = findViewById(R.id.et_user_id);
        etDescription = findViewById(R.id.et_description);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSave = findViewById(R.id.btn_upload_data);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Admin Profile");
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
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
                Log.e(TAG, "Image processing error", e);
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

    private void uploadAdminData() {
        String name = etName.getText().toString().trim();
        String adminCode = etAdminCode.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String adminId = sharedPreferences.getString("user_id", "");

        if (name.isEmpty() || adminCode.isEmpty()) {
            Toast.makeText(this, "Name and code are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        String url = BASE_URL + "upload_admin_data.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    try {
                        JSONObject json = new JSONObject(response);
                        Toast.makeText(this, json.optString("message"), Toast.LENGTH_SHORT).show();
                        if (json.optBoolean("success", false)) {
                            loadAdminProfile();
                            clearForm();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Response parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("admin_id", adminId);
                params.put("name", name);
                params.put("admin_code", adminCode);
                params.put("description", description);
                params.put("image", encodedImage);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void loadAdminProfile() {
        progressBar.setVisibility(View.VISIBLE);
        String adminId = sharedPreferences.getString("user_id", "");
        String url = BASE_URL + "get_admin_profile.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success", false) && json.has("data")) {
                            JSONObject data = json.getJSONObject("data");
                            String name = data.optString("name", "N/A");
                            String code = data.optString("admin_code", "");
                            String desc = data.optString("description", "");
                            String imageFile = data.optString("image", "");

                            tvCurrentName.setText("Name: " + name);
                            tvCurrentCode.setText("Code: " + (code.isEmpty() ? "N/A" : code));
                            tvCurrentDesc.setText("Description: " + (desc.isEmpty() ? "N/A" : desc));

                            if (!imageFile.isEmpty()) {
                                String imageUrl = BASE_URL + "uploads/" + imageFile;
                                Glide.with(this).load(imageUrl).into(ivCurrentImage);
                            } else {
                                ivCurrentImage.setImageResource(R.drawable.ic_image_placeholder);
                            }
                        } else {
                            tvCurrentName.setText("No profile data found");
                            tvCurrentCode.setText("");
                            tvCurrentDesc.setText("");
                            ivCurrentImage.setImageResource(R.drawable.ic_image_placeholder);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    // silent or show message
                }) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("admin_id", adminId);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void clearForm() {
        etName.setText("");
        etAdminCode.setText("");
        etDescription.setText("");
        ivProfileImage.setImageResource(R.drawable.ic_image_placeholder);
        selectedImageUri = null;
        encodedImage = "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle back arrow in toolbar
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

