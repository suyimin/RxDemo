package com.xdroid.rxjava.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.xdroid.rxjava.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxOp3Activity extends AppCompatActivity {
    private static String TAG = "RxOp3Activity";

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

        filter();
        ofType();
        first();
        firstOrDefault();
        takeFirst();
        single();
        last();
        skip();
        skipLast();
        take();
        debounce();
        distinct();
        elementAt();
        ignoreElements();

    }

    private void filter() {
        Log.e(TAG, "Filter 接收一个Func1参数，我们可以在其中通过运用你自己的判断条件去判断我们要过滤的数据，当数据通过判断条件后返回true表示发射该项数据，否则就不发射。");
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Observable observable = Observable.from(ints).filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer % 2 != 0;//返回true，就不会过滤掉，会发射数据，过滤掉返回false的值
            }
        });
        Action1 action1 = new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                Log.e(TAG, "call: " + i);
            }
        };
        observable.subscribe(action1);
    }

    private void ofType() {
        Log.e(TAG, "ofType 是filter操作符的一个特殊形式。它过滤一个Observable只返回指定类型的数据。");
        Observable.just(0, "one", 6, 4, "two", 8, "three", 1, "four", 0)
                .ofType(String.class)
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted:ofType ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError:ofType ");
                    }

                    @Override
                    public void onNext(String string) {
                        Log.e(TAG, "onNext:ofType " + string);
                    }
                });
    }

    private void first() {
        Log.e(TAG, "First 如果我们只对Observable发射的第一项数据，或者满足某个条件的第一项数据感兴趣，则可以使用First操作符。");
        Observable.just(10, 11, 12, 13).first().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer + "");
            }
        });

        Observable.just(10, 11, 12, 13).first(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer > 12;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer + "");
            }
        });
    }

    private void firstOrDefault() {
        Log.e(TAG, "firstOrDefault 该操作符是first操作符的变形。主要是在没有发射任何数据时发射一个你在参数中指定的默认值。");
        Observable.just(11, 12, 13).firstOrDefault(10).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Log.e(TAG, o.toString());
            }
        });

        Observable.empty().firstOrDefault(10).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Log.e(TAG, o.toString());
            }
        });

        Observable.just(10, 13, 16).firstOrDefault(15, new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer > 20;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, "" + integer);
            }
        });
    }

    private void takeFirst() {
        Log.e(TAG, "takeFirst 该操作符与first操作符的区别就是如果原始Observable没有发射任何满足条件的数据，first会抛出一个NoSuchElementException直接执行onError()，而takeFist会返回一个空的Observable（不调用onNext()但是会调用onCompleted）。");
        Observable.just(10, 11).filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer > 20;
            }
        }).first().subscribe(new Subscriber<Object>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.toString());
            }

            @Override
            public void onNext(Object o) {
                Log.e(TAG, "onNext: " + o.toString());
            }
        });

        Observable.just(10, 11).takeFirst(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                Log.e(TAG, "call: takeFirst");
                return integer > 30;
            }
        }).subscribe(new Subscriber<Object>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.toString());
            }

            @Override
            public void onNext(Object o) {
                Log.e(TAG, "onNext: " + o.toString());
            }
        });
    }


    private void single() {
        Log.e(TAG, "single 发送数据是一项的话输出此项的值，若是多个数据则抛出异常执行onError()方法。");
        Log.e(TAG, "single也有singleOrDefault(T)和singleOrDefault(T,Func1)两个变体。");
        Observable.just(10, 11, 12, 13).single().subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError " + e.toString());
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext " + integer);
            }
        });
    }

    private void last() {
        Log.e(TAG, "Last 若我们只对Observable发射的最后一项数据，或者满足某个条件的最后一项数据感兴趣时使用该操作符。");
        Observable.just(10, 11, 12, 13).last().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, "call: " + integer);
            }
        });

        Observable.just(10, 11, 12, 13).last(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer < 12;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, "call: " + integer);
            }
        });
    }

    private void skip() {
        Log.e(TAG, "Skip 跳过前几项数据，然后再发射数据。");
        Observable.range(1, 10).skip(6).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, "call: " + integer);
            }
        });

        Observable.interval(500, TimeUnit.MILLISECONDS)
                .skip(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong > 10) {
                            this.unsubscribe();
                        }
                    }
                });
    }

    private void skipLast() {
        Log.e(TAG, "skipLast 忽略最后产生的n个数据项。");
        Observable.range(1, 10).skipLast(6).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, "call: " + integer);
            }
        });
    }

    private void take() {
        Log.e(TAG, "Take 修改Observable的行为，只返回前面的N项数据，然后发射完成通知，忽略剩余的数据。");
        Log.e(TAG, "take和skip一样也有其它两个重载方法take(long time, TimeUnit unit)，take(long time, TimeUnit unit, Scheduler scheduler)，默认在computation调度器上执行。");
        Log.e(TAG, "Take还有变体操作符TakeLast，takeLastBuffer。");
        Observable.range(1, 8)
                .take(4)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        Log.e(TAG, "Next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "complete.");
                    }
                });
    }

    private void debounce() {
        Log.e(TAG, "Debounce 指的是过了一段指定的时间还没发射数据时才发射一个数据。");
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Observable<String> observable = Observable.from(ints).flatMap(new Func1<Integer, Observable<String>>() {
            @Override
            public Observable<String> call(Integer integer) {
                try {
                    Thread.sleep(200 * integer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return Observable.just(integer + "");
            }
        });
        observable.subscribeOn(Schedulers.newThread())
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
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

    private void distinct() {
        Log.e(TAG, "Distinct 过滤掉重复的数据，只允许还没有发射过的数据项通过。");
        Observable.just(0, 0, 6, 4, 2, 8, 2, 1, 9, 0)
                .distinct()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted:Distinct ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError:Distinct ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext:Distinct " + integer);
                    }
                });
    }

    private void elementAt() {
        Log.e(TAG, "ElementAt 获取原始Observable发射的数据序列指定索引位置的数据项，然后当做自己的唯一数据发射。");
        Observable.just(0, 0, 6, 4, 2, 8, 2, 1, 9, 0)
                .elementAt(4)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted:ElementAt ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError:ElementAt ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext:ElementAt " + integer);
                    }
                });
    }

    private void ignoreElements() {
        Log.e(TAG, "IgnoreElements 抑制原始Observable发射的所有数据，只允许它的终止通知（onError或onCompleted）通过，使用该操作符onNext()方法不会执行。");
        Observable.just(1, 2, 3).ignoreElements().subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext");
            }
        });
    }

}
