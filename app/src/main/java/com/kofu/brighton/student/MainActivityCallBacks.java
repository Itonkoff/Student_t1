package com.kofu.brighton.student;

public interface MainActivityCallBacks {
    void setToken(String token, int student);
    String getToken();
    int getStudent();
    void hideFab();
    void showFab();
}
