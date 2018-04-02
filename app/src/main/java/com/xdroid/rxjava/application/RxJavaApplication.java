package com.xdroid.rxjava.application;

import android.app.Application;


/**
 * 类描述:Application
 * 创建人:launcher.myemail@gmail.com
 * 创建时间:15-12-16 上午11:00
 * 修改人:
 * 修改时间:
 * 修改备注:
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
