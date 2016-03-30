package com.ebay.roy.weatherapp.interceptors;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by hungr on 30/03/16.
 */
public class FileInterceptors implements Interceptor {

    Context context;
    int rawFileId;

    public FileInterceptors(Context context, int rawFileId) {
        this.context = context;
        this.rawFileId = rawFileId;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = null;
        //because it's returning the same type, we don't need to parse query strings, simply just return a mock rawfile
        String responseString = readRawTextFile(context, rawFileId);


        response = new Response.Builder()
                .code(200)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                .addHeader("content-type", "application/json")
                .build();

        return response;
    }

    private String readRawTextFile(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}
