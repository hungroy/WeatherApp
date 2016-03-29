package com.ebay.roy.weatherapp.manager;

import android.app.Activity;
import android.content.SharedPreferences;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by Roy on 3/29/2016.
 */
public class SearchManagerTest {

    SearchManager searchManager;
    Activity activity;

    @Before
    public void setUp() {
        constructMock();
        searchManager = new SearchManager(activity);
    }

    private void constructMock() {
        //mock activity and sharedpreferences
        activity = Mockito.mock(Activity.class);
        SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        when(activity.getPreferences(anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("[\"brisbane\",\"sydney\",\"portland\"]");
        when(editor.putString(anyString(), anyString())).thenReturn(null);
        when(editor.commit()).thenReturn(true);

    }

    @Test
    public void testCanGetSearchHistory() {
        List<String> historyList = searchManager.getSearchHistory();
        assertEquals(historyList.size(), 3);
        assertEquals(historyList.get(1), "sydney");
    }

    @Test
    public void testCanAddSearchHistory() {
        searchManager.addSearchHistory("Russia");
        assertEquals(searchManager.getSearchHistory().size(), 4);
        assertEquals(searchManager.getSearchHistory().get(0) , "Russia");
    }

    @Test
    public void testMoveSearchTextToTop() {
        searchManager.moveSearchTextToTop("sydney");
        assertEquals(searchManager.getSearchHistory().size(), 3);
        assertEquals(searchManager.getSearchHistory().get(0), "sydney");
    }

    @Test
    public void testMoveSearchTextToTopWithNoTextInHistory() {
        searchManager.moveSearchTextToTop("some search that does not exist");
        assertEquals(searchManager.getSearchHistory().size(), 3);
        assertEquals(searchManager.getSearchHistory().get(0), "brisbane");
    }

    @Test
    public void testClearSearchHistory() {
        searchManager.clearSearchHistory();
        assertEquals(searchManager.getSearchHistory().size(), 0);
    }
}
