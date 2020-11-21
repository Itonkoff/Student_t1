package com.kofu.brighton.student.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIServiceBuilder {
    private static final String BASE_URL = "http://192.168.43.142/t1/api/";
//    private static OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.build();

    public static <T> T buildService(Class<T> serviceType){
        return retrofit.create(serviceType);
    }
}
