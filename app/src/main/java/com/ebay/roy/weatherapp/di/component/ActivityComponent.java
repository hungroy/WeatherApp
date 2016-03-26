package com.ebay.roy.weatherapp.di.component;

import android.app.Activity;


import com.ebay.roy.weatherapp.activity.fragment.BaseFragment;
import com.ebay.roy.weatherapp.di.module.ActivityModule;
import com.ebay.roy.weatherapp.manager.SearchManager;

import dagger.Component;
import dagger.Lazy;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(BaseFragment fragment);

    //expose to sub-graph
    Activity activity();
    Lazy<SearchManager> searchManager();
}
