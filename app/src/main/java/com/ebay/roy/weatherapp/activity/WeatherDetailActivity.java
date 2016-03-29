package com.ebay.roy.weatherapp.activity;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.databinding.ActivityWeatherDetailBinding;
import com.ebay.roy.weatherapp.model.Weather;
import com.ebay.roy.weatherapp.view.presenter.map.MapViewPresenter;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.parceler.Parcels;

import butterknife.Bind;

/**
 * Created by Roy on 3/28/2016.
 */
public class WeatherDetailActivity extends BaseActivity {

    Weather weather;
    Bitmap weatherIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityWeatherDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_weather_detail);
        //using data binding, need to rebind toolbar to with support action bar again, and binding doesn't seem to like layout, it treats toolbar as relative layout if it's an included tempate
        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //ActivityWeatherDetailBinding binding = ActivityWeatherDetailBinding.inflate(getLayoutInflater());
        weather = new Weather();
        //get weather variable
        Bundle extra = getIntent().getExtras();
        if (extra != null && extra.getParcelable(MapViewPresenter.WEATHER_DETAIL_KEY) != null) {
            weather = Parcels.unwrap(extra.getParcelable(MapViewPresenter.WEATHER_DETAIL_KEY));
            weatherIcon = extra.getParcelable(MapViewPresenter.WEATHER_DETAIL_BITMAP_KEY);
        }

        binding.setWeather(weather);
        setTitle(weather.getName());
        if (weatherIcon != null) {
            binding.imageIcon.setImageBitmap(weatherIcon);
        }

        //@Todo
        //don't know how to do rotate on binding object.. do it here for now
        if (weather.getWind().getDeg() != null && weather.getWind().getDeg() > 0) {
            binding.windArrow.setRotation(new Float(weather.getWind().getDeg()));
        }

    }


    @Override
    public int layoutTemplate() {
        return R.layout.activity_weather_detail;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
