package com.ebay.roy.weatherapp.view.presenter.search;

import com.ebay.roy.weatherapp.model.Weather;
import com.ebay.roy.weatherapp.service.WeatherApiService;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by hungr on 29/03/16.
 */
public interface ISearchViewPresenter {
    void showLoadingDialog();
    void dismissLoadingDialog();
    WeatherApiService getWeatherApiService();
    void addMarker(Weather weather);
    void hideSearchView();
    void addWeatherMarkerByLocation(LatLng latLng);
    void addWeatherMarkerBySearch(String searchText);
    void displayMessage(String message);
}
