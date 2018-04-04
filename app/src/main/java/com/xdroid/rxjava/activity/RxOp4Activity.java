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
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class RxOp4Activity extends AppCompatActivity {
    private static String TAG = "RxOp4Activity";

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

        merge();
        mergeDelayError();
        concat();
        zip();
        startWith();
        combineLatest();
        join();
        switchOnNext();

    }

    private void merge() {
        Log.e(TAG, "Merge 可以将多个Observables的输出合并，就好像它们是一个单个的Observable一样，他可能让我们让合并的Observables发射的数据交错（顺序发生变化）。");
        Log.e(TAG, "在此过程中任何一个原始Observable的onError通知都会被立即传递给观察者，而且会终止合并后的Observable。");
        Observable observable = Observable.just(1, 2);
        Observable observable1 = Observable.just(6, 7);
        Observable observable2 = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    Thread.sleep(500);
                    subscriber.onNext(200);
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.newThread());

        Observable.merge(observable2, observable, observable1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.toString());
                    }

                    @Override
                    public void onNext(Integer s) {
                        Log.e(TAG, "onNext: " + s);
                    }
                });
    }

    private void mergeDelayError() {
        Log.e(TAG, "MergeDelayError 如果你想让它继续发射数据，在最后才报告错误，可以使用mergeDelayError。");
        Observable observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {

                try {
                    subscriber.onNext(100);
                    subscriber.onError(new Throwable("error"));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(new Throwable("error11"));
                }

            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        Observable observable2 = Observable.just(6, 7, 8, 9).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        Observable.mergeDelayError(observable, observable2)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.toString());
                    }

                    @Override
                    public void onNext(Integer s) {
                        Log.e(TAG, "onNext: " + s);
                    }
                });
    }

    private void concat() {
        Log.e(TAG, "Concat 该操作符和merge操作符相似，不同之处就是该操作符按顺序一个接着一个发射多个Observables的发射物。保证顺序性。");
        Observable observableA = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {

                try {
                    subscriber.onNext(100);
                    Thread.sleep(500);
                    subscriber.onNext(200);
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    subscriber.onError(new Throwable("error11"));
                }
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        Observable<Integer> observableB = Observable.range(7, 2);
        Observable.concat(observableA, observableB).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: concat");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: concat");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext: concat" + integer);
            }
        });
    }

    private void zip() {
        Log.e(TAG, "Zip 返回一个Obversable，它使用这个函数按顺序结合两个或多个Observables发射的数据项，然后它发射这个函数返回的结果。");
        Log.e(TAG, "Zip 按照严格的顺序应用这个函数。它只发射与发射数据项最少的那个Observable一样多的数据。");
        List<String> names = new ArrayList<>();
        List<Integer> ages = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            names.add("张三" + i);
            ages.add(20 + i);
        }
        ages.add(15);
        Observable observable1 = Observable.from(names).subscribeOn(Schedulers.io());
        Observable observable2 = Observable.from(ages).subscribeOn(Schedulers.io());
        //Func2第三个参数是返回值类型
        Observable.zip(observable1, observable2, new Func2<String, Integer, String>() {
            @Override
            public String call(String name, Integer age) {
                return name + ": " + age;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(String o) {
                Log.e(TAG, "onNext: " + o);
            }
        });
    }

    private void startWith() {
        Log.e(TAG, "StartWith 在一个Observable在发射数据之前先发射一个指定的数据序列。");
        Observable.range(1, 3).startWith(11, 12).subscribe(new Subscriber<Integer>() {
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

    private void combineLatest() {
        Log.e(TAG, "CombineLatest 操作符行为类似于zip，但是只有当原始的Observable中的每一个都发射了一条数据时zip才发射数据。CombineLatest则在原始的Observable中任意一个发射了数据时发射一条数据。");
        Log.e(TAG, "当原始Observables的任何一个发射了一条数据时，CombineLatest使用一个函数结合它们最近发射的数据，然后发射这个函数的返回值。");
        Observable<Integer> observableA = Observable.range(1, 4);
        Observable<Integer> observableB = Observable.range(10, 5);
        Observable.combineLatest(observableA, observableB, new Func2<Integer, Integer, String>() {
            @Override
            public String call(Integer integer, Integer integer2) {
                Log.e(TAG, "call: combineLatest");
                return "observableA:" + integer + "  observableB:" + integer2;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, "call: combineLatest" + s);
            }
        });
    }

    private void join() {
        Log.e(TAG, "Join 只要在另一个Observable发射的数据定义的时间窗口内，这个Observable发射了一条数据，就结合两个Observable发射的数据。");
        Observable<Integer> observableA = Observable.range(1, 2).subscribeOn(Schedulers.newThread());
        Observable<Integer> observableB = Observable.range(7, 3).subscribeOn(Schedulers.newThread());
        observableA.join(observableB, new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer integer) {
                Log.e(TAG, "call: A" + integer + "   " + Thread.currentThread().getName());
                return Observable.just(integer).delay(1, TimeUnit.SECONDS);
            }
        }, new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer integer) {
                Log.e(TAG, "call: B" + integer + "   " + Thread.currentThread().getName());
                return Observable.just(integer).delay(1, TimeUnit.SECONDS);
            }
        }, new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                Log.e(TAG, "call:AjoinB A: " + integer + " B:" + integer2 + Thread.currentThread().getName());
                return integer + integer2;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.toString());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext: " + integer);
                    }
                });
    }

    private void switchOnNext() {
        Log.e(TAG, "switchOnNext 将一个发射多个Observables的Observable转换成另一个单独的Observable，后者发射那些Observables最近发射的数据项。当有新的Observable开始订阅时，会取消之前的订阅，并将数据丢弃。");
        Observable<Observable<Long>> observable = Observable.interval(0, 500, TimeUnit.MILLISECONDS).map(new Func1<Long, Observable<Long>>() {
            @Override
            public Observable<Long> call(Long aLong) {
                //每隔200毫秒产生一组数据（0,10,20,30,40)
                Log.e(TAG, "call1: " + aLong);
                return Observable.interval(0, 200, TimeUnit.MILLISECONDS).map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        Log.e(TAG, "call2: " + aLong);
                        return aLong * 10;
                    }
                }).take(5);
            }
        }).take(2);
        Observable.switchOnNext(observable).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: SwitchOnNext");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: SwitchOnNext");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "SwitchOnNext onNext: " + aLong);
            }
        });
    }

}
