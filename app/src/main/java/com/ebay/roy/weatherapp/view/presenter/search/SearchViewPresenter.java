package com.ebay.roy.weatherapp.view.presenter.search;

import android.app.Activity;
import android.widget.Toast;

import com.ebay.roy.weatherapp.activity.MainActivity;
import com.ebay.roy.weatherapp.model.Weather;
import com.ebay.roy.weatherapp.view.presenter.BasePresenter;
import com.google.android.gms.maps.model.LatLng;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hungr on 29/03/16.
 */
public class SearchViewPresenter extends BasePresenter {
    ISearchViewPresenter searchViewPresenter;
    Subscription subscription;

    public SearchViewPresenter(ISearchViewPresenter searchViewPresenter) {
        this.searchViewPresenter = searchViewPresenter;
    }

    @Override
    public void render() {

    }

    public void addWeatherMarkerBySearch(String searchText) {
        searchViewPresenter.showLoadingDialog();
        Observable<Weather> call = searchViewPresenter.getWeatherApiService().getWeatherByCity(searchText);
        subscription = call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                        searchViewPresenter.dismissLoadingDialog();
                        Toast.makeText(getActivity(), "on complete", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        searchViewPresenter.dismissLoadingDialog();
                        Toast.makeText(getActivity(), "error, " + e.getMessage() , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        searchViewPresenter.addMarker(weather);
                        //probably looking at the map page now, close all search if search bar is still open
                        searchViewPresenter.hideSearchView();
                    }
                });
    }

    public void addWeatherMarkerByLocation(LatLng latLng) {
        searchViewPresenter.showLoadingDialog();
        Observable<Weather> call = searchViewPresenter.getWeatherApiService().getWeatherByLatLon(latLng.latitude, latLng.longitude);
        subscription = call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                        searchViewPresenter.dismissLoadingDialog();
                        Toast.makeText(getActivity(), "on complete", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        searchViewPresenter.dismissLoadingDialog();
                        Toast.makeText(getActivity(), "error, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        searchViewPresenter.addMarker(weather);
                        //probably looking at the map page now, close all search if search bar is still open
                        searchViewPresenter.hideSearchView();
                    }
                });
    }


    @Override
    public Activity getActivity() {
        if (searchViewPresenter == null) {
            return null;
        }
        return ((MainActivity) searchViewPresenter);
    }

    @Override
    public void destroy() {
        if (subscription != null) {
            subscription.unsubscribe(); //cancel all calls if still running
        }
        subscription = null;
        searchViewPresenter = null;
    }
}
