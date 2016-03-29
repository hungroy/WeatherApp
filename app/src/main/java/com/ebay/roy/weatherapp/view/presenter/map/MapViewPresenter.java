package com.ebay.roy.weatherapp.view.presenter.map;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.activity.MainActivity;
import com.ebay.roy.weatherapp.activity.WeatherDetailActivity;
import com.ebay.roy.weatherapp.model.Weather;
import com.ebay.roy.weatherapp.model.Weather_;
import com.ebay.roy.weatherapp.view.presenter.BasePresenter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hungr on 29/03/16.
 */
public class MapViewPresenter extends BasePresenter implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    IMapViewPresenter mapViewPresenter;

    public static final String LAST_LOCATION_LAT = "LastLocationLat";
    public static final String LAST_LOCATION_LNG = "LastLocationLng";
    public static final String WEATHER_DETAIL_KEY = "WeatherDetailKey";
    public static final String WEATHER_DETAIL_BITMAP_KEY = "WeatherDetailBitmapKey";
    public static final Float MIN_LOCATION_LAT_LNG_VALUE = -999f;
    public static final Integer DEFAULT_MAP_ZOOM_LEVEL = 10;
    public static final Integer PERMISSIONS_REQUEST_GPS_CODE = 100;

    Float lastLat = MIN_LOCATION_LAT_LNG_VALUE;  //set a negative value that exceeds the max negative lat and lng to check for null
    Float lastLng = MIN_LOCATION_LAT_LNG_VALUE;

    HashMap<String, Weather> markerReference; //reference to all the marker currently created
    HashMap<String, Bitmap> markerIconReference; //reference to all bitmap

    MapFragment mapFragment;
    GoogleMap googleMap;

    GoogleApiClient googleApiClient;

    public MapViewPresenter(IMapViewPresenter mapViewPresenter) {
        this.mapViewPresenter = mapViewPresenter;
        //initialise googleapiclient for locatoin service
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void render() {
        markerReference = new HashMap<>();
        markerIconReference = new HashMap<>();
        syncMap();
    }

    @Override
    public Activity getActivity() {
        if (mapViewPresenter == null) {
            return null;
        }
        return ((MainActivity) mapViewPresenter);
    }

    public void saveLastLocation() {
        //store the last location before stop, we could store whole weather object this would avoid another api call, but the user probably wants the latest weather status, so we query api just by storing lnt lat
        SharedPreferences.Editor editor = mapViewPresenter.getSharedPreference().edit();
        editor.putFloat(LAST_LOCATION_LAT, lastLat);
        editor.putFloat(LAST_LOCATION_LNG, lastLng);
        editor.commit();
    }

    public void restoreLastLocation() {
        Float lat = mapViewPresenter.getSharedPreference().getFloat(LAST_LOCATION_LAT, MIN_LOCATION_LAT_LNG_VALUE);
        Float lng = mapViewPresenter.getSharedPreference().getFloat(LAST_LOCATION_LNG, MIN_LOCATION_LAT_LNG_VALUE);
        if (lat > MIN_LOCATION_LAT_LNG_VALUE && lng > MIN_LOCATION_LAT_LNG_VALUE) {
            lastLat = lat;
            lastLng = lng;
        }

        //remove these value, so when restate won't have the previous value
        mapViewPresenter.getSharedPreference()
                .edit()
                .remove(LAST_LOCATION_LAT)
                .remove(LAST_LOCATION_LNG)
                .commit();
    }

    private void syncMap() {
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            FragmentTransaction fragmentTransaction = mapViewPresenter.getActivityFragmentManager().beginTransaction();
            fragmentTransaction.add(mapViewPresenter.getMapContainerId(), mapFragment);
            fragmentTransaction.commit();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getActivity(), "Map is now ready", Toast.LENGTH_SHORT).show();
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false); //hide control buttons from google map
        addGPSBtnActionOnMap();

        //if have previous location restore previous location
        if (lastLng > MIN_LOCATION_LAT_LNG_VALUE && lastLat > MIN_LOCATION_LAT_LNG_VALUE) {
            mapViewPresenter.addWeatherMarkerByLocation(new LatLng(lastLat, lastLng));
        }
    }


    private void addGPSBtnActionOnMap() {
        if (googleMap == null) {
            syncMap();
            return;
        }

        if (mapViewPresenter.getGPSBtnView() == null) {
            //do nothing no gps button defined
            return;
        }

        mapViewPresenter.getGPSBtnView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //connect google service to get location
                if (!googleApiClient.isConnected()) {
                    googleApiClient.connect();
                } else {
                    //already connected, request location
                    addCurrentLocationToMarker();
                }


            }
        });
    }

    private void addCurrentLocationToMarker() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_GPS_CODE);

            Toast.makeText(getActivity(), "Please allow permission to access GPS in order to determine your location", Toast.LENGTH_SHORT).show();

            return;
        }
        googleMap.setMyLocationEnabled(true);
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (myLocation == null) {
            Toast.makeText(getActivity(), "Could not determine your location, your GPS may be turned off, please turn it on and try again", Toast.LENGTH_SHORT).show();
        }
        mapViewPresenter.addWeatherMarkerByLocation(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
    }

    @Override
    public void onConnected(Bundle bundle) {
        //on googlemap service connected
        addCurrentLocationToMarker();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Could not determine your location, your GPS may be turned off, please turn it on and try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //go to detailed weather page
        if (!markerReference.containsKey(marker.getId())) {
            Toast.makeText(getActivity(), "marker does not exist ? try query again", Toast.LENGTH_SHORT).show();
        }
        Weather weather = markerReference.get(marker.getId());
        Bitmap weatherIcon = markerIconReference.get(marker.getId());
        Bundle bundle = new Bundle();
        bundle.putParcelable(WEATHER_DETAIL_KEY, Parcels.wrap(weather));
        bundle.putParcelable(WEATHER_DETAIL_BITMAP_KEY, weatherIcon);
        mapViewPresenter.goToDetailedWeatherView(bundle);
    }


    public void addMarker(Weather weather) {
        if (googleMap == null) {
            syncMap();
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
        mapViewPresenter.getImageLoader().loadImage(currentWeather.getIconUrl(), new ImageSize(300, 300), new ImageLoadingListener() {
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


    }

    @Override
    public void destroy() {
        markerReference.clear();
        markerReference = null;
        markerIconReference.clear();
        markerIconReference = null;
        mapFragment = null;
        googleMap.clear();
        googleMap = null;
        mapViewPresenter = null;
        googleApiClient.disconnect();
        googleApiClient = null;
    }
}
