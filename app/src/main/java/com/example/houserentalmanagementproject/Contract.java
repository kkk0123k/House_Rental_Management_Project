package com.example.houserentalmanagementproject;

import java.io.Serializable;

public class Contract implements Serializable {
    public Contract(String memberName, String ownerName, String houseName, String memberId, String ownerId, String houseId, String roomNumber, String startDate, String endDate, double rentAmount, String status) {
        this.memberName = memberName;
        this.ownerName = ownerName;
        this.houseName = houseName;
        this.memberId = memberId;
        this.ownerId = ownerId;
        this.houseId = houseId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentAmount = rentAmount;
        this.status = status;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    String memberName;
    String ownerName;
    String houseName;
    String memberId;
    String ownerId;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    String houseId;
    String roomNumber;
    String startDate;
    String endDate;
    double rentAmount;

    public Contract() {
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(double rentAmount) {
        this.rentAmount = rentAmount;
    }

    String status;  // New attribute for the status of the contract

    // getters and setters for renter, owner, house, roomNumber, startDate, endDate, rentAmount...

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
