package com.xdroid.rxjava.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.xdroid.rxjava.R;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

public class RxOp2Activity extends AppCompatActivity {
    private static String TAG = "RxOp2Activity";

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

//        map();
//        cast();
//        flatMap();
//        concatMap();
//        switchMap();
//        groupBy();
//        scan();
//        buffer();
        window();

    }


    private void map() {
        Log.e(TAG, "Map 对原始Observable发射的每一项数据运用一个函数，然后返回一个发射这些结果的Observable.");
        Integer[] integers = {0, 9, 6, 4, 8};
        Observable.from(integers).map(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                Log.e(TAG, "call: " + integer);
                return (integer > 5);
            }
        }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(Boolean aBoolean) {
                Log.e(TAG, "onNext: " + aBoolean);
            }
        });
    }

    private void cast() {
        Log.e(TAG, "Cast 做一些强制类型转换操作。这个操作符也可以达到java 中instanceof相同的作用，用于类型检查，当不是该类型就会执行onError()方法。");
        List<Object> urlsList = new ArrayList<>();
        urlsList.add("http://google.com");
        urlsList.add("http://droidcon.in");
        urlsList.add("http://jsfoo.in");
        urlsList.add(1);

        Observable.from(urlsList).cast(String.class).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(String info) {
                Log.e(TAG, "onNext: " + info);
                mEditText.append("\n" + info);
            }
        });
    }

    private void flatMap() {
        Log.e(TAG, "FlatMap 该操作符与map操作符的区别是它将一个发射数据的Observable变换为多个Observables，然后将它们发射的数据合并后放进一个单独的Observable。");
        Integer[] integers = {1, 2, 3};
        Observable.from(integers).flatMap(new Func1<Integer, Observable<String>>() {
            @Override
            public Observable<String> call(final Integer integer) {
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        Log.e(TAG, "call: FlatMap " + Thread.currentThread().getName());
                        try {
                            Thread.sleep(200);
                            subscriber.onNext(integer + 100 + " FlatMap");
                            subscriber.onCompleted();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.newThread());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: FlatMap");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: FlatMap");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onNext: FlatMap " + s);
                    }
                });
    }

    private void concatMap() {
        Log.e(TAG, "ConcatMap 类似于最简单版本的flatMap，但是它按次序连接而不是合并那些生成的Observables，然后产生自己的数据序列。");
        Integer[] integers = {1, 2, 3};
        Observable.from(integers).concatMap(new Func1<Integer, Observable<String>>() {
            @Override
            public Observable<String> call(final Integer integer) {
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        Log.e(TAG, "call:2 ConcatMap " + Thread.currentThread().getName());
                        try {
                            Thread.sleep(200);
                            subscriber.onNext(integer + 100 + " ConcatMap");
                            subscriber.onCompleted();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.newThread());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ConcatMap");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ConcatMap");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onNext: ConcatMap " + s);
                    }
                });
    }

    private void switchMap() {
        Log.e(TAG, "SwitchMap 当原始Observable发射一个新的数据（Observable）时，它将取消订阅并停止监视产生执之前那个数据的Observable，只监视当前这一个。");
        Integer[] integers = {1, 2, 3};
        Observable.from(integers).switchMap(new Func1<Integer, Observable<String>>() {
            @Override
            public Observable<String> call(Integer integer) {
                Log.e(TAG, "call: SwitchMap" + Thread.currentThread().getName());
                //如果不通过subscribeOn(Schedulers.newThread())在在子线程模拟并发操作，所有数据源依然会全部输出,也就是并发操作此操作符才有作用
                //若在此通过Thread.sleep（）设置等待时间，则输出信息会不一样。相当于模拟并发程度
                return Observable.just((integer + 100) + "SwitchMap").subscribeOn(Schedulers.newThread());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: SwitchMap");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: SwitchMap");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: SwitchMap " + s);
            }
        });
    }

    private void groupBy() {
        Log.e(TAG, "GroupBy 将数据源按照你的约定进行分组。");
        Observable.range(1, 10).groupBy(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer % 2 == 0;
            }
        }).subscribe(new Subscriber<GroupedObservable<Boolean, Integer>>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted:1 ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError:1 ");
            }

            @Override
            public void onNext(GroupedObservable<Boolean, Integer> booleanIntegerGroupedObservable) {
                booleanIntegerGroupedObservable.toList().subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted:2 ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError:2 ");
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.e(TAG, "onNext:2 " + integers);
                    }
                });
            }
        });
    }

    private void scan() {
        Log.e(TAG, "Scan 对原始Observable发射的第一项数据应用一个函数，然后将那个函数的结果作为自己的第一项数据发射。它将函数的结果同第二项数据一起填充给这个函数来产生它自己的第二项数据。它持续进行这个过程来产生剩余的数据序列。");
        Log.e(TAG, "计算1+2+3+4的和");
        Observable.range(1, 4).scan(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                Log.e(TAG, "call: integer:" + integer + "  integer2 " + integer2);
                return integer + integer2;
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
            }
        });
    }

    private void buffer() {
        Log.e(TAG, "Buffer 将一个Observable变换为另一个，原来的Observable正常发射数据，变换产生的Observable发射这些数据的缓存集合，如果原来的Observable发射了一个onError通知，Buffer会立即传递这个通知，而不是首先发射缓存的数据，即使在这之前缓存中包含了原始Observable发射的数据。");
        Observable.range(10, 6).buffer(2).subscribe(new Subscriber<List<Integer>>() {
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

    private void window() {
        Log.e(TAG, "Window 和Buffer类似，但不是发射来自原始Observable的数据包，它发射的是Observables，这些Observables中的每一个都发射原始Observable数据的一个子集，最后发射一个onCompleted通知。");
        Observable.range(10, 6).window(2).subscribe(new Subscriber<Observable<Integer>>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted1: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError1: ");
            }

            @Override
            public void onNext(Observable<Integer> integerObservable) {
                Log.e(TAG, "onNext1: ");
                integerObservable.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted2: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError2: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext2: " + integer);
                    }
                });
            }
        });
    }

}
