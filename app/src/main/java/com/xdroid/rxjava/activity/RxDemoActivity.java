package com.xdroid.rxjava.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.xdroid.rxjava.R;
import com.xdroid.rxjava.application.RxJavaApplication;
import com.xdroid.rxjava.constant.Constant;
import com.xdroid.rxjava.factory.DataFactory;
import com.xdroid.rxjava.factory.ImageNameFactory;
import com.xdroid.rxjava.githubapi.GitHubApi;
import com.xdroid.rxjava.model.Contributor;
import com.xdroid.rxjava.model.Course;
import com.xdroid.rxjava.model.Student;
import com.xdroid.rxjava.model.User;
import com.xdroid.rxjava.service.RetrofitService;
import com.xdroid.rxjava.utils.ClickUtils;
import com.xdroid.rxjava.utils.DeviceInfo;
import com.xdroid.rxjava.utils.ImageUtils;
import com.xdroid.rxjava.utils.RecycleBitmap;
import com.xdroid.rxjava.utils.RxUtils;
import com.xdroid.rxjava.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * 类描述:RxJava测试活动主页面
 */
public class RxDemoActivity extends AppCompatActivity {

    private static final String TAG = "RxDemoActivity";
    private static final String ERROR = "故意让程序出错";
    private static final String JPG = ".jpg";

    /**
     * 循环的计数器
     */
    private int mCounter;

