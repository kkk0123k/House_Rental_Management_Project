package com.example.houserentalmanagementproject;

import java.util.Date;

public class Payment {
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    private String paymentId; // Add this line
    private String billId;
    private String renterId;
    private String houseId;
    private double amount;
    private Date date;

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    private String status;
    private int month;
    private int year;
    private boolean paycheck = false;

    public boolean isPaycheck() {
        return paycheck;
    }

    public void setPaycheck(boolean paycheck) {
        this.paycheck = paycheck;
    }

    public Payment(String renterId, String houseId, double amount, Date date, boolean paycheck) {
        this.renterId = renterId;
        this.houseId = houseId;
        this.amount = amount;
        this.date = date;
        this.paycheck = paycheck;
    }

    public String getRenterId() {
        return renterId;
    }

    public String getHouseId() {
        return houseId;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    // getters and setters for each field
}