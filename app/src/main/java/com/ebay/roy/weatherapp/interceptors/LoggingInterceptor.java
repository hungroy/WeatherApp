package com.ebay.roy.weatherapp.interceptors;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hungr on 30/03/16.
 */
public class LoggingInterceptor implements Interceptor {
    public static final String RETROFIT_TAG = "Retrofit";

    public LoggingInterceptor() {}

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        logInfo(String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        logInfo(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }

    private void logInfo(String message) {
        Log.d(RETROFIT_TAG, message);
    }
}