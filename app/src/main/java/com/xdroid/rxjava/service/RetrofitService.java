package com.xdroid.rxjava.service;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 类描述:RetrofitService
 * 创建人:launcher.myemail@gmail.com
 * 创建时间:16-1-24
 * 备注:{@link https://github.com/basil2style/Retrofit-Android-Basic} Thanks for  ,Her code is very good ! I made reference to his code,It saves me a lot of time!
 * 修改人:
 * 修改时间:
 * 修改备注:
 */
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
