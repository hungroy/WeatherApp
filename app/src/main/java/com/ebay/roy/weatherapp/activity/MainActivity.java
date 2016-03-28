package com.ebay.roy.weatherapp.activity;

import android.Manifest;
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
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.State;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    Subscription subscription;
    SearchView searchView;
    MapFragment mapFragment;
    GoogleMap googleMap;

    @Bind(R.id.gpsBtn) FloatingActionButton gpsBtn;
    ProgressDialog progressDialog;
    public static final String LAST_LOCATION_LAT = "LastLocationLat";
    public static final String LAST_LOCATION_LNG = "LastLocationLng";
    public static final String WEATHER_DETAIL_KEY = "WeatherDetailKey";
    public static final String WEATHER_DETAIL_BITMAP_KEY = "WeatherDetailBitmapKey";
    public static final Float MIN_LOCATION_LAT_LNG_VALUE = -999f;
    public static final Integer DEFAULT_MAP_ZOOM_LEVEL = 10;

    Float lastLat = MIN_LOCATION_LAT_LNG_VALUE;  //set a negative value that exceeds the max negative lat and lng to check for null
    Float lastLng = MIN_LOCATION_LAT_LNG_VALUE;

    HashMap<String, Weather> markerReference; //reference to all the marker currently created
    HashMap<String, Bitmap> markerIconReference; //reference to all bitmap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }

        ButterKnife.bind(this);
        markerReference = new HashMap<>();
        markerIconReference = new HashMap<>();
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.containerBody, mapFragment);
            fragmentTransaction.commit();
        }

        mapFragment.getMapAsync(this);

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
        //store the last location before stop, we could store whole weather object this would avoid another api call, but the user probably wants the latest weather status, so we query api just by storing lnt lat
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putFloat(LAST_LOCATION_LAT , lastLat);
        editor.putFloat(LAST_LOCATION_LNG, lastLng);
        editor.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Float lat = getPreferences(MODE_PRIVATE).getFloat(LAST_LOCATION_LAT, MIN_LOCATION_LAT_LNG_VALUE);
        Float lng = getPreferences(MODE_PRIVATE).getFloat(LAST_LOCATION_LNG, MIN_LOCATION_LAT_LNG_VALUE);
        if (lat > MIN_LOCATION_LAT_LNG_VALUE && lng > MIN_LOCATION_LAT_LNG_VALUE) {
            lastLat = lat;
            lastLng = lng;
        }

        //remove these value, so when restate won't have the previous value
        getPreferences(MODE_PRIVATE)
                .edit()
                .remove(LAST_LOCATION_LAT)
                .remove(LAST_LOCATION_LNG)
                .commit();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe(); //cancel all calls if still running
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is now ready", Toast.LENGTH_SHORT).show();
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false); //hide control buttons from google map
        addGPSBtnActionOnMap();

        //if have previous location restore previous location
        if (lastLng > MIN_LOCATION_LAT_LNG_VALUE && lastLat > MIN_LOCATION_LAT_LNG_VALUE) {
            addWeatherMarkerByLocation(new LatLng(lastLat, lastLng));
        }
    }

    private void reSyncMap() {
        Toast.makeText(this, "Map not ready yet, please try again later", Toast.LENGTH_SHORT).show();
        mapFragment.getMapAsync(this);
    }

    private void addGPSBtnActionOnMap() {
        if (googleMap == null) {
            reSyncMap();
            return;
        }
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overridin
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(getApplicationContext(), "GPS Request permission failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                // Get the name of the best provider
                String provider = locationManager.getBestProvider(new Criteria(), true);
                // Get Current Location
                Location myLocation = locationManager.getLastKnownLocation(provider);
                addWeatherMarkerByLocation(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));


            }
        });
    }

    public void addWeatherMarkerBySearch(String searchText) {
        if (googleMap == null) {
            reSyncMap();
            return;
        }
        showLoadingDialog();
        Observable<Weather> call = weatherApiService.getWeatherByCity(searchText);
        subscription = call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                        Toast.makeText(getApplicationContext(), "on complete", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        addMarker(weather);
                    }
                });
    }

    private void addWeatherMarkerByLocation(LatLng latLng) {
        if (googleMap == null) {
            reSyncMap();
            return;
        }
        showLoadingDialog();
        Observable<Weather> call = weatherApiService.getWeatherByLatLon(latLng.latitude, latLng.longitude);
        subscription = call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                        Toast.makeText(getApplicationContext(), "on complete", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        addMarker(weather);
                    }
                });
    }

    private void addMarker(Weather weather) {
        if (googleMap == null) {
            reSyncMap();
            return;
        }

        //store last location so if app resumes, it can find it's previous location
        //use float type so can store in sharedpreferences
        lastLat = new Float(weather.getCoord().getLat());
        lastLng = new Float(weather.getCoord().getLon());

        //get location from api to confirm correct result
        final LatLng latLng = new LatLng(weather.getCoord().getLat(), weather.getCoord().getLon());
        Weather_ currentWeather = weather.getWeather().get(0);

        //return tempurature in celsuis
        final MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(weather.getName())
                .snippet(currentWeather.getMain() + ", " + currentWeather.getDescription() + ", " + "temp :" + weather.getMain().getTempC() + " \u2103")
                ;

        final Marker marker = googleMap.addMarker(markerOptions);
        // Zoom and move camera to queried location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_MAP_ZOOM_LEVEL));
        googleMap.setOnInfoWindowClickListener(this);
        marker.showInfoWindow();
        markerReference.put(marker.getId(), weather);   //add weather and marker reference, so can be used later either to redraw whole marker or on click for detail page

        //over network need to load image async
        imageLoader.loadImage(currentWeather.getIconUrl(), new ImageSize(300, 300), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(loadedImage));
                markerIconReference.put(marker.getId(), loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        //i am probably looking at the map page now, close all search if search bar is still open
        searchView.setQuery("", false);
        searchView.setIconified(true);
    }

    public void showLoadingDialog() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading ....");
            progressDialog.setMessage("Searching weather location");
        }
        progressDialog.show();
    }

    public void dismissLoadingDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //go to detailed weather page
        WeatherDetailActivity weatherDetailActivity = new WeatherDetailActivity();
        if (!markerReference.containsKey(marker.getId())) {
            Toast.makeText(this, "marker does not exist ? try query again", Toast.LENGTH_SHORT).show();
        }
        Weather weather = markerReference.get(marker.getId());
        Bitmap weatherIcon = markerIconReference.get(marker.getId());
        Bundle bundle = new Bundle();
        bundle.putParcelable(WEATHER_DETAIL_KEY, Parcels.wrap(weather));
        bundle.putParcelable(WEATHER_DETAIL_BITMAP_KEY, weatherIcon);
        Intent intent = new Intent(this, WeatherDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
