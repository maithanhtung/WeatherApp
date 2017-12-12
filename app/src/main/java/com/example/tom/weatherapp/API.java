package com.example.tom.weatherapp;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tom on 12/12/2017.
 */

public class API {
    public static final String BASE_URL = "https://weatherapp.eficode.fi/api/";

    static CustomInterceptor interceptor = new CustomInterceptor();

    static OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    static Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit get() {
        return retrofit;
    }
}
