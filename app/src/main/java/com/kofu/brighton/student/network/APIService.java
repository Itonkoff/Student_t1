package com.kofu.brighton.student.network;

import com.kofu.brighton.student.model.DashboardData;
import com.kofu.brighton.student.model.Login;
import com.kofu.brighton.student.model.PersonalInfo;
import com.kofu.brighton.student.model.Register;
import com.kofu.brighton.student.model.Token;
import com.kofu.brighton.student.model.Topup;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    /*
     * Authentication
     *
     * */
    @POST("Authentication/SignIn")
    Call<Token> login(@Body Login login);

    @POST("Authentication/SignUp/Student")
    Call<Void> register(@Body Register details);

    /*
     * Student
     *
     * */
    @GET("Students/Dash/{studentId}")
    Call<DashboardData> getDashBoardDataForStudent(@Path("studentId") int studentID);

    @POST("Canteen/Topup")
    Call<Void> canteenTopup(@Body Topup topup);

    @GET("Students/Details/{id}")
    Call<PersonalInfo> getPersonalInformation(@Path("id") int studentId);

}
