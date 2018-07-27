package com.wyzk.lottery.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TimeUtils {

    private static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static Subscription subscription;

    public static String getCurrentTimeFormat(long mills) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
        return sdf.format(new Date(mills));
    }

    public static void sub(final CallBack callBack) {
        subscription = Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io()).take(Integer.MAX_VALUE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long value) {
                        if (callBack != null) {
                            callBack.onNext(TimeUtils.getCurrentTimeFormat(System.currentTimeMillis()));
                        }
                    }
                });
    }


    public static void disSub() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public interface CallBack {
        void onNext(String str);
    }
}
