package com.ebay.roy.weatherapp.di.module;

import android.app.Activity;

import com.ebay.roy.weatherapp.di.component.PerActivity;
import com.ebay.roy.weatherapp.manager.SearchManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by hungr on 30/11/15.
 */
@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    Activity activity() {
        return this.activity;
    }

    @Provides
    @PerActivity
    SearchManager provideSearchManager() {
        return new SearchManager(activity);
    }


}
