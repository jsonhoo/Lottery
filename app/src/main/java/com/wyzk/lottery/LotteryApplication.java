package com.wyzk.lottery;

import android.app.Application;

import com.crashsdk.CrashManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.uuxia.email.Email;

public class LotteryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        CrashManager.setMail(new Email());
        CrashManager.install(this);
    }
}