    private ImageView mImageView;
    private Bitmap mManyBitmapSuperposition = null;
    private Canvas mCanvas = null;
    private ProgressBar mProgressBar;
    private EditText mSearchEditText;
    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int number = getIntent().getIntExtra("Fuction", 0);
        setContentView(R.layout.activity_rx_demo);
        initializeLogAndDeviceInfo();
        initView();
        testFuncation(number);
    }

    /**
     * 初始化Logger日志输出配置和获取手机尺寸信息
     */
    private void initializeLogAndDeviceInfo() {
        //Use LogLevel.NONE for the release versions.
        Logger.init(TAG).logLevel(LogLevel.FULL);
        DeviceInfo.getInstance().initializeScreenInfo(this);
    }

    /**
     * 用于显示图片的初始化
     */
    private void initView() {
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mResultTextView = (TextView) findViewById(R.id.tv_result);
        mSearchEditText = (EditText) findViewById(R.id.ed_search);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    /**
     * 0:RxJava基础练习==================================================
     * 概念解释
     * 1:被观察者,事件源:它决定什么时候触发事件以及触发怎样的事件
     * 2:观察者:它决定事件触发的时候将有怎样的行为
     * 3:订阅
     */
    private void method0() {

        //1:被观察者,事件源
        //概念解释:RxJava 使用 Observable.create()方法来创建一个Observable，并为它定义事件触发规则。
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello");
                subscriber.onNext("World");
                subscriber.onNext("!");
                subscriber.onCompleted();
                subscriber.onError(new Throwable());
                Logger.d("被观察者-observable->call()->onCompleted()之后是否还有输出");
            }
        });

        /**
         * 可以看到，这里传入了一个 OnSubscribe 对象作为参数。
         * OnSubscribe 会被存储在返回的 Observable 对象中，它的作用相当于一个计划表，
         * 当Observable 被订阅的时候，OnSubscribe 的 call() 方法会自动被调用，事件序列就会依照设定依次触发
         * （对于上面的代码，就是观察者subscriber 将会被调用三次 onNext() 和一次 onCompleted()）。
         * 这样，由被观察者调用了观察者的回调方法，就实现了由被观察者向观察者的事件传递，即观察者模式。
         */

        //2:观察者
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                Logger.d("观察者-observer:onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Logger.d("观察者-observer:onError" + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Logger.d("观察者-observer:onNext():" + s);
            }
        };

        //3:订阅--被观察者被观察者订阅
        observable.subscribe(observer);
    }


    /**
     * 1:快捷创建事件队列 Observable.just(T...)==================================================
     * create() 方法是 RxJava 最基本的创造事件序列的方法。
     * 基于这个方法， RxJava 还提供了一些方法用来快捷创建事件队列，例如just(T...): 将传入的参数依次发送出来.
     * 简化:观察者的创建,RxJava快捷创建事件队列的方法:just(T...):
     */
    private void method1() {

        //1:被观察者:
        //just(T...): 将传入的参数依次发送出来
        Observable<String> observable = Observable.just("Hello", "World", "!");
        // 将会依次调用：
        // onNext("Hello");
        // onNext("World");
        // onNext("!");
        // onCompleted();


        //2:观察者:
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                Logger.d("观察者-observer:onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Logger.d("观察者-observer:onError()");
            }

            @Override
            public void onNext(String s) {
                Logger.d("观察者-observer:onNext():" + s);
            }
        };

        //3:订阅:被观察者被观察者订阅
        observable.subscribe(observer);
    }


    /**
     * 2:快捷创建事件队列 Observable.from(T[]) / from(Iterable<? extends T>============================
     */
    private void method2() {

        String[] array = new String[]{"Hello", "World", "!"};
        //1:被观察者:
        //just(String[] array) 将传入的数组或 Iterable 拆分成具体对象后，依次发送出来。
        Observable observable = Observable.from(array);
        // 将会依次调用：
        // onNext("Hello");
        // onNext("World");
        // onNext("!");
        // onCompleted();


        //2:观察者
        Observer observer = new Observer() {
            @Override
            public void onCompleted() {
                Logger.d("观察者-observer:onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Logger.d("观察者-observer:onError()");
            }

            @Override
            public void onNext(Object o) {
                String str = (String) o;
                Logger.d("观察者-observer:onNext():" + str);
            }
        };

        //3:订阅: 被观察者被观察者订阅
        observable.subscribe(observer);

    }

    /**
     * 3: subscribe()支持不完整定义的回调==================================================
     * 对观察者的简化
     * subscribe一个参数的不完整定义的回调
     * subscribe(final Action1<? super T> onNext)
     */
    private void method3() {

        String[] array = new String[]{"Hello", "World", "!"};
        //1:被观察者
        Observable observable = Observable.from(array);

        //2:观察者
        Action1 onNextAction = new Action1() {
            @Override
            public void call(Object o) {
                String str = (String) o;
                Logger.d("观察者:call(Object o):" + str);
            }
        };

        //3:订阅-被观察者被观察者订阅
        //subscribe(final Action1<? super T> onNext)
        //自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
        observable.subscribe(onNextAction);
    }

    /**
     * 对观察者的简化
     * subscribe两个参数的不完整定义的回调
     * subscribe(final Action1<? super T> onNext, final Action1<Throwable> onError)
     */
    private void method4() {

        //1:被观察者
        Observable observable = Observable.from(new String[]{"Hello", "World", "!"});

        //2:观察者
        Action1 onNextAction = new Action1() {
            @Override
            public void call(Object o) {
                String str = (String) o;
                Logger.d("观察者:onNextAction:call(Object o):o:" + str);
            }
        };


        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Logger.d("观察者:onErrorAction:call(Throwable throwable):" + throwable.getMessage());
            }
        };


        //3:订阅
        //subscribe(final Action1<? super T> onNext, final Action1<Throwable> onError)
        // 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
        observable.subscribe(onNextAction, onErrorAction);


    }

    /**
     * subscribe三个参数的不完整定义的回调
     * subscribe(final Action1<? super T> onNext, final Action1<Throwable> onError, final Action0 onComplete)
     */
    private void method5() {
        //1:被观察者
        Observable observable = Observable.from(new String[]{"Hello", "World", "!"});


        //2:观察者
        Action1 onNextAction = new Action1() {
            @Override
            public void call(Object o) {
                String str = (String) o;
                Logger.d("观察者:onNextAction:call():s:" + str);
            }
        };


        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Logger.d("观察者:onErrorAction:call(Throwable throwable):" + throwable.getMessage());
            }
        };


        Action0 onCompletedAction = new Action0() {
            @Override
            public void call() {
                Logger.d("观察者:onCompletedAction:call()");
            }
        };


        //3:订阅:被观察者被观察者订阅

        //subscribe(final Action1<? super T> onNext, final Action1<Throwable> onError, final Action0 onComplete)
        // 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);

    }

    /**
     * 4: Action0和Action1 讲解==================================================
     *
     * Action0 是 RxJava 的一个接口，它只有一个方法 call()，这个方法是无参无返回值的；
     * 由于 onCompleted() 方法也是无参无返回值的，因此 Action0 可以被当成一个包装对象，
     * 将 onCompleted() 的内容打包起来将自己作为一个参数传入 subscribe() 以实现不完整定义的回调。
     * 这样其实也可以看做将 onCompleted() 方法作为参数传进了 subscribe()，相当于其他某些语言中的『闭包』。
     *
     * Action1 也是一个接口，它同样只有一个方法 call(T param)，这个方法也无返回值，但有一个参数；
     * 与 Action0 同理，由于 onNext(T obj) 和 onError(Throwable error) 也是单参数无返回值的，
     * 因此 Action1 可以将 onNext(obj) 和 onError(error) 打包起来传入 subscribe() 以实现不完整定义的回调。
     * 事实上，虽然 Action0 和 Action1 在 API 中使用最广泛，
     * 但 RxJava 是提供了多个 ActionX 形式的接口 (例如 Action2, Action3) 的，
     * 它们可以被用以包装不同的无返回值的方法。
     */


    /**
     * 5: 推荐两个好用的日志查看工具==================================================
     * 1.[logger](https://github.com/orhanobut/logger) | 一个简洁,优雅,功能强大的Android日志输出工具
     * 2.[pidcat](https://github.com/JakeWharton/pidcat)|JakeWharton项目一个简洁,优雅的,彩色日志终端查看库|在终端过滤日志信息
     * 使用com.github.orhanobut:logger 库可以查看当前的线程
     *  ╔════════════════════════════════════════════════════════════════════════════════════════
     *  ╟────────────────────────────────────────────────────────────────────────────────────────
     *  ║ RxDemoActivity$11.onNext  (RxDemoActivity.java:338)
     *  ║    RxDemoActivity$11.onNext  (RxDemoActivity.java:354)
     *  ╟────────────────────────────────────────────────────────────────────────────────────────
     *  ║ 观察者 onNext()
     *  ╚════════════════════════════════════════════════════════════════════════════════════════
     *  ╔════════════════════════════════════════════════════════════════════════════════════════
     *  ║ Thread: main
     *  ╟────────────────────────────────────────────────────────────────────────────────────────
     *  ║ SafeSubscriber.onCompleted  (SafeSubscriber.java:83)
     *  ║    RxDemoActivity$11.onCompleted  (RxDemoActivity.java:341)
     *  ╟────────────────────────────────────────────────────────────────────────────────────────
     *  ║ 观察者 onCompleted()
     *  ╚════════════════════════════════════════════════════════════════════════════════════════
     */


    /**
     * 6 线程控制-Scheduler==================================================
     * 显示图片
     * 后台线程取数据，主线程显示
     * 加载图片将会发生在 IO 线程，而设置图片则被设定在了主线程。
     * 这就意味着，即使加载图片耗费了几十甚至几百毫秒的时间，也不会造成丝毫界面的卡顿。
     */
    private void method6() {

        final int drawableRes = R.mipmap.launcher;
        // 1:被观察者
        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Logger.d("被观察者");
                Drawable drawable = ContextCompat.getDrawable(RxJavaApplication.getApplication(), drawableRes);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        })
                // 事件产生的线程。
                // 指定 subscribe() 发生在 IO 线程
                .subscribeOn(Schedulers.io())
                // doOnSubscribe() 之后有 observeOn() 的话，它将执行在离它最近的 observeOn() 所指定的线程。
                // 这里将执行在主线程中
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mProgressBar != null) {
                            // 显示一个等待的ProgressBar
                            // 需要在主线程中执行
                            mProgressBar.setVisibility(View.VISIBLE);
                        }
                    }
                })
                //指定 Subscriber 所运行在的线程。或者叫做事件消费的线程
                .observeOn(AndroidSchedulers.mainThread())
                // 3:订阅 // 2:观察者
                .subscribe(new Subscriber<Drawable>() {
                    @Override
                    public void onCompleted() {
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.GONE);
                        }
                        Logger.d("观察者 onCompleted()");
                        Toast.makeText(RxDemoActivity.this, "观察者 onCompleted()", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.GONE);
                        }
                        Logger.d("观察者 onError()");
                        Toast.makeText(RxDemoActivity.this, "观察者 onError() " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNext(Drawable drawable) {
                        Toast.makeText(RxDemoActivity.this, "观察者 onNext()", Toast.LENGTH_SHORT).show();
                        Logger.d("观察者 onNext()");
                        if (mImageView == null || drawable == null) {
                            return;
                        }
                        mImageView.setImageDrawable(drawable);
                    }
                });

    }

    /**
     * 7: 变换 map()==================================================
     */
    private void method7() {
        final int drawableRes = R.mipmap.launcher;

        // 1:被观察者
        Observable.just(drawableRes)
                .map(new Func1<Integer, Drawable>() {

                    @Override
                    public Drawable call(Integer integer) {
                        Logger.d("integer:" + integer);
                        return ContextCompat.getDrawable(RxJavaApplication.getApplication(), integer);
                    }
                })
                // 事件产生的线程。指定 subscribe() 发生在 IO 线程
                .subscribeOn(Schedulers.io())
                // doOnSubscribe() 之后有 observeOn() 的话，它将执行在离它最近的 observeOn() 所指定的线程。
                // 这里将执行在主线程中。
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mProgressBar != null) {
                            // 显示一个等待的ProgressBar
                            // 需要在主线程中执行
                            mProgressBar.setVisibility(View.VISIBLE);
                        }
                    }
                })
                // 指定 Subscriber 所运行在的线程。
                // 或者叫做事件消费的线程
                .observeOn(AndroidSchedulers.mainThread())
                // 3:订阅 // 2:观察者
                .subscribe(new Subscriber<Drawable>() {
                    @Override
                    public void onCompleted() {
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.GONE);
                        }
                        Logger.d("观察者:onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(RxDemoActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Logger.d("观察者:onError(Throwable e):" + e.getMessage());
                    }

                    @Override
                    public void onNext(Drawable drawable) {
                        if (mImageView == null || drawable == null) {
                            return;
                        }
                        mImageView.setImageDrawable(drawable);
                        Logger.d("观察者:onNext(Drawable drawable):" + drawable.toString());
                    }
                });
    }


    /**
     * 8: 演示嵌套循环==================================================
     */
    private void method8() {
        ArrayList<Student> students = DataFactory.getData();
        int size = students.size();
        for (int i = 0; i < size; i++) {
            Logger.d("姓名:" + students.get(i).name);
            int sizeCourses = students.get(i).courses.size();
            for (int j = 0; j < sizeCourses; j++) {
                Logger.d("课程:" + students.get(i).courses.get(j).name);
            }
        }
    }


    /**
     * 依次输出学生的姓名:将每个学生(实体对象)依次发射出去
     */
    private void method9() {
        //just(T...): 将传入的参数依次发送出来,实现遍历的目的
        Observable.from(DataFactory.getData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                        Logger.d("观察者:" + student.name);
                    }
                });
    }


    /**
     * 输出学生的姓名:将每个学生的(姓名)依次发射出去
     */
    private void method10() {

        //1:被观察者

        //2:数据转换

        //3:事件产生的线程。

        //4:事件消费的线程。

        //5:被观察者被观察者订阅

        //6:观察者

        Observable.from(DataFactory.getData())

                .map(new Func1<Student, String>() {
                    @Override
                    public String call(Student student) {
                        return student.name;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("观察者:onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("观察者:onError(Throwable e)  " + e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Logger.d("观察者:onNext(String s) " + s);
                    }
                });

    }

    /**
     * 输出学生的姓名:将每个学生的(姓名)依次发射出去
     */
    private void method11() {
        Observable.from(DataFactory.getData())
                .map(new Func1<Student, String>() {
                    @Override
                    public String call(Student student) {
                        return student.name;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.d("观察者:" + s);
                    }
                });

    }


    /**
     * 9: 引入flatmap()==================================================
     * 需要:输出每一个学生所有选修的课程
     * 嵌套循环的RxJava解决方案
     * 输出每一个学生选修的课程
     */
    private void method12() {

        //1:被观察者

        //2:被观察者被观察者订阅

        //3:观察者

        Observable.from(DataFactory.getData())
                .subscribe(new Subscriber<Student>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("观察者:onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("观察者:onError(Throwable e)" + e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        ArrayList<Course> courses = student.courses;
                        for (Course course : courses) {
                            Logger.d("观察者:" + course.name);
                        }
                    }
                });

    }

    /**
     * 需要:输出每一个学生选修的课程
     * 嵌套循环的RxJava解决方案
     * Student->ArrayList<Course>
     */
    private void method13() {

        Observable.from(DataFactory.getData())

                .map(new Func1<Student, ArrayList<Course>>() {
                    @Override
                    public ArrayList<Course> call(Student student) {
                        return student.courses;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Course>>() {
                    @Override
                    public void call(ArrayList<Course> courses) {
                        for (int i = 0; i < courses.size(); i++) {
                            Logger.d("观察者:" + courses.get(i).name);
                        }
                    }
                });
    }

    /**
     * flatMap()的使用
     * 需要:输出每一个学生选修的课程
     * 嵌套循环的RxJava解决方案
     * Student -> ArrayList<Course> -> Observable<Course> ->
     */
    private void method14() {

        //1:被观察者

        //2:数据转换

        //3:事件产生的线程。

        //4:事件消费的线程。

        //5:被观察者被观察者订阅

        //6:观察者

        // Student->Course
        Observable.from(DataFactory.getData())
                .flatMap(new Func1<Student, Observable<Course>>() {
                    @Override
                    public Observable<Course> call(Student student) {
                        return Observable.from(student.courses);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Course>() {
                    @Override
                    public void call(Course course) {
                        Logger.d("观察者:" + course.name);
                    }
                });
    }


    /**
     * 10: RxBinding的引入==================================================
     * 需要防止快速连续点击,短时间内连续点击.
     */
    private void method15() {

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ClickUtils.isFastDoubleClick()) {
                            ToastUtil.getInstance().showToast(RxDemoActivity.this, "点击过快啦");
                            return;
                        }
                        ToastUtil.getInstance().showToast(RxDemoActivity.this, "匿名内部类实现click");
                    }
                });
            }
        });


    }

    /**
     * RxBinding 是 Jake Wharton 的一个开源库，它提供了一套在 Android 平台上的基于 RxJava 的 Binding API。
     * 所谓 Binding，就是类似设置 OnClickListener 、设置 TextWatcher 这样的注册绑定对象的 API。
     * 举个设置点击监听的例子。使用 RxBinding ，可以把事件监听用这样的方法来设置：
     * throttleFirst() ，用于去抖动，也就是消除手抖导致的快速连环点击：
     */
    private void method16() {
        RxView.clicks(mImageView)
                // 500ms,第一次点击后,500ms内点击无效,500ms后点击才会响应
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(RxDemoActivity.this, "click", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * RxBinding
     */
    private void method17() {
        RxView.longClicks(mImageView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(RxDemoActivity.this, "long click", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * EditText,每隔500ms,去响应变化
     */
    private void method18() {
        mSearchEditText.setVisibility(View.VISIBLE);
        RxTextView.textChangeEvents(mSearchEditText)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TextViewTextChangeEvent>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                        String changedMessage = textViewTextChangeEvent.text().toString();
                        Logger.d(TAG, changedMessage);
                        if (!TextUtils.isEmpty(changedMessage)) {
                            ToastUtil.getInstance().showToast(RxDemoActivity.this, changedMessage);
                        }
                    }
                });
    }


    /**
     * 11: 操作符==================================================
     * Range操作符根据出入的初始值n和数目m发射一系列大于等于n的m个值
     * 其使用也非常方便，仅仅制定初始值和数目就可以了，不用自己去实现对Subscriber的调用。
     * 例如:实现:输出1,2,3,4,5
     */
    private void method19() {
        Observable.range(1, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Logger.d(integer.toString() + "");
                    }
                });
    }

    /**
     * 12: RxJava + Retrofit==================================================
     * 使用Retrofit网络库,获取suyimin的GitHub个人信息
     */
    private void method21() {

        mProgressBar.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);
        mResultTextView.setVisibility(View.VISIBLE);
        mResultTextView.setText("");
        Call call = RetrofitService.getInstance().createService(GitHubApi.class).getUser("suyimin");

        //asynchronous
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = (User) response.body();

                if (user == null) {
                    //404 or the response cannot be converted to User.
                    ResponseBody responseBody = response.errorBody();
                    if (responseBody != null) {
                        try {
                            Logger.d("responseBody = " + responseBody.string());
                            mResultTextView.setText("responseBody = " + responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Logger.d("responseBody = null");
                        mResultTextView.setText("responseBody = null");
                    }
                } else {
                    //200
                    String message = "Github Name :" + user.name + "\nWebsite :" + user.blog + "\nCompany Name :" + user.company;
                    ToastUtil.getInstance().showToast(RxDemoActivity.this, message);
                    Logger.d(message);
                    mResultTextView.setText(message);
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Logger.d("t = " + t.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 使用Retrofit网络库,同时使用RxJava 获取suyimin的GitHub个人信息
     */
    private void method22() {
        //1:被观察者,数据源
        //2:观察者
        //3:订阅,被观察者 被 观察者订阅

        Observable<User> observable = RetrofitService.getInstance().createService(GitHubApi.class).getUserObservable("suyimin");
        observable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mResultTextView.setText("");
                        mImageView.setVisibility(View.GONE);
                        mResultTextView.setVisibility(View.VISIBLE);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("onCompleted()");
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RxDemoActivity.this, "onCompleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("onError()=>" + e.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                        Logger.d("onNext()");
                        String message = "Github Name :" + user.name + "\nWebsite :" + user.blog + "\nCompany Name :" + user.company;
                        Toast.makeText(RxDemoActivity.this, "onNext", Toast.LENGTH_SHORT).show();
                        Logger.d(message);
                        mResultTextView.setText(user.toString());
                    }
                });
    }


    private ArrayAdapter<String> mAdapter;
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private ListView mResultListView;


    /**
     * 使用Retrofit网络库,同时使用RxJava 获取square的公司的retrofit项目的贡献者
     */
    private void method23() {

        mImageView.setVisibility(View.GONE);
        mResultTextView.setVisibility(View.GONE);

        mResultListView = (ListView) findViewById(R.id.lv_list);
        mAdapter = new ArrayAdapter<>(RxDemoActivity.this, R.layout.item_log, R.id.item_log, new ArrayList<String>());
        mResultListView.setAdapter(mAdapter);

        mSubscription.add(RetrofitService.getInstance().createService(GitHubApi.class).getContributorsObservable("square", "retrofit")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                })
                .subscribe(new Observer<List<Contributor>>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("Retrofit call 1 completed");
                        mProgressBar.setVisibility(View.GONE);
                        mResultListView.setVisibility(View.VISIBLE);
                        ToastUtil.getInstance().showToast(RxDemoActivity.this, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressBar.setVisibility(View.GONE);
                        Logger.e(e.getMessage() + " woops we got an error while getting the list of contributors");
                        ToastUtil.getInstance().showToast(RxDemoActivity.this, "onError");
                    }

                    @Override
                    public void onNext(List<Contributor> contributors) {
                        ToastUtil.getInstance().showToast(RxDemoActivity.this, "onNext");
                        for (Contributor c : contributors) {
                            mAdapter.add(String.format("%s has made %d contributions to %s",
                                    c.login,
                                    c.contributions,
                                    "retrofit"));

                            Logger.d(String.format("%s has made %d contributions to %s",
                                    c.login,
                                    c.contributions,
                                    "retrofit"));
                        }
                    }
                }));
    }

    /**
     * 3秒内点击次数>=5， Toast提示
     */
    private void method20() {

        final int COUNT = 5;
        final int TIME_ALL = 3000;
        final ArrayList<Long> timeList = new ArrayList<>();
        final ArrayList<Long> allList = new ArrayList<>();

        RxView.clicks(findViewById(R.id.iv_image))
                .map(new Func1<Void, Long>() {
                    @Override
                    public Long call(Void aVoid) {
                        return System.currentTimeMillis();
                    }
                })
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long nowTime) {
                        allList.add(nowTime);
                        timeList.add(nowTime);

                        boolean isOver = false;
                        Log.d(TAG, "timeList.size():" + timeList.size());
                        if (timeList.size() >= COUNT) {

                            if (nowTime - timeList.get(0) < TIME_ALL) {
                                isOver = true;
                            } else {
                                isOver = false;
                            }
                            timeList.clear();
                        }
                        return isOver;
                    }
                }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(RxDemoActivity.this, "3秒内点击超过了" + allList.size(), Toast.LENGTH_SHORT).show();
                    allList.clear();
                }
            }
        });
    }


    /**
     * 测试这些每个知识点的功能
     *
     * @param number
     */
    private void testFuncation(int number) {
        switch (number) {
            case 0: {
                method0();
                break;
            }

            case 1: {
                method1();
                break;
            }

            case 2: {
                method2();
                break;
            }

            case 3: {
                method3();
                break;
            }

            case 4: {
                method4();
                break;
            }

            case 5: {
                method5();
                break;
            }

            case 6: {
                method6();
                break;
            }

            case 7: {
                method7();
                break;
            }

            case 8: {
                method8();
                break;
            }

            case 9: {
                method9();
                break;
            }

            case 10: {
                method10();
                break;
            }

            case 11: {
                method11();
                break;
            }

            case 12: {
                method12();
                break;
            }

            case 13: {
                method13();
                break;
            }

            case 14: {
                method14();
                break;
            }

            case 15: {
                method7();
                method15();
                break;
            }
            case 16: {
                method7();
                method16();
                break;
            }

            case 17: {
                method7();
                method17();
                break;
            }

            case 18: {
                method18();
                break;
            }


            case 19: {
                method19();
                break;
            }

            case 20: {
                method7();
                method20();
                break;
            }

            case 21: {
                method21();
                break;
            }


            case 22: {
                method22();
                break;
            }

            case 23: {
                method23();
                break;
            }

            case 24: {
                miZhiSuoJinAndNestedLoopAndCallbackHell();
                break;
            }

            case 25: {
                rxJavaSolveMiZhiSuoJinAndNestedLoopAndCallbackHell();
                break;
            }
            default: {

                break;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mSubscription);
    }

    @Override
    public void onPause() {
        super.onPause();

        RxUtils.unsubscribeIfNotNull(mSubscription);
    }

    private boolean mGoToRecycleImageView = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //rl_root_layout 这个是根布局的id
        unBingListener(findViewById(R.id.rl_root_layout));
        unBindDrawables(findViewById(R.id.rl_root_layout));
        recycleImageView();
    }


    private void recycleImageView() {
        //回收ImageView占用的图像内存
        if (mGoToRecycleImageView) {
            Logger.d("onDestroy()> RecycleBitmap.recycleImageView(mImageView)");
            RecycleBitmap.recycleImageView(mImageView);
            mImageView.setImageBitmap(null);
        }

        if (mManyBitmapSuperposition != null && !mManyBitmapSuperposition.isRecycled()) {
            mManyBitmapSuperposition.recycle();
            mManyBitmapSuperposition = null;
        }

        if (mCanvas != null) {
            //清屏
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mCanvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            mCanvas = null;
        }

    }

    /**
     * 在Activity onDestory时候从view的rootview开始，
     * 递归释放所有子view涉及的图片，背景，DrawingCache，监听器等等资源，
     * 让Activity成为一个不占资源的空壳，泄露了也不会导致图片资源被持有。
     *
     * @param view:the root view of the layout
     */
    private void unBindDrawables(View view) {
        if (view != null) {
            try {
                Drawable drawable = view.getBackground();
                if (drawable != null) {
                    drawable.setCallback(null);
                } else {
                }
                if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    int viewGroupChildCount = viewGroup.getChildCount();
                    for (int j = 0; j < viewGroupChildCount; j++) {
                        unBindDrawables(viewGroup.getChildAt(j));
                    }
                    viewGroup.removeAllViews();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Remove an onclick listener
     *
     * @param view
     */
    private void unBingListener(View view) {
        if (view != null) {
            try {
                if (view.hasOnClickListeners()) {
                    view.setOnClickListener(null);
                }
                if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    int viewGroupChildCount = viewGroup.getChildCount();
                    for (int i = 0; i < viewGroupChildCount; i++) {
                        unBingListener(viewGroup.getChildAt(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 实现的功能:获取assets文件夹下所有文件夹中的jpg图片,并且将所有的图片画到一个ImageView上
     * 不使用RxJava的写法-- 谜之缩进--回调地狱
     * 思路:需要以下6个步骤完成
     * 1:遍历获取assets文件夹下所有的文件夹的名称
     * 2:遍历获取获取assets文件夹下某个文件夹中所有图片路径的集合
     * 3:过滤掉非JPG格式的图片
     * 4:获取某个路径下图片的bitmap
     * 5:将Bitmap绘制到画布上
     * 6:循环结束后更新UI,给ImageView设置最后绘制完成后的Bitmap,隐藏ProgressBar
     */
    private void miZhiSuoJinAndNestedLoopAndCallbackHell() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
                //1:遍历获取assets文件夹下所有的文件夹的名称
                ArrayList<String> assetsFolderNameList = ImageNameFactory.getAssetImageFolderName();

                for (String folderName : assetsFolderNameList) {

                    //2:遍历获取获取assets文件夹下某个文件夹中所有图片路径的集合
                    ArrayList<String> imagePathList = ImageUtils.getAssetsImageNamePathList(getApplicationContext(), folderName);

                    for (final String imagePathName : imagePathList) {
                        //3:过滤掉非JPG格式的图片
                        if (imagePathName.endsWith(JPG)) {

                            //4:获取某个路径下图片的bitmap
                            final Bitmap bitmap = ImageUtils.getImageBitmapFromAssetsFolderThroughImagePathName(getApplicationContext(), imagePathName, Constant.IMAGE_WITH, Constant.IMAGE_HEIGHT);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Logger.d(mCounter + ":" + imagePathName);

                                    //5:将Bitmap绘制到画布上
                                    createSingleImageFromMultipleImages(bitmap, mCounter);
                                    mCounter++;

                                }
                            });
                        }
                    }
                }


                //6:循环结束后更新UI,给ImageView设置最后绘制完成后的Bitmap,隐藏ProgressBar
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImageBitmap(mManyBitmapSuperposition);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });

            }
        }).start();
    }


    /**
     * 就是循环在画布上画图,呈现一种整齐的线性分布:像方格
     * 所有绘制都绘制到了创建Canvas时传入的Bitmap上面
     * <p>
     * 实现思路:
     * 1:产生和手机屏幕尺寸同样大小的Bitmap
     * 2:以Bitmap对象创建一个画布，将内容都绘制在Bitmap上,这个Bitmap用来存储所有绘制在Canvas上的像素信息.
     * 3:这里将所有图片压缩成了相同的尺寸均为正方形图(64px*64px)
     * 4:计算获取绘制每个Bitmap的坐标,距离屏幕左边和上边的距离,距离左边的距离不断自增,距离顶部的距离循环自增
     * 5:将Bitmap画到指定坐标
     *
     * @param bitmap:每张图片对应的Bitamp
     * @param mCounter:一个自增的整数从0开始
     */
    private void createSingleImageFromMultipleImages(Bitmap bitmap, int mCounter) {
        if (mCounter == 0) {
            //1:产生和手机屏幕尺寸同样大小的Bitmap
            mManyBitmapSuperposition = Bitmap.createBitmap(DeviceInfo.screenWidthForPortrait, DeviceInfo.screenHeightForPortrait, bitmap.getConfig());

            //2:以Bitmap对象创建一个画布，则将内容都绘制在Bitmap上
            mCanvas = new Canvas(mManyBitmapSuperposition);
        }
        if (mCanvas != null) {
            int left;//距离左边的距离
            int top;//距离顶部的距离

            //3:这里将所有图片压缩成了相同的尺寸均为正方形图(64px*64px)
            int imageWidth = Constant.IMAGE_WITH;
            int imageHeight = Constant.IMAGE_HEIGHT;
            int number = DeviceInfo.screenHeightForPortrait / imageHeight;//手机竖屏模式下,垂直方向上绘制图片的个数

            //4:计算获取绘制每个Bitmap的坐标,距离屏幕左边和上边的距离,距离左边的距离不断自增,距离顶部的距离循环自增
            if (mCounter >= (mCounter / number) * number && mCounter < (((mCounter / number) + 1) * number)) {//[0,number)
                left = (mCounter / number) * imageWidth;
                top = (mCounter % number) * imageHeight;
                // Log.d(TAG,""+mCounter+" left="+left+" top="+top);

                //5:将Bitmap画到指定坐标
                mCanvas.drawBitmap(bitmap, left, top, null);
            }
        }
    }

    /**
     * RxJava的实现--链式调用--十分简洁
     */
    private void rxJavaSolveMiZhiSuoJinAndNestedLoopAndCallbackHell() {
        //1:被观察者:

        //2:数据转换

        //3:设置事件的产生发生在IO线程

        //4:设置事件的消费发生在主线程

        //5:观察者

        //6:订阅:被观察者被观察者订阅
        mGoToRecycleImageView = false;
        Observable.from(ImageNameFactory.getAssetImageFolderName())
                //assets下一个文件夹的名称,assets下一个文件夹中一张图片的路径
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String folderName) {
                        return Observable.from(ImageUtils.getAssetsImageNamePathList(getApplicationContext(), folderName));
                    }
                })
                //过滤,筛选出jpg图片
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String imagePathNameAll) {
                        return imagePathNameAll.endsWith(JPG);
                    }
                })
                //将图片路径转换为对应图片的Bitmap
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String imagePathName) {
                        return ImageUtils.getImageBitmapFromAssetsFolderThroughImagePathName(getApplicationContext(), imagePathName, Constant.IMAGE_WITH, Constant.IMAGE_HEIGHT);
                    }
                })
                .map(new Func1<Bitmap, Void>() {
                    @Override
                    public Void call(Bitmap bitmap) {
                        createSingleImageFromMultipleImages(bitmap, mCounter);
                        mCounter++;
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())//设置事件的产生发生在IO线程
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//设置事件的消费发生在主线程
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        mImageView.setImageBitmap(mManyBitmapSuperposition);
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Toast.makeText(RxDemoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }
}
