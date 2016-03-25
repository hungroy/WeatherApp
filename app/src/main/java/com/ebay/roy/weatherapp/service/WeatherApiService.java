package com.ebay.roy.weatherapp.service;

import com.ebay.roy.weatherapp.model.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherApiService {

    @GET("weather")
    Call<Weather> getWeather(@Query("q") String query);

}