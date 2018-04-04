package com.xdroid.rxjava.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xdroid.rxjava.R;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.schedulers.Timestamped;

public class RxOp5Activity extends AppCompatActivity {
    private static String TAG = "RxOp5Activity";

    private EditText mEditText;
    private Button mButton;
    private ImageView mImageView;
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        mEditText = (EditText) findViewById(R.id.editText);
        mButton = (Button) findViewById(R.id.button);
        mImageView = (ImageView) findViewById(R.id.image);
        rl = (RelativeLayout) findViewById(R.id.rl_root_layout);

        delay();
        delaySubscription();
        dos();
        subscribeOn();
        timeInterval();
        timestamp();
        timeout();
        toList();
        toMap();
        toMutimap();
        toSortedList();

    }

    private void delay() {
        Log.e(TAG, "Delay 让原始Observable在发射每项数据之前都暂停一段指定的时间。");
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.e(TAG, "call: " + new SimpleDateFormat("yyyy/MM/dd HH:MM:ss").format(new Date()));
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onNext(4);
                subscriber.onCompleted();
            }
        }).delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: " + new SimpleDateFormat("yyyy/MM/dd HH:MM:ss").format(new Date()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + new SimpleDateFormat("yyyy/MM/dd HH:MM:ss").format(new Date()) + e.toString());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        mEditText.append("\n" + new SimpleDateFormat("yyyy/MM/ddHH:MM:ss").format(new Date()) + "  " + integer);
                        Log.e(TAG, "onNext: " + new SimpleDateFormat("yyyy/MM/dd HH:MM:ss").format(new Date()) + integer);
                    }
                });
    }

    private void delaySubscription() {
        Log.e(TAG, "delaySubscription 该操作符也是delay的一种实现，它和dealy的区别是dealy是延迟数据的发送，而此操作符是延迟数据的注册。");
        Observable observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.e(TAG, "call: " + new SimpleDateFormat("yyyy/MM/dd HH:MM:ss").format(new Date()));
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onNext(4);
                subscriber.onCompleted();
            }
        });
        Log.e(TAG, "call11: " + new SimpleDateFormat("yyyy/MM/dd HH:MM:ss").format(new Date()));
        observable.delaySubscription(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: " + new SimpleDateFormat("yyyy/MM/dd HH:MM:ss").format(new Date()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + new SimpleDateFormat("yyyy/MM/dd HH:MM:ss").format(new Date()) + e.toString());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        mEditText.append("\n" + new SimpleDateFormat("yyyy/MM/ddHH:MM:ss").format(new Date()) + "  " + integer);
                        Log.e(TAG, "onNext: " + new SimpleDateFormat("yyyy/MM/dd HH:MM:ss").format(new Date()) + integer);
                    }
                });
    }

    private void dos() {
        Log.e(TAG, "Do系列操作符，给Observable执行周期的关键节点添加回调。当Observable执行到这个阶段的时候，这些回调就会被触发。");
        Observable.just(1, 2, 3)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, "doOnNext: ");
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "doOnError: ");
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Log.e(TAG, "doOnCompleted: ");
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.e(TAG, "doOnSubscribe: ");
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.e(TAG, "doOnUnsubscribe: ");
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        Log.e(TAG, "doOnTerminate: ");
                    }
                })
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        Log.e(TAG, "doAfterTerminate: ");
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted1: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError1: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext1: " + integer);
                    }
                });

        Log.e(TAG, "doOnEach操作符，他接收的是一个Observable参数，相当于doOnNext，doOnError，doOnCompleted综合体。");
        Observable.just(1, 2, 3)
                .doOnEach(new Subscriber<Integer>() {
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
                }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted1: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError1: ");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext1: " + integer);
            }
        });
    }

    private void subscribeOn() {
        Log.e(TAG, "ObserveOn 指定Observable在一个特定的调度器上发送通知给观察者。observeOn可以指定多次，每次指定会在observeOn下一句代码处生效。");
        Log.e(TAG, "SubscribeOn 指定Observable本身在特定的调度器上执行。指定多次则以第一次为准。");

        final StringBuffer stringBuffer = new StringBuffer();
        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                //不能执行耗时操作，及更新ui
                stringBuffer.append("\n" + "开始发送事件" + Thread.currentThread().getName() + "\n");
                Drawable drawable = getResources().getDrawable(R.mipmap.launcher);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        })
                //指定创建Observable在io中
                .subscribeOn(Schedulers.io())
                //由于map中做耗时操作，通过Observable指定发射数据在新的线程
                .observeOn(Schedulers.newThread())
                .map(new Func1<Drawable, ImageView>() {
                    @Override
                    public ImageView call(Drawable drawable) {
                        ImageView imageView = new ImageView(RxOp5Activity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        imageView.setLayoutParams(params);
                        imageView.setImageDrawable(drawable);
                        return imageView;
                    }
                })
                //操作UI，需要指定在主线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ImageView>() {
                    @Override
                    public void call(ImageView imageView) {
                        mEditText.append(stringBuffer.toString() + "接收信息事件" + Thread.currentThread().getName());
                        rl.addView(imageView);
                    }
                });
    }

    private void timeInterval() {
        Log.e(TAG, "TimeInterval 将原始Observable转换为另一个Obserervable，后者发射一个标志替换前者的数据项，这个标志表示前者的两个连续发射物之间流逝的时间长度。");
        Observable.interval(1, TimeUnit.SECONDS)
                .filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong < 5;
                    }
                })
                .timeInterval()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TimeInterval<Long>>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(TimeInterval<Long> longTimeInterval) {
                        Log.e(TAG, "onNext: value:" + longTimeInterval.getValue() + ", getIntervalInMilliseconds:" + longTimeInterval.getIntervalInMilliseconds());
                    }
                });
    }

    private void timestamp() {
        Log.e(TAG, "Timestamp 发射数据每一项包含数据的原始发射时间。");
        Observable.just(1, 2, 3, 4).timestamp().subscribe(new Action1<Timestamped<Integer>>() {
            @Override
            public void call(Timestamped<Integer> integerTimestamped) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                Log.e(TAG, "value: " + integerTimestamped.getValue() + "       time:   " + sdf.format(new Date(integerTimestamped.getTimestampMillis())));
            }
        });
    }

    private void timeout() {
        Log.e(TAG, "Timeout 如果原始Observable过了指定的一段时间没有发射任何数据，Timeout操作符会以一个onError通知终止这个Observable。");
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    subscriber.onNext(1);
                    Thread.sleep(100);
                    subscriber.onNext(2);
                    Thread.sleep(200);
                    subscriber.onNext(3);
                    Thread.sleep(300);
                    subscriber.onNext(4);
                    Thread.sleep(400);
                    subscriber.onNext(5);
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    subscriber.onError(new Throwable("Error"));
                    e.printStackTrace();
                }
            }
        })
                //此timeout方法默认在computation调度器上执行.
                .timeout(250, TimeUnit.MILLISECONDS)
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

        Log.e(TAG, "timeout还有重载方法可以在超时的时候切换到一个我们指定的备用的Observable，而不是发错误通知。它也默认在computation调度器上执行。");
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    subscriber.onNext(1);
                    Thread.sleep(100);
                    subscriber.onNext(2);
                    Thread.sleep(200);
                    subscriber.onNext(3);
                    Thread.sleep(300);
                    subscriber.onNext(4);
                    Thread.sleep(400);
                    subscriber.onNext(5);
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    subscriber.onError(new Throwable("Error"));
                    e.printStackTrace();
                }
            }
        })
                .timeout(250, TimeUnit.MILLISECONDS, Observable.just(10, 11))
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

    private void toList() {
        Log.e(TAG, "toList 让Observable将多项数据组合成一个List，然后调用一次onNext方法传递整个列表，如果原始Observable没有发射任何数据就调用了onCompleted，toList返回的Observable会在调用onCompleted之前发射一个空列表。");
        Observable.just(1, 2, 3, 4, 5).toList().subscribe(new Subscriber<List<Integer>>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(List<Integer> integers) {
                Log.e(TAG, "onNext: " + integers);
            }
        });
    }

    private void toMap() {
        Log.e(TAG, "ToMap 收集原始Observable发射的所有数据项到一个Map（默认是HashMap）然后发射这个Map。");
        Observable.just(1, 2, 3, 4)
                .toMap(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        //生成map的key值
                        return "key" + integer;
                    }
                }).subscribe(new Subscriber<Map<String, Integer>>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(Map<String, Integer> integerIntegerMap) {
                Log.e(TAG, "onNext: " + integerIntegerMap.toString());

            }
        });

        Log.e(TAG, "该操作符有个两个参数的构造方法可以更改发射的数据的值。");
        Observable.just(1, 2, 3, 4)
                .toMap(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "key" + integer;
                    }
                }, new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return integer + 10;
                    }
                })
                .subscribe(new Subscriber<Map<String, Integer>>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(Map<String, Integer> integerIntegerMap) {
                        Log.e(TAG, "onNext: " + integerIntegerMap.toString());

                    }
                });
    }

    private void toMutimap() {
        Log.e(TAG, "toMutimap 生成的这个Map同时还是一个ArrayList。");
        Observable.just(1, 2, 3, 4)
                .toMultimap(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "key" + integer;
                    }
                }).subscribe(new Subscriber<Map<String, Collection<Integer>>>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(Map<String, Collection<Integer>> integerCollectionMap) {
                Log.e(TAG, "onNext: " + integerCollectionMap.toString());
            }
        });
    }

    private void toSortedList() {
        Log.e(TAG, "toSortedList 类似于toList，区别是它可以对数据进行自然排序。");
        Integer[] integers = {2, 3, 6, 4, 9, 2, 8};
        Observable.from(integers)
                .toSortedList()
                .flatMap(new Func1<List<Integer>, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(List<Integer> integer) {
                        Log.e(TAG, "call: " + integer.toString());
                        return Observable.from(integer);
                    }
                }).subscribe(new Subscriber<Integer>() {
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
                mEditText.append("\n" + integer);
            }
        });
    }

}
