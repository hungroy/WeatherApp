package com.ebay.roy.weatherapp.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Roy on 3/26/2016.
 */
public class SearchManager {
    Activity activity;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> searchHistory;
    Gson gson;
    public static final String SEARCH_HISTORY_LIST_KEY = "SEARCH_HISTORY_LIST_KEY";

    public SearchManager(Activity activity) {
        this.activity = activity;
        gson = new Gson();
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);  //using sharedpreference as a persistent storage, can be replaced with some other storage
        editor = sharedPreferences.edit();
        searchHistory = gson.fromJson(sharedPreferences.getString(SEARCH_HISTORY_LIST_KEY, "[]"), ArrayList.class);
    }

    public List<String> getSearchHistory() {
        return searchHistory;
    }

    public void addSearchHistory(String searchText) {
        //store value on top, so most recent always displays first
        searchHistory.add(0, searchText);
        editor.putString(SEARCH_HISTORY_LIST_KEY, gson.toJson(searchHistory));
        editor.commit();
    }

    public void clearSearchHistory() {
        searchHistory.clear();
        editor.putString(SEARCH_HISTORY_LIST_KEY, gson.toJson(searchHistory));
        editor.commit();

    }

}
