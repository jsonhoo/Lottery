package com.wyzk.lottery.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


/*
 * Created by hx on 2016-06-24.
 */
public class BuildManager {
    //版本是19之后
    public static boolean isSDKLater19() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
    }

    /**
     * @param activity 上下文
     */
    public static void setStatusTrans2(Activity activity) {
        if (isSDKLater19()) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
        }
    }

    /**
     * @param activity 上下文
     */
    public static void setStatusTransOther(Activity activity) {
        if (isSDKLater19()) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
            //全屏模式
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * @param activity   上下文
     * @param parentType 1 view的父容器是Linearlayout; 2 view的父容器是RelativeLayout
     * @param view       上下文
     */
    public static void setStatusTrans(Activity activity, int parentType, View view) {
        if (isSDKLater19()) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
            //设置沉浸模式时,页面会填充状态栏位置,设置合适的margin值使页面从状态栏下开始布局
            if (parentType == 1) {
                //LinearLayout
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                params.setMargins(0, ScreenUtils.getStatusBarHeight(activity), 0, 0);
            } else if (parentType == 2) {
                //RelativeLayout
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                params.setMargins(0, ScreenUtils.getStatusBarHeight(activity), 0, 0);
            }
        }
    }

}
