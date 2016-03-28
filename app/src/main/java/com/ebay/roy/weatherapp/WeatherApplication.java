package com.ebay.roy.weatherapp;

import android.app.Application;
import android.content.Context;

import com.ebay.roy.weatherapp.di.component.ApplicationComponent;
import com.ebay.roy.weatherapp.di.component.DaggerApplicationComponent;
import com.ebay.roy.weatherapp.di.module.ApplicationModule;
import com.ebay.roy.weatherapp.model.Weather;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Roy on 3/25/2016.
 */
public class WeatherApplication extends Application {
    private ApplicationComponent applicationComponent;

    private RefWatcher refWatcher;


    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = initComponent();
        refWatcher = LeakCanary.install(this);
        Stetho.initializeWithDefaults(this);

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

    public static RefWatcher getRefWatcher(Context context) {
        WeatherApplication application = (WeatherApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}
