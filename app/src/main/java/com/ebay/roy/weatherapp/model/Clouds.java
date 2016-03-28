
package com.ebay.roy.weatherapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel(Parcel.Serialization.BEAN)
public class Clouds {

    private Integer all;

    /**
     * 
     * @return
     *     The all
     */
    public Integer getAll() {
        return all;
    }

    /**
     * 
     * @param all
     *     The all
     */
    public void setAll(Integer all) {
        this.all = all;
    }

}
