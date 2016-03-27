
package com.ebay.roy.weatherapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Main {

    private Double temp;
    private Double pressure;
    private Double humidity;
    private Double tempMin;
    private Double tempMax;

    /**
     * 
     * @return
     *     The temp in kelvin
     */
    public Double getTemp() {
        return temp;
    }

    /**
     *
     * @return
     *  The temp in celsius
     */
    public String getTempC() {
        return String.format("%.1f", temp - 274.15);
    }

    /**
     * 
     * @param temp
     *     The temp
     */
    public void setTemp(Double temp) {
        this.temp = temp;
    }

    /**
     * 
     * @return
     *     The pressure
     */
    public Double getPressure() {
        return pressure;
    }

    /**
     * 
     * @param pressure
     *     The pressure
     */
    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    /**
     * 
     * @return
     *     The humidity
     */
    public Double getHumidity() {
        return humidity;
    }

    /**
     * 
     * @param humidity
     *     The humidity
     */
    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    /**
     * 
     * @return
     *     The tempMin
     */
    public Double getTempMin() {
        return tempMin;
    }

    /**
     * 
     * @param tempMin
     *     The temp_min
     */
    public void setTempMin(Double tempMin) {
        this.tempMin = tempMin;
    }

    /**
     * 
     * @return
     *     The tempMax
     */
    public Double getTempMax() {
        return tempMax;
    }

    /**
     * 
     * @param tempMax
     *     The temp_max
     */
    public void setTempMax(Double tempMax) {
        this.tempMax = tempMax;
    }

}
