package com.example.secureroleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private List<UserModel> userList;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    private static final String BASE_URL = "http://172.20.10.7/secure_role_app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupToolbar();
        setupRecyclerView();

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        String adminName = sharedPreferences.getString("user_name", "Admin");
        tvWelcome.setText("Welcome, " + adminName + "!");

        loadAllUsers();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        rvUsers = findViewById(R.id.rv_users);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Admin Dashboard");
            }
        }
    }


    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, BASE_URL);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);
    }

    private void loadAllUsers() {
        StringRequest request = new StringRequest(Request.Method.GET, BASE_URL + "get_all_users.php",
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.optBoolean("success", false);

                        if (success) {
                            JSONArray usersArray = jsonResponse.optJSONArray("users");
                            if (usersArray != null) {
                                userList.clear();

                                for (int i = 0; i < usersArray.length(); i++) {
                                    JSONObject userObj = usersArray.optJSONObject(i);
                                    if (userObj != null) {
                                        UserModel user = new UserModel();
                                        user.setId(userObj.optString("id", ""));
                                        user.setName(userObj.optString("name", ""));
                                        user.setEmail(userObj.optString("email", ""));
                                        user.setRole(userObj.optString("role", ""));
                                        user.setUserId(userObj.optString("user_id_custom", "N/A"));
                                        user.setDescription(userObj.optString("description", "N/A"));
                                        user.setImage(userObj.optString("image", ""));
                                        user.setCreatedAt(userObj.optString("created_at", ""));

                                        userList.add(user);
                                    }
                                }

                                userAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
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
        }else if (item.getItemId() == R.id.action_profile) {
            // open admin profile
            startActivity(new Intent(this, AdminProfileActivity.class));
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
