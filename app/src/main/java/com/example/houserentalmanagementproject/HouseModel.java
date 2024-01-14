package com.example.houserentalmanagementproject;

import java.io.Serializable;

public class HouseModel implements Serializable {
    String houseId, noOfRoom, rentPerRoom, houseDescription, houseLocation, houseImage, userId, search;

    public String getHouseImage() {
        return houseImage;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setHouseImage(String houseImage) {
        this.houseImage = houseImage;
    }


    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getNoOfRoom() {
        return noOfRoom;
    }

    public void setNoOfRoom(String noOfRoom) {
        this.noOfRoom = noOfRoom;
    }

    public String getRentPerRoom() {
        return rentPerRoom;
    }

    public void setRentPerRoom(String rentPerRoom) {
        this.rentPerRoom = rentPerRoom;
    }

    public String getHouseDescription() {
        return houseDescription;
    }

    public void setHouseDescription(String houseDescription) {
        this.houseDescription = houseDescription;
    }

    public String getHouseLocation() {
        return houseLocation;
    }

    public void setHouseLocation(String houseLocation) {
        this.houseLocation = houseLocation;
    }

    public HouseModel() {
    }

    public HouseModel(String houseId, String noOfRoom, String rentPerRoom, String houseDescription, String houseLocation, String houseImage, String userId, String search) {
        this.houseId = houseId;
        this.noOfRoom = noOfRoom;
        this.rentPerRoom = rentPerRoom;
        this.houseDescription = houseDescription;
        this.houseLocation = houseLocation;
        this.houseImage = houseImage;
        this.userId = userId;
        this.search = search;
    }
}
