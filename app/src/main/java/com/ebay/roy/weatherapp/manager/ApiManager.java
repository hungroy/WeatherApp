package com.ebay.roy.weatherapp.manager;

import android.content.Context;


import com.ebay.roy.weatherapp.R;
import com.ebay.roy.weatherapp.service.WeatherApiService;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import okhttp3.Request;

import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hungr on 24/03/16.
 */
public class ApiManager {

    /**
     * this function initialised the weather service and add api key as a prefix query by intercepting the query using okhttp
     * @param apiUrl
     * @param apiKey
     */
    public WeatherApiService getWeatherService(final String apiUrl, final String apiKey) {
        //add api key as a query suffix by intercepting value
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Interceptor.Chain chain) throws IOException {
                                Request request = chain.request();
                                HttpUrl url = request.url().newBuilder().addQueryParameter("apikey", apiKey).build();
                                request = request.newBuilder().url(url).build();
                                return chain.proceed(request);
                            }
                        })
                .build();



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WeatherApiService.class);
    }


}
