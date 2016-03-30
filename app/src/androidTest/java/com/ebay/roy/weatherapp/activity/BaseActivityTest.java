package com.ebay.roy.weatherapp.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.ebay.roy.weatherapp.WeatherApplication;
import com.ebay.roy.weatherapp.activity.fragment.BaseFragment;
import com.ebay.roy.weatherapp.di.component.MockApplicationComponent;
import com.ebay.roy.weatherapp.service.WeatherApiService;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;

/**
 * Created by hungr on 30/03/16.
 */
public abstract class BaseActivityTest {
    protected ImageLoader imageLoader;
    protected WeatherApiService weatherApiService;

    protected MockApplicationComponent component;

    @Rule
    public ActivityTestRule<MainActivityMock> activityRule = new ActivityTestRule<MainActivityMock>(
            MainActivityMock.class,
            true,     // initialTouchMode
            false);   // launchActivity. False so we can customize the intent per test method

    @Before
    public void setUp() {

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        WeatherApplication app
                = (WeatherApplication) instrumentation.getTargetContext().getApplicationContext();

        activityRule.launchActivity(new Intent());

        //inject dagger in application
        component = (MockApplicationComponent) app.getApplicationComponent();
        component.injectActivity(activityRule.getActivity());
        imageLoader = component.provideImageLoaderService();
        weatherApiService = component.provideWeatherService();


        //set up mock for image view
        doNothing().when(imageLoader).displayImage(anyString(), any(ImageView.class));

    }


    public void injectMockActivityService(Fragment fragment) {
        //inject dagger in activity
        activityRule.getActivity().getComponent().inject((BaseFragment)fragment);
    }

    @After
    public void tearDown() {

    }


}
