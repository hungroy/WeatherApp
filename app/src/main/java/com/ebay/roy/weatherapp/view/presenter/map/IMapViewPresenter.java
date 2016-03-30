package com.ebay.roy.weatherapp.view.presenter.map;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by hungr on 29/03/16.
 */
public interface IMapViewPresenter {
    SharedPreferences getSharedPreference();
    int getMapContainerId();
    FragmentManager getActivityFragmentManager();
    void addWeatherMarkerByLocation(LatLng latLng);
    View getGPSBtnView();
    void goToDetailedWeatherView(Bundle bundle);
    ImageLoader getImageLoader();
    void displayMessage(String message);
}
