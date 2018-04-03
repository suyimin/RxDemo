package com.xdroid.rxjava.service;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String API = "https://api.github.com";

    protected RetrofitService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private volatile static RetrofitService instance = null;

    private Retrofit retrofit;

    public static RetrofitService getInstance() {
        if (instance == null) {
            synchronized (RetrofitService.class) {
                if (instance == null) {
                    instance = new RetrofitService();
                }
            }
        }
        return instance;
    }

    public <T> T createService(Class<T> clz){
        return retrofit.create(clz);
    }

}
