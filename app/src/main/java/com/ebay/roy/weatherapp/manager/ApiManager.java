package com.ebay.roy.weatherapp.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by hungr on 24/03/16.
 */
public class ApiManager {

    public ApiService init(final Context context, String fapiUrl) {
        //register deserializer
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Recipe.class, new RecipeDeserializer())
                .registerTypeAdapter(RecipeListResponse.class, new RecipeListResponseDeserializer())
                .registerTypeAdapter(CollectionListResponse.class, new CollectionListResponseDeserializer())
                .create();

        //setup cache
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        Cache cache = null;
        try {
            cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        } catch (Exception e) {
            Log.e(LOG_PREFIX, "OKHTTP, Could not create http cache", e);
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(new StethoInterceptor());    //link http with debuggers

        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        if (cache != null) {
            okHttpClient.setCache(cache);
        }


        String credentials = context.getResources().getString(R.string.fapi_api_user_name) + ":" + context.getResources().getString(R.string.fapi_api_user_password);
        final String encodedCredentials = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        //get version
        String versionName = "1.0";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final String finalVersionName = versionName;

        //setup restAdapter ,
        RestAdapter.Builder restBuilder = new RestAdapter.Builder()
                .setEndpoint(fapiUrl)
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(
                        new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                request.addHeader("Accept", "application/json;versions=1");
                                //context may not be avaiable here so instead use global application context to check if online
                                if (ConnectivityService.isOnline(context)) {
                                    int maxAge = 60; // read from cache for 1 minute
                                    request.addHeader("Cache-Control", "public, max-age=" + maxAge);
                                    request.addHeader("Authorization", encodedCredentials);
                                    request.addHeader("User-Agent", "Tasteapp/" + finalVersionName +"/Android " + "(" + Build.MODEL +")");

                                } else {
                                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                                    request.addHeader("Cache-Control",
                                            "public, only-if-cached, max-stale=" + maxStale);
                                }
                            }
                        }
                );

        String isMockClient = context.getString(R.string.fapi_mock_enabled);

        if (isMockClient.equals("true")) {
            restBuilder.setClient(new MockClient(context, "test"));
        }

        RestAdapter restAdapter = restBuilder.build();

        if (BuildConfig.DEBUG) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.BASIC);
        }

        ApiService service = restAdapter.create(ApiService.class);
        return service;
    }
}
