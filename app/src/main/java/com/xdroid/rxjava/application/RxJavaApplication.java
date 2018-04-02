package com.xdroid.rxjava.application;

import android.app.Application;


/**
 * 类描述:Application
 */
public class RxJavaApplication extends Application {

    private static RxJavaApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static RxJavaApplication getApplication(){
        return application;
    }
}
