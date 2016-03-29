package com.ebay.roy.weatherapp.activity;

import android.Manifest;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;

import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.activity.fragment.SearchDisplayFragment;
import com.ebay.roy.weatherapp.manager.ApiManager;
import com.ebay.roy.weatherapp.manager.ImageLoaderManager;
import com.ebay.roy.weatherapp.model.Weather;
import com.ebay.roy.weatherapp.model.Weather_;
import com.ebay.roy.weatherapp.service.WeatherApiService;
import com.ebay.roy.weatherapp.view.presenter.map.IMapViewPresenter;
import com.ebay.roy.weatherapp.view.presenter.map.MapViewPresenter;
import com.ebay.roy.weatherapp.view.presenter.search.ISearchViewPresenter;
import com.ebay.roy.weatherapp.view.presenter.search.SearchViewPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import butterknife.Bind;


import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseActivity implements IMapViewPresenter, ISearchViewPresenter {

    SearchView searchView;

    @Bind(R.id.gpsBtn) FloatingActionButton gpsBtn;
    ProgressDialog progressDialog;

    MapViewPresenter mapViewPresenter;
    SearchViewPresenter searchViewPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapViewPresenter = new MapViewPresenter(this);
        mapViewPresenter.render();

        searchViewPresenter = new SearchViewPresenter(this);
        searchViewPresenter.render();


    }

    @Override
    public int layoutTemplate() {
        return R.layout.activity_main;
    }

    /**
     * initialise menu search action
     */
    public void initSearchAction() {
        //searchView not initialised, do nothing
        if (searchView == null) {
            return;
        }

        searchView.setQueryHint("Enter city or zip");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDisplayFragment searchDisplayFragment = new SearchDisplayFragment();
                replaceFragmentContent(searchDisplayFragment);
                searchDisplayFragment.addSearchViewTextListener(searchView); //attach search as a listener to fragment
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        initSearchAction();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewPresenter.saveLastLocation();
    }

    @Override
    public SharedPreferences getSharedPreference() {
        return getPreferences(MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapViewPresenter.restoreLastLocation();
    }

    @Override
    public int getMapContainerId() {
        return R.id.containerBody;
    }

    @Override
    public FragmentManager getActivityFragmentManager() {
        return getFragmentManager();
    }

    @Override
    public View getGPSBtnView() {
        return gpsBtn;
    }

    @Override
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    @Override
    public void showLoadingDialog() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading ....");
            progressDialog.setMessage("Searching weather location");
        }
        progressDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void goToDetailedWeatherView(Bundle bundle) {
        Intent intent = new Intent(this, WeatherDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public WeatherApiService getWeatherApiService() {
        return weatherApiService;
    }


    @Override
    public void addMarker(Weather weather) {
        mapViewPresenter.addMarker(weather);
    }

    @Override
    public void hideSearchView() {
        searchView.setQuery("", false);
        searchView.setIconified(true);
    }

    @Override
    public void addWeatherMarkerByLocation(LatLng latLng) {
        searchViewPresenter.addWeatherMarkerByLocation(latLng);
    }

    @Override
    public void addWeatherMarkerBySearch(String searchText) {
        searchViewPresenter.addWeatherMarkerBySearch(searchText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapViewPresenter != null) {
            mapViewPresenter.destroy();
        }

        if (searchViewPresenter != null) {
            searchViewPresenter.destroy();
        }

    }


}
