package com.ebay.roy.weatherapp.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebay.roy.weatherapp.activity.BaseActivity;
import com.ebay.roy.weatherapp.di.component.ActivityComponent;

import icepick.Icepick;

/**
 * Created by Roy on 3/26/2016.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        injectService();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    public View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layoutTemplate(), container, false);
    }

    protected void injectService() {
        ((BaseActivity)getActivity()).getComponent().inject(this);
    }

    public ActivityComponent getComponent() {
        return ((BaseActivity)getActivity()).getComponent();
    }
    /**
     *
     * @return fragment layout template to render
     */
    public abstract int layoutTemplate();


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
