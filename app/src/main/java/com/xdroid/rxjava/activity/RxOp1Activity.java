package com.xdroid.rxjava.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.xdroid.rxjava.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class RxOp1Activity extends AppCompatActivity {
    private static String TAG = "RxOp1Activity";

    private EditText mEditText;
    private Button mButton;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        mEditText = (EditText) findViewById(R.id.editText);
        mButton = (Button) findViewById(R.id.button);
        mImageView = (ImageView) findViewById(R.id.image);

        create();

        from();

        just();

        empty();

        never();

        error();

        range();

        timer();

        interval();

        repeat();

        defer();

    }


    private void create() {
        Log.e(TAG, "Observable.create 从零开始创建一个 Observable.");
        //被观察者
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //可以多次调用subscriber.onNext("大家好")发射数据
                subscriber.onNext("大家好");
                subscriber.onNext("我开始学习RxJava");
                subscriber.onCompleted();

            }
        });

        //订阅者
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext：" + s);
            }
        };

        observable.subscribe(subscriber);
    }

    private void from() {
        Log.e(TAG, "Observable.from 将其它种类的对象和数据类型转换为Observable.");
        Integer[] integers = {1, 2, 3, 4};
        Observable<Integer> observable = Observable.from(integers);
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onNext(Integer i) {
                Log.e(TAG, "onNext：" + i);
            }
        };
        observable.subscribe(subscriber);
    }

    private void just() {
        Log.e(TAG, "Observable.just 将单个数据转换为发射那个数据的Observable.");
        Observable.just(0, "one", 6, "two", 8, "three")
                .subscribe(new Subscriber<Serializable>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(Serializable serializable) {
                        Log.e(TAG, "onNext: " + serializable.toString());
                    }
                });
    }

    private void empty() {
        Log.e(TAG, "Empty:创建一个不发射任何数据但是正常终止的Observable，此时会回调onCompleted()");
        Observable.<String>empty().subscribe(new Observer<String>() {
            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext:" + s);
            }

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError:" + e.getMessage());
            }
        });
    }

    private void never() {
        Log.e(TAG, "Never:创建一个不发射数据也不终止的Observable");
        Observable.<String>never().subscribe(new Observer<String>() {
            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext:" + s);
            }

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError:" + e.getMessage());
            }
        });
    }

    private void error() {
        Log.e(TAG, "Error:创建一个不发射数据以一个错误终止的Observable");
        Observable.<String>error(new Throwable("error")).subscribe(new Observer<String>() {
            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext:" + s);
            }

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError:" + e.getMessage());
            }
        });
    }

    private void range() {
        Log.e(TAG, "Range:创建特定整数序列的Observable，它接受两个参数，一个是范围的起始值，一个是范围的数据的数目。如果你将第二个参数设为0，将导致Observable不发射任何数据（如果设置为负数，会抛异常）");
        Observable.range(1, 4)
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext: " + integer);
                    }
                });
    }

    private void timer() {
        Log.e(TAG, "Timer:创建一个在给定的时间段之后返回一个特殊值的Observable。它在延迟一段给定的时间后发射一个简单的数字0 。");
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Long>() {

                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "Timer onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Timer onError: ");
                    }

                    @Override
                    public void onNext(Long integer) {
                        Log.e(TAG, "Timer onNext: " + integer);
                    }
                });
    }

    private void interval() {
        Log.e(TAG, "Interval:该操作符按固定的时间间隔发射一个无限递增的整数序列。");
        Subscription subscription = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mEditText.append(" " + aLong + "、");
                    }
                });
    }

    private void repeat() {
        Log.e(TAG, "Repeat:重复的发射某个数据序列，并且可以自己设置重复的次数。当接收到onComplete()会触发重订阅再次重复发射数据,当重复发射数据次数到达后执行onCompleted()。");
        String[] strs = {"也许当初忙着微笑和哭泣", "忙着追逐天空中的流星"};
        Observable.from(strs).repeat(2).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }
        });
    }

    private void defer() {
        Log.e(TAG, "Defer:直到有观察者订阅时才创建Observable，并且为每个观察者创建一个新的Observable，该操作符能保证订阅执行时数据源是最新的数据。");
        Observable<Bitmap> deferObservable = Observable.defer(new Func0<Observable<Bitmap>>() {
            @Override
            public Observable<Bitmap> call() {
                return Observable.just(getBitmap());
            }
        });

        deferObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Bitmap result) {
                        mImageView.setImageBitmap(result);
                    }
                });
    }

    @Nullable
    private Bitmap getBitmap() {
        Bitmap bitmap = null;
        InputStream stream;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection("https://droidcon.in/_themes/droidcon2015/img/logo.png");
            bitmap = BitmapFactory.
                    decodeStream(stream, null, bmOptions);
            stream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }

    private InputStream getHttpConnection(String urlString)
            throws IOException {
        InputStream stream = null;
        java.net.URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }


}
