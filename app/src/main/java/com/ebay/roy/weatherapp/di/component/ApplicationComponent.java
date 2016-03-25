package com.ebay.roy.weatherapp.di.component;

import android.app.Application;

import com.ebay.roy.weatherapp.activity.BaseActivity;
import com.ebay.roy.weatherapp.di.module.ApplicationModule;
import com.ebay.roy.weatherapp.service.WeatherApiService;

import javax.inject.Singleton;

import dagger.Component;


// Constraints this component to one-per-application or unscoped bindings.
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void injectActivity(BaseActivity activity);

    //Exposed to sub-graphs.
    Application application();
    WeatherApiService provideWeatherService();
}
