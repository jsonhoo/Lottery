package com.wyzk.lottery;

import android.app.Application;
import android.content.Context;

import com.crashsdk.CrashManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.uuxia.email.Email;
import com.wyzk.lottery.utils.AppSqliteDelegate;

public class LotteryApplication extends Application {

    public static Bus bus;

    public static LotteryApplication get(Context context) {
        return (LotteryApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        CrashManager.setMail(new Email());
        CrashManager.install(this);

        bus = new Bus(ThreadEnforcer.ANY);

        AppSqliteDelegate.initActiveAndroid(this);
    }
}
