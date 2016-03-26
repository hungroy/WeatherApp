package com.ebay.roy.weatherapp.view.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ebay.roy.weatherapp.R;

/**
 * Created by Roy on 3/26/2016.
 */
public class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
    protected TextView displayText;

    public SearchHistoryViewHolder(View itemView) {
        super(itemView);
        this.displayText = (TextView) itemView.findViewById(R.id.displayText);
    }

    public TextView getDisplayText() {
        return displayText;
    }


}
