package com.ebay.roy.weatherapp;

import android.app.Application;

import com.ebay.roy.weatherapp.di.component.ApplicationComponent;
import com.ebay.roy.weatherapp.di.module.ApplicationModule;
import com.ebay.roy.weatherapp.di.component.DaggerApplicationComponent;

/**
 * Created by Roy on 3/25/2016.
 */
public class WeatherApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = initComponent();
    }

    public ApplicationComponent initComponent() {
        //initialising DI

        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
