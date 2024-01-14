package com.example.houserentalmanagementproject;

import java.io.Serializable;

public class MemberModel implements Serializable {
    String roomNumber;
    double bankAccountBalance; // The balance of the user's demo bank account.
    String memberId;
    String houseId;
    String age;
    String name;
    String job;
    String rent;
    String joiningDate;
    String phoneNumber;
    String ownerId;

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }


    public double getBankAccountBalance() {
        return bankAccountBalance;
    }

    public void setBankAccountBalance(double bankAccountBalance) {
        this.bankAccountBalance = bankAccountBalance;
    }

    public MemberModel(double bankAccountBalance) {
        this.bankAccountBalance = bankAccountBalance;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public MemberModel() {
    }

    public MemberModel(String memberId, String age, String name,  String roomNumber, String rent, String joiningDate, String phoneNumber, String ownerId, String houseId) {
        this.roomNumber = roomNumber;
        this.ownerId = ownerId;
        this.age = age;
        this.name = name;
        this.rent = rent;
        this.joiningDate = joiningDate;
        this.phoneNumber = phoneNumber;
        this.memberId = memberId;
        this.houseId = houseId;
    }

    public String getMemberId() {
        return memberId;
    }

}
