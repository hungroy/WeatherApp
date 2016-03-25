package com.ebay.roy.weatherapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.WeatherApplication;
import com.ebay.roy.weatherapp.di.component.ApplicationComponent;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutTemplate());
        ButterKnife.bind(this);
        this.getApplicationComponent().injectActivity(this);    //set dependency injection
    }

    /**
     *
     * @return activity layout template to render
     */
    public abstract int layoutTemplate();

    /**
     *
     * @return dependency injection graph
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((WeatherApplication) getApplication()).getApplicationComponent();
    }

}
