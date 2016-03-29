package com.ebay.roy.weatherapp.view.presenter;

import android.app.Activity;

/**
 * Created by Roy on 3/26/2016.
 */
public abstract class BasePresenter {
    public abstract void render();
    public abstract Activity getActivity();
    public abstract void destroy();
}
