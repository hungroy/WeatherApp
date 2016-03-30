package com.ebay.roy.weatherapp.activity;

import android.os.SystemClock;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.activity.fragment.SearchDisplayFragment;
import com.ebay.roy.weatherapp.helper.TestHelper;
import com.ebay.roy.weatherapp.manager.SearchManager;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

/**
 * Created by hungr on 30/03/16.
 */
public class SearchFragmentTest extends BaseActivityTest {
    @Override
    public void setUp() {
        super.setUp();
    }

    private void mockHistory() {
        SearchDisplayFragment searchDisplayFragment = new SearchDisplayFragment();
        injectMockActivityService(searchDisplayFragment);
        SearchManager searchManager = activityRule.getActivity().getComponent().searchManager().get();
        when(searchManager.getSearchHistory())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(
                                new String[]
                                        {
                                                "Sydney",
                                                "Portland",
                                                "Taipei",
                                                "USA",
                                                "Russia",
                                                "China",
                                                "Brisbane",
                                                "Some place on earth"


                                        }
                        )
                ));


    }

    @Test
    public void testCanGoToSearch() {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_src_text))
                .check(matches(isDisplayed())).
                perform(typeText("Sydney"), pressKey(66));  //type sydney and press enter
        onView(withId(R.id.action_search)).check(matches(isDisplayed()));   //after pressing enter should see search icon again
    }

    @Test
    public void testCanSeeSearchHistory() {
        mockHistory();
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.searchHistoryRecyclerView))
                .check(matches(hasDescendant(withText("Some place on earth"))));
        onView(withId(R.id.searchHistoryRecyclerView))
                .check(matches(
                        TestHelper.hasChildren(Matchers.is(8))
                ));
    }

    @Test
    public void testDialogIsDisplayedOnClear() {
        mockHistory();
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.clearHistoryBtn)).check(matches(isDisplayed())).perform(click());
        onView(withText("Are you sure you want to clear all search history ?")).check(matches(isDisplayed()));
    }


}
