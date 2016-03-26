package com.ebay.roy.weatherapp.activity.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.activity.MainActivity;
import com.ebay.roy.weatherapp.manager.SearchManager;
import com.ebay.roy.weatherapp.view.adapter.SearchHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.State;

/**
 * Created by Roy on 3/26/2016.
 */
public class SearchDisplayFragment extends BaseFragment {

    View rootView;
    SearchManager searchManager;
    SearchHistoryAdapter searchHistoryAdapter;
    @Bind(R.id.searchHistoryRecyclerView) RecyclerView searchHistoryRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        searchManager = getComponent().searchManager().get();
        displaySearchHistory();
        return rootView;
    }

    private void displaySearchHistory() {
        List<String> searchList = searchManager.getSearchHistory();
        searchHistoryAdapter = new SearchHistoryAdapter(getActivity(), searchList);
        searchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchHistoryRecyclerView.setAdapter(searchHistoryAdapter);


    }

    public void addSearchViewTextListener(final SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchManager.addSearchHistory(query);
                searchHistoryAdapter.notifyDataSetChanged();    //notify view changed
                getActivity().setTitle(query);
                ((MainActivity)getActivity()).addWeatherMarkerBySearch(query);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                closeSelf();
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                closeSelf();
                return false;
            }
        });
    }

    @Override
    public int layoutTemplate() {
        return R.layout.fragment_search_display;
    }


    //function that closes itself, so can display the underlaying map
    private void closeSelf() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
