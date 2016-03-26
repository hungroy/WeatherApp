package com.ebay.roy.weatherapp.service;

import com.ebay.roy.weatherapp.model.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface WeatherApiService {

    @GET("weather")
    Observable<Weather> getWeatherByCity(@Query("q") String query);

    @GET("weather")
    Observable<Weather> getWeatherByZip(@Query("zip") String query);

    @GET("weather")
    Observable<Weather> getWeatherByLatLon(@Query("lat") double lat, @Query("lon") double lon );

}