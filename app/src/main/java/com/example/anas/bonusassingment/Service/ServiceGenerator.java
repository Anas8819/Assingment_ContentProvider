package com.example.anas.bonusassingment.Service;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ServiceGenerator {

    private static final String API_BASE_URL = "http://192.168.10.2:8000/api/";

    private static Retrofit retrofit;

    private static Gson mGson = new GsonBuilder()
            .setLenient()
            //.setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
            .create();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(mGson));


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    public static <S> S createService(Class<S> serviceClass) {

        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        retrofit = builder.client(httpClient.build()).build();

        return retrofit.create(serviceClass);
    }

    /**
     * or Error Handing when non-OK response is received from Server
     */
    @NonNull
    public static Retrofit retrofit() {
        OkHttpClient client = httpClient.build();
        ;
        return builder.client(client).build();
    }
}