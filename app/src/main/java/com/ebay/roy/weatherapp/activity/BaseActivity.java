package com.ebay.roy.weatherapp.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.WeatherApplication;
import com.ebay.roy.weatherapp.di.component.ActivityComponent;
import com.ebay.roy.weatherapp.di.component.ApplicationComponent;
import com.ebay.roy.weatherapp.di.component.DaggerActivityComponent;
import com.ebay.roy.weatherapp.di.module.ActivityModule;
import com.ebay.roy.weatherapp.manager.ImageLoaderManager;
import com.ebay.roy.weatherapp.service.WeatherApiService;
import com.nostra13.universalimageloader.core.ImageLoader;


import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;

public abstract class BaseActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.containerBody) CoordinatorLayout containerBody;

    @Inject WeatherApiService weatherApiService;
    @Inject ImageLoader imageLoader;

    public ActivityComponent activityComponent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutTemplate());
        ButterKnife.bind(this);
        this.getApplicationComponent().injectActivity(this);    //set dependency injection
        setSupportActionBar(toolbar);
        Icepick.restoreInstanceState(this, savedInstanceState); //icepick to restore state
        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
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

    protected void initActivityComponent() {
        //construct activity module
        activityComponent = DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    public ActivityComponent getComponent() {
        //activity component is missing probably some activity called onfinish, reinit if it's null
        if (activityComponent == null) {
            initActivityComponent();
        }

        return activityComponent;
    }

    protected void replaceFragmentContent(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerBody, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    public void changeToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
