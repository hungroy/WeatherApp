package com.ebay.roy.weatherapp.activity;

import android.os.SystemClock;
import android.widget.FrameLayout;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.helper.TestHelper;
import com.ebay.roy.weatherapp.model.Weather;
import com.ebay.roy.weatherapp.service.WeatherApiService;
import com.google.android.gms.maps.MapFragment;
import com.google.gson.Gson;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.doAnswer;

/**
 * Created by hungr on 30/03/16.
 */
public class MainActivityTest extends BaseActivityTest {
    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testInitialPage() {
        onView(withId(R.id.toolbar))
                .check(matches(isDisplayed()));

        //check can see search icon
        onView(withId(R.id.action_search))
                .check(matches(isDisplayed()));

        //check can see gps button
        onView(withId(R.id.gpsBtn))
                .check(matches(isDisplayed()));

        onView(withId(R.id.containerBody))
                .check(matches(isDisplayed()));

        //check if google map is rendered by checking if element is injected into the container
        onView(allOf(is(instanceOf(FrameLayout.class)), withParent(withId(R.id.containerBody))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkIfCanClickGpsButton() {
        //click on gps button and check if exception is thrown
        onView(withId(R.id.gpsBtn)).perform(click());

    }
}
