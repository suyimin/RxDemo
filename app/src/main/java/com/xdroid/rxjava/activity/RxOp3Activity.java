package com.xdroid.rxjava.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.xdroid.rxjava.R;

import rx.Observable;
import rx.Subscriber;

public class RxOp3Activity extends AppCompatActivity {
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

        window();

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
