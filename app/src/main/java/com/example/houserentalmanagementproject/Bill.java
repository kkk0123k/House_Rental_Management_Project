package com.example.houserentalmanagementproject;

public class Bill {
    String memberId;
    String houseId;
    private String status;
    private String billId;
    private String name;
    private String roomNumber;
    private String rentBill;
    private String waterBill;
    private String electricBill;
    private String billSum;
    private int month;
    private int year;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getBillSum() {
        return billSum;
    }

    public void setBillSum(String billSum) {
        this.billSum = billSum;
    }

    public Bill(){}

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public Bill(String status, String billId, String name, String roomNumber, String rentBill, String waterBill, String electricBill, String billSum, int month, int year,String memberId,String houseId) {
        this.status = status;
        this.billId = billId;
        this.name = name;
        this.roomNumber = roomNumber;
        this.rentBill = rentBill;
        this.waterBill = waterBill;
        this.electricBill = electricBill;
        this.billSum = billSum;
        this.month = month;
        this.year = year;
        this.memberId = memberId;
        this.houseId = houseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRentBill() {
        return rentBill;
    }

    public void setRentBill(String rentHouse) {
        this.rentBill = rentHouse;
    }

    public String getWaterBill() {
        return waterBill;
    }

    public void setWaterBill(String waterBill) {
        this.waterBill = waterBill;
    }

    public String getElectricBill() {
        return electricBill;
    }

    public void setElectricBill(String electricBill) {
        this.electricBill = electricBill;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    // getters and setters for each field
}
