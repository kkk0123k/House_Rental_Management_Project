package com.example.houserentalmanagementproject;

import java.io.Serializable;

public class UserModel implements Serializable {
    public String houseId;
    public String imageUrl;
    public String phone;
    public String search;
    public String userEmail;

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String userId;
    public String username;
    public boolean isAdmin;

    public UserModel() {
        // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
    }

    public UserModel(String houseId, String imageUrl, String phone, String search, String userEmail, String userId, String username, boolean isAdmin) {
        this.houseId = houseId;
        this.imageUrl = imageUrl;
        this.phone = phone;
        this.search = search;
        this.userEmail = userEmail;
        this.userId = userId;
        this.username = username;
        this.isAdmin = isAdmin;
    }

    // Getters and setters for each field...

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }
}
