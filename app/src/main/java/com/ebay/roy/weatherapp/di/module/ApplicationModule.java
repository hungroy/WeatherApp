package com.ebay.roy.weatherapp.di.module;


import android.app.Application;


import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.manager.ApiManager;
import com.ebay.roy.weatherapp.service.WeatherApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class ApplicationModule {
    Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    WeatherApiService provideWeatherService() {
        ApiManager apiManager = new ApiManager();
        String weatherApiUrl = application.getString(R.string.weather_api_url);
        String weatherApiKey = application.getString(R.string.weather_api_key);
        return apiManager.getWeatherService(weatherApiUrl, weatherApiKey);
    }


}
