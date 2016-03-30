package com.ebay.roy.weatherapp.di.module;


import android.app.Application;


import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.manager.ApiManager;
import com.ebay.roy.weatherapp.manager.ImageLoaderManager;
import com.ebay.roy.weatherapp.manager.SearchManager;
import com.ebay.roy.weatherapp.service.WeatherApiService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;


@Module
public class MockApplicationModule {
    Application application;

    public MockApplicationModule(Application application) {
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
        //return function where api returns a file instead of online data
        String weatherApiUrl = application.getString(R.string.weather_api_url);
        return apiManager.getWeatherServiceFromFile(weatherApiUrl, application, R.raw.raw_openweather_response_test);
    }


    @Provides
    @Singleton
    ImageLoader provideImageLoaderService() {
        return mock(ImageLoader.class);
    }





}
