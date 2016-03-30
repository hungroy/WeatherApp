package com.ebay.roy.weatherapp;

import com.ebay.roy.weatherapp.di.component.ApplicationComponent;
import com.ebay.roy.weatherapp.di.component.DaggerMockApplicationComponent;
import com.ebay.roy.weatherapp.di.module.MockApplicationModule;


public class MockWeatherApplication extends WeatherApplication {
    @Override
    public ApplicationComponent initComponent() {
        return DaggerMockApplicationComponent.builder()
                .mockApplicationModule(new MockApplicationModule(this))
                .build();
    }
}
