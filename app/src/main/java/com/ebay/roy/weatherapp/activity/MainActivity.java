package com.ebay.roy.weatherapp.activity;

import android.os.Bundle;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.manager.ApiManager;
import com.ebay.roy.weatherapp.model.Weather;
import com.ebay.roy.weatherapp.service.WeatherApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    WeatherApiService weatherApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherApiService = getApplicationComponent().provideWeatherService();
        Call<Weather> call = weatherApiService.getWeather("Syd");
        call.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                Boolean a = true;
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Boolean a = true;
            }
        });
    }

    @Override
    public int layoutTemplate() {
        return R.layout.activity_main;
    }


}
