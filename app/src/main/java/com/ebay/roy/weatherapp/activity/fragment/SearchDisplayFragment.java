package com.ebay.roy.weatherapp.activity.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
    @Bind(R.id.clearHistoryBtn) Button clearHistoryBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        searchManager = getComponent().searchManager().get();
        displaySearchHistory();
        setOnClearSearchBtnListener();
        return rootView;
    }

    private void displaySearchHistory() {
        final List<String> searchList = searchManager.getSearchHistory();
        searchHistoryAdapter = new SearchHistoryAdapter(getActivity(), searchList);
        searchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchHistoryRecyclerView.setAdapter(searchHistoryAdapter);
        searchHistoryAdapter.setOnViewItemClickListener(new SearchHistoryAdapter.OnViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String clickedText = searchList.get(position);
                searchManager.moveSearchTextToTop(clickedText);
                addMapMarkerBySearchText(clickedText);
                closeSelf();
            }
        });
    }

    private void addMapMarkerBySearchText(String searchText) {
        getActivity().setTitle(searchText);
        ((MainActivity) getActivity()).addWeatherMarkerBySearch(searchText);
    }

    private void setOnClearSearchBtnListener() {
        //create confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Clear History");
        builder.setMessage("Are you sure you want to clear all search history ?");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                searchManager.clearSearchHistory();
                searchHistoryAdapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = builder.create();

        //add btn listener
        clearHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });
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
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

}
