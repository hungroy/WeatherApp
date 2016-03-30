package com.ebay.roy.weatherapp.activity;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;

import com.ebay.roy.weatherapp.WeatherApplication;
import com.ebay.roy.weatherapp.di.component.DaggerMockActivityComponent;
import com.ebay.roy.weatherapp.di.component.MockApplicationComponent;
import com.ebay.roy.weatherapp.di.module.MockActivityModule;

/**
 * Created by hungr on 28/01/16.
 */
public class MainActivityMock extends MainActivity {
    /* overriding component with the mock objects */
    @Override
    protected void initActivityComponent() {

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        WeatherApplication app
                = (WeatherApplication) instrumentation.getTargetContext().getApplicationContext();

        //construct activity module
        activityComponent = DaggerMockActivityComponent.builder()
                .mockApplicationComponent((MockApplicationComponent) app.getApplicationComponent())
                .mockActivityModule(new MockActivityModule(this))
                .build();

    }
}
