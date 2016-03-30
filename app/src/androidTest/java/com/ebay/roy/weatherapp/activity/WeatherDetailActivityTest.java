package com.ebay.roy.weatherapp.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.widget.FrameLayout;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.model.Weather;
import com.ebay.roy.weatherapp.view.presenter.map.MapViewPresenter;
import com.google.gson.Gson;

import org.junit.Test;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by hungr on 30/03/16.
 */
public class WeatherDetailActivityTest extends BaseActivityTest {
    @Override
    public void setUp() {
        super.setUp();

        //parse raw weather and convert to weather object
        InputStream stream = activityRule.getActivity().getResources()
                .openRawResource(R.raw.raw_openweather_response_test);
        Reader reader = new BufferedReader(new InputStreamReader(stream), 8092);
        Gson gson = new Gson();
        Weather weather = gson.fromJson(reader, Weather.class);
        Bitmap icon = BitmapFactory.decodeResource(activityRule.getActivity().getResources(), R.drawable.places_ic_search); //populate with some random image


        Bundle bundle = new Bundle();
        bundle.putParcelable(MapViewPresenter.WEATHER_DETAIL_KEY, Parcels.wrap(weather));
        bundle.putParcelable(MapViewPresenter.WEATHER_DETAIL_BITMAP_KEY, icon);


        activityRule.getActivity().goToDetailedWeatherView(bundle);

    }

    @Test
    public void testCanSeeWeatherDetail() {
        onView(withId(R.id.Region)).check(matches(withText("Sydney Test Data AU")));
        onView(withId(R.id.description)).check(matches(withText("Clear, clear sky")));
        onView(withId(R.id.imageIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.tempurature)).check(matches(withText("23.0")));
        onView(withId(R.id.tempurature)).check(matches(withText("23.0")));
        onView(withId(R.id.pressureValue)).check(matches(withText("1014.0")));
        onView(withId(R.id.humidityValue)).check(matches(withText("47.0")));
        onView(withId(R.id.tempMinValue)).check(matches(withText("23.0 C")));
        onView(withId(R.id.tempMaxValue)).check(matches(withText("23.0 C")));
        onView(withId(R.id.windSpeedValue)).check(matches(withText("4.6 km/h")));
        onView(withId(R.id.windArrow)).check(matches(isDisplayed()));
    }

    @Test
    public void testCanGoBackToMainActivity() {
        Espresso.pressBack();
        onView(withId(R.id.gpsBtn)).check(matches(isDisplayed()));
        //check if google map is rendered by checking if element is injected into the container
        onView(allOf(is(instanceOf(FrameLayout.class)), withParent(withId(R.id.containerBody))))
                .check(matches(isDisplayed()));

    }



}

