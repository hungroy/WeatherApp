package com.ebay.roy.weatherapp.activity;

import android.Manifest;
import android.app.FragmentTransaction;
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


import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.State;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    Subscription subscription;
    SearchView searchView;
    MapFragment mapFragment;
    GoogleMap googleMap;
    @Bind(R.id.gpsBtn) FloatingActionButton gpsBtn;

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
        //weatherApiService = getApplicationComponent().provideWeatherService();

        mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.containerBody, mapFragment);
        fragmentTransaction.commit();

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
        addGPSBtnActionOnMap();

    }

    private void addGPSBtnActionOnMap() {
        if (googleMap == null) {
            Toast.makeText(this, "Map not ready yet, please try again later", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Map not ready yet, please try again later", Toast.LENGTH_SHORT).show();
            return;
        }
        Observable<Weather> call = weatherApiService.getWeatherByCity(searchText);
        subscription = call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getApplicationContext(), "on complete", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
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
            Toast.makeText(this, "Map not ready yet, please try again later", Toast.LENGTH_SHORT).show();
            return;
        }
        Observable<Weather> call = weatherApiService.getWeatherByLatLon(latLng.latitude, latLng.longitude);
        subscription = call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getApplicationContext(), "on complete", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
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
            Toast.makeText(this, "Map not ready yet, please try again later", Toast.LENGTH_SHORT).show();
            return;
        }
        //get location from api to confirm correct result
        final LatLng latLng = new LatLng(weather.getCoord().getLat(), weather.getCoord().getLon());
        Weather_ currentWeather = weather.getWeather().get(0);

        final MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(weather.getName())
                .snippet(currentWeather.getMain() + ", " + currentWeather.getDescription() + ", " + "temp :" + weather.getMain().getTemp())
                ;

        final Marker marker = googleMap.addMarker(markerOptions);
        // Zoom and move camera to queried location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        marker.showInfoWindow();

        //over network need to load image async
        String imageUrl = "http://openweathermap.org/img/w/" + currentWeather.getIcon() +".png";
        imageLoader.loadImage(imageUrl, new ImageSize(300, 300), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(loadedImage));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }


}
