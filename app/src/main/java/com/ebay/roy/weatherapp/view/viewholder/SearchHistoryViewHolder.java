package com.ebay.roy.weatherapp.view.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.view.adapter.SearchHistoryAdapter;

/**
 * Created by Roy on 3/26/2016.
 */
public class SearchHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected TextView displayText;
    protected SearchHistoryAdapter.OnViewItemClickListener onViewItemClickListener;

    public SearchHistoryViewHolder(View itemView) {
        super(itemView);
        this.displayText = (TextView) itemView.findViewById(R.id.displayText);
        itemView.setOnClickListener(this);
    }

    public TextView getDisplayText() {
        return displayText;
    }

    /**
     * set view item listener from adapter
     * @param onViewItemClickListener
     */
    public void setOnViewItemClickListener(final SearchHistoryAdapter.OnViewItemClickListener onViewItemClickListener) {
        this.onViewItemClickListener = onViewItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onViewItemClickListener != null) {
            onViewItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
