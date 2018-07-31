package com.wyzk.lottery.ui;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wyzk.lottery.R;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.utils.ACache;
import com.wyzk.lottery.utils.BuildManager;

import io.reactivex.functions.Consumer;

/**
 * 闪屏页面
 */
public class SplashActivity extends LotteryBaseActivity {

    private final static int ANIMATION_DURATION = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        BuildManager.setStatusTransOther(this);
        getPermission();
    }

    /**
     * 获取权限
     */
    private void getPermission() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rxPermissions.request(Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean grant) throws Exception {
                            if (!grant) {
                                SplashActivity.this.finish();
                            } else {
                                //延迟2S跳转
                                SplashActivity.this.loadAnimation();
                            }
                        }
                    });
        }else {
            SplashActivity.this.loadAnimation();
        }

    }

    private void loadAnimation() {
        RelativeLayout splash = (RelativeLayout) findViewById(R.id.splash);

        AlphaAnimation aa = new AlphaAnimation(0.8f, 1);
        aa.setDuration(ANIMATION_DURATION);
        aa.setInterpolator(new LinearInterpolator());
        aa.setFillAfter(true);

        splash.startAnimation(aa);

        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String token = ACache.get(SplashActivity.this).getAsString(IConst.TOKEN);
                Log.d("token", "" + token);
                if (TextUtils.isEmpty(token)) {
                    toActivity(LoginActivity.class);
                } else {
                    toActivity(MainActivity.class);
                }
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
