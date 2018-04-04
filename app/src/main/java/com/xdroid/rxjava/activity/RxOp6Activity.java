package com.xdroid.rxjava.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xdroid.rxjava.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxOp6Activity extends AppCompatActivity {
    private static String TAG = "RxOp6Activity";

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

        all();
        amb();
        contains();
        defaultIfEmpty();
        sequenceEqual();
        skipUntil();
        skipWhile();
        takeUntil();
        takeWhile();
    }

    private void all() {
        Log.e(TAG, "All 判断Observable发射的所有数据是否都满足某一条件。");
        Log.e(TAG, "它接收一个Fun1方法参数，此方法接收原始数据，并返回一个布尔类型值。");
        Log.e(TAG, "如果原始Observable正常终止并且每一项数据都满足条件，就返回true；如果原始Observable的任何一项数据不满足条件就返回False。");
        Log.e(TAG, "当某数据不满足条件（返回false）时之后的数据不再发射。");
        Observable.just(1, 2, 3, 4).all(new Func1<Integer, Boolean>() {

            @Override
            public Boolean call(Integer integer) {
                Log.e(TAG, "call: " + integer);
                return integer < 2;
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

    private void amb() {
        Log.e(TAG, "Amb 当我们传递多个Observable（可接收2到9个Observable）给amb时，它只发射其中一个Observable的数据和通知。");
        Log.e(TAG, "Observable.amb(observable,observable1)改为observable.ambWith(observable1)是等价的。");
        Observable observable = Observable.just(1, 2, 3).delay(500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread());
        Observable observable1 = Observable.just(4, 5, 6).delay(100, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread());
        Observable.amb(observable, observable1).subscribe(new Subscriber<Integer>() {
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
                Log.e(TAG, "  onNext: " + integer);
            }
        });
    }

    private void contains() {
        Log.e(TAG, "Contains 即Observable发射的数据是否包含某一对象。");
        Log.e(TAG, "isEmpty也是一种判断是否包含的操作符，不同的是它判断原始Observable是否没有发射任何数据。");
        Log.e(TAG, "isEmpty也是一种判断是否包含的操作符，不同的是它判断原始Observable是否没有发射任何数据。");
        Observable.just(1, 2, 3, 4).contains(2)
                .subscribe(new Subscriber<Boolean>() {
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

    private void defaultIfEmpty() {
        Log.e(TAG, "DefaultIfEmpty 简单的精确地发射原始Observable的值，如果原始Observable没有发射任何数据正常终止（以onCompletedd的形式），DefaultIfEmpty返回的Observable就发射一个我们提供的默认值。");
        Observable.empty().defaultIfEmpty(1).subscribe(new Subscriber<Object>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(Object object) {
                Log.e(TAG, "onNext: " + object);
            }
        });
    }

    private void sequenceEqual() {
        Log.e(TAG, "SequenceEqual 会比较两个Observable的发射物，如果两个序列是相同的（相同的数据，相同的顺序，相同的终止状态），它就发射true，否则发射false。");
        Observable observable = Observable.just(1, 2, 3);
        Observable observable1 = Observable.just(1, 3, 2);
        Observable.sequenceEqual(observable, observable1)
                .subscribe(new Subscriber<Boolean>() {
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

    private void skipUntil() {
        Log.e(TAG, "SkipUntil SkipUntil订阅原始的Observable，但是忽略它的发射物，直到第二个Observable发射了一项数据那一刻，它开始发射原始Observable。");
        Observable observable = Observable.just(1, 2, 3, 4, 5, 6).delay(100, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread());
        Observable observable1 = Observable.just(20, 21, 22).delay(130, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread());
        observable.skipUntil(observable1)
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "onNext: " + o);
                    }
                });
    }

    private void skipWhile() {
        Log.e(TAG, "SkipWhile该操作符也是忽略它的发射物，直到我们指定的某个条件变为false的那一刻，它开始发射原始Observable。");
        Observable.range(1, 5).skipWhile(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                Log.e(TAG, "call: " + integer);
                return integer < 3;
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

    private void takeUntil() {
        Log.e(TAG, "TakeUntil 当第二个Observable发射了一项数据或者终止时，丢弃原始Observable发射的任何数据。");
        Observable observable = Observable.just(1, 2, 3, 4, 5, 6).delay(100, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread());
        Observable observable1 = Observable.just(20, 21, 22).delay(120, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread());
        observable.takeUntil(observable1)
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "onNext: " + o);
                    }
                });
    }

    private void takeWhile() {
        Log.e(TAG, "TakeWhile 发射原始Observable，直到我们指定的某个条件不成立的那一刻，它停止发射原始Observable，并终止自己的Observable。");
        Observable.range(1, 5).takeWhile(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                Log.e(TAG, "call: " + integer);
                return integer < 3;
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

}
