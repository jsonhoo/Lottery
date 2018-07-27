package com.wyzk.lottery.utils;

/**
 * Created by uuxia-mac on 2017/12/30.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;
import java.net.InetAddress;
import java.util.List;

@SuppressLint({"NewApi"})
public class AppTools {
    private AppTools() {
    }

    public static void startForwardActivity(Activity context, Class<?> forwardActivity) {
        startForwardActivity(context, forwardActivity, Boolean.valueOf(false));
    }

    public static void startForwardActivity(Activity context, Class<?> forwardActivity, Boolean isFinish) {
        Intent intent = new Intent(context, forwardActivity);
        context.startActivity(intent);
        if (isFinish.booleanValue()) {
            context.finish();
        }

    }

    public static void startForwardActivity(Activity context, Class<?> forwardActivity, Bundle bundle, Boolean isFinish, int animin, int animout) {
        Intent intent = new Intent(context, forwardActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
        if (isFinish.booleanValue()) {
            context.finish();
        }

        try {
            context.overridePendingTransition(animin, animout);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

    }

    public static void startForwardActivity(Activity context, Class<?> forwardActivity, Bundle bundle, Boolean isFinish) {
        Intent intent = new Intent(context, forwardActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
        if (isFinish.booleanValue()) {
            context.finish();
        }

    }

    public static void startForResultActivity(Activity context, Class<?> forwardActivity, int requestCode, Bundle bundle, Boolean isFinish) {
        Intent intent = new Intent(context, forwardActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivityForResult(intent, requestCode);
        if (isFinish.booleanValue()) {
            context.finish();
        }

    }

    public static void startForResultActivity(Activity context, Class<?> forwardActivity, int requestCode, Bundle bundle, Boolean isFinish, int animin, int animout) {
        Intent intent = new Intent(context, forwardActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivityForResult(intent, requestCode);
        if (isFinish.booleanValue()) {
            context.finish();
        }

        try {
            context.overridePendingTransition(animin, animout);
        } catch (Exception var9) {
            var9.printStackTrace();
        }

    }

    public static void startForResultActivity2(Activity context, Class<?> forwardActivity, int requestCode, Bundle bundle, Boolean isFinish) {
        Intent intent = new Intent(context, forwardActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivityForResult(intent, requestCode);
        if (isFinish.booleanValue()) {
            context.finish();
        }

    }

    public static String getVersionName(Context context) {
        String version;
        try {
            PackageManager e = context.getPackageManager();
            PackageInfo packageInfo = e.getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (Exception var4) {
            var4.printStackTrace();
            version = "";
        }

        return version;
    }

    public static int getVersionCode(Context context) {
        int versionCode;
        try {
            PackageManager e = context.getPackageManager();
            PackageInfo packageInfo = e.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Exception var4) {
            var4.printStackTrace();
            versionCode = 999;
        }

        return versionCode;
    }

    public static String getLocalMacAddress(Activity activity) {
        WifiManager wifi = (WifiManager) activity.getSystemService("wifi");
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        return mac;
    }

    public static String getLocalIpAddress(Activity activity) {
        try {
            WifiManager e = (WifiManager) activity.getSystemService("wifi");
            WifiInfo info = e.getConnectionInfo();
            int ipAddress = info.getIpAddress();
            String Ipv4Address = InetAddress.getByName(String.format("%d.%d.%d.%d", new Object[]{Integer.valueOf(ipAddress & 255), Integer.valueOf(ipAddress >> 8 & 255), Integer.valueOf(ipAddress >> 16 & 255), Integer.valueOf(ipAddress >> 24 & 255)})).getHostAddress().toString();
            return Ipv4Address;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService("activity");
        List runningTaskInfos = manager.getRunningTasks(1);
        return runningTaskInfos != null ? ((RunningTaskInfo) runningTaskInfos.get(0)).topActivity.getClassName().toString() : "";
    }

    public static boolean isTopActivity(Context context, String className) {
        String topactivity = getTopActivity(context);
        return className.equals(topactivity);
    }

    public static void setFullScreen(Activity activity, boolean isFull) {
        Window window = activity.getWindow();
        LayoutParams params = window.getAttributes();
        if (isFull) {
            params.flags |= 1024;
            window.setAttributes(params);
            window.addFlags(512);
        } else {
            params.flags &= -1025;
            window.setAttributes(params);
            window.clearFlags(512);
        }

    }

    public static void hideTitleBar(Activity activity) {
        activity.requestWindowFeature(1);
    }

    public static void setScreenVertical(Activity activity) {
        activity.setRequestedOrientation(1);
    }

    public static void setScreenHorizontal(Activity activity) {
        activity.setRequestedOrientation(0);
    }

    public static void adjustSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(16);
    }

    public static void install(Activity activity, File apkFile) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }

    public static void hideSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(2);
    }

    public static void hideSoftInput(Activity activity, IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) activity.getSystemService("input_method");
            im.hideSoftInputFromWindow(token, 2);
        }

    }

    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && v instanceof EditText) {
            int[] l = new int[]{0, 0};
            v.getLocationInWindow(l);
            int left = l[0];
            int top = l[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return event.getX() <= (float) left || event.getX() >= (float) right || event.getY() <= (float) top || event.getY() >= (float) bottom;
        } else {
            return false;
        }
    }

    public static boolean isPackageExists(Context context, String packageName) {
        if (null != packageName && !"".equals(packageName)) {
            try {
                ApplicationInfo e = context.getPackageManager().getApplicationInfo(packageName, 8192);
                return null != e;
            } catch (NameNotFoundException var3) {
                return false;
            }
        } else {
            throw new IllegalArgumentException("Package name cannot be null or empty !");
        }
    }

    public static String getAppName(Context context) {
        try {
            PackageManager e = context.getPackageManager();
            PackageInfo packageInfo = e.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static Object getMetaDataAsObject(Activity context, String key) {
        ActivityInfo info = getActivityInfo(context);
        return info == null ? null : info.metaData.get(key);
    }

    public static String getMetaDataAsString(Activity context, String key) {
        ActivityInfo info = getActivityInfo(context);
        return info == null ? null : info.metaData.getString(key);
    }

    public static int getMetaDataAsInt(Activity context, String key) {
        ActivityInfo info = getActivityInfo(context);
        return (info == null ? null : Integer.valueOf(info.metaData.getInt(key))).intValue();
    }

    public static boolean getMetaDataAsBoolean(Activity context, String key) {
        ActivityInfo info = getActivityInfo(context);
        return (info == null ? null : Boolean.valueOf(info.metaData.getBoolean(key))).booleanValue();
    }

    private static ActivityInfo getActivityInfo(Activity context) {
        PackageManager packageManager = context.getPackageManager();

        try {
            return packageManager.getActivityInfo(context.getComponentName(), 128);
        } catch (NameNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }
}