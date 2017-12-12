package com.example.tom.weatherapp;

import com.example.tom.weatherapp.models.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by tom on 12/12/2017.
 */

public interface WeatherAppAPI {
    @GET("forecast")
    Call<Response> getWeatherWithLL(@Query("lat") String lat, @Query("lon") String lon);

    @GET("forecast")
    Call<Response> getWeatherWithoutLL();
}
