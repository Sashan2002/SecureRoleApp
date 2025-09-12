package com.example.secureroleapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserModel> userList;
    private String baseUrl;

    public UserAdapter(List<UserModel> userList, String baseUrl) {
        this.userList = userList;
        this.baseUrl = baseUrl;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);

        holder.tvName.setText("Name: " + user.getName());
        holder.tvEmail.setText("Email: " + user.getEmail());
        holder.tvRole.setText("Role: " + user.getRole());
        holder.tvUserId.setText("User ID: " + (user.getUserId().isEmpty() ? "N/A" : user.getUserId()));
        holder.tvDescription.setText("Description: " + (user.getDescription().isEmpty() ? "N/A" : user.getDescription()));
        holder.tvCreatedAt.setText("Joined: " + user.getCreatedAt());

        // Load image if available
        if (!user.getImage().isEmpty()) {
            String imageUrl = baseUrl + "uploads/" + user.getImage();
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.ivUserImage);
        } else {
            holder.ivUserImage.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole, tvUserId, tvDescription, tvCreatedAt;
        ImageView ivUserImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvRole = itemView.findViewById(R.id.tv_role);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            ivUserImage = itemView.findViewById(R.id.iv_user_image);
        }
    }
}