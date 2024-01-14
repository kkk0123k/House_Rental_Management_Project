package com.example.houserentalmanagementproject;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class OwnerModel implements Serializable {
    @PropertyName("phone")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @PropertyName("phone")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String search;
    public String userEmail;
    String houseId;
    String username;
    String phoneNumber;

    public OwnerModel(String search, String userEmail, String username, String phoneNumber, String ownerId, String imageUrl) {
        this.search = search;
        this.userEmail = userEmail;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.ownerId = ownerId;
        this.imageUrl = imageUrl;
    }

    String ownerId;
    public String imageUrl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public OwnerModel() {
    }

    public OwnerModel(String houseId, String name, String phoneNumber, String ownerId) {
        this.houseId = houseId;
        this.username = name;
        this.phoneNumber = phoneNumber;
        this.ownerId = ownerId;
    }


}
