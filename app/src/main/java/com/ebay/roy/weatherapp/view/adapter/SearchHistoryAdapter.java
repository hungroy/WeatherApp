package com.ebay.roy.weatherapp.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.view.viewholder.SearchHistoryViewHolder;

import java.util.List;

/**
 * Created by Roy on 3/26/2016.
 */
public class SearchHistoryAdapter extends RecyclerView.Adapter {

    private List<String> textList;
    private Context context;
    OnViewItemClickListener onViewItemClickListener;

    public SearchHistoryAdapter(Context context, List<String> textList) {
        this.context = context;
        this.textList = textList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_search_history, null);
        SearchHistoryViewHolder searchHistoryViewHolder = new SearchHistoryViewHolder(itemView);
        //set on view click listener if assigned
        if (onViewItemClickListener != null) {
            searchHistoryViewHolder.setOnViewItemClickListener(onViewItemClickListener);
        }
        return searchHistoryViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SearchHistoryViewHolder)holder).getDisplayText().setText(textList.get(position));
    }

    @Override
    public int getItemCount() {
        return textList.size();
    }

    public void setOnViewItemClickListener(final OnViewItemClickListener onViewItemClickListener) {
        this.onViewItemClickListener = onViewItemClickListener;
    }

    public interface OnViewItemClickListener {
        public void onItemClick(View view, int position);
    }
}

