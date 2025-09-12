package com.example.secureroleapp;

public class UserModel {
    private String id;
    private String name;
    private String email;
    private String role;
    private String userId;
    private String description;
    private String image;
    private String createdAt;

    // Default constructor
    public UserModel() {
    }

    // Constructor with all parameters
    public UserModel(String id, String name, String email, String role,
                     String userId, String description, String image, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.userId = userId;
        this.description = description;
        this.image = image;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}