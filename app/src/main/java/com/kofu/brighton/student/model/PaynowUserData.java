package com.kofu.brighton.student.model;

public class PaynowUserData {
    public String phoneNumber;
    public double amount;
    public String email;

    public PaynowUserData(String phoneNumber, double amount, String email) {
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.email = email;
    }
}
