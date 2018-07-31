package com.wyzk.lottery.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class TimeUtils {

    private static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static Disposable subscription;

    public static String getCurrentTimeFormat(long mills) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
        return sdf.format(new Date(mills));
    }

    public static void sub(final CallBack callBack) {
        subscription = Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io()).take(Integer.MAX_VALUE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (callBack != null) {
                            callBack.onNext(TimeUtils.getCurrentTimeFormat(System.currentTimeMillis()));
                        }
                    }
                });
    }


    public static void disSub() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    public interface CallBack {
        void onNext(String str);
    }
}
