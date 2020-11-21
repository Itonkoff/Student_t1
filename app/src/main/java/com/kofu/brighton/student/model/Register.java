package com.kofu.brighton.student.model;

import java.util.UUID;

public class Register {
    private String studentId;
    private String email;
    private String phoneNumber;
    private String password;
    private String passwordConfirm;

    public Register(String studentId, String email, String phoneNumber, String password, String passwordConfirm) {
        this.studentId = studentId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
