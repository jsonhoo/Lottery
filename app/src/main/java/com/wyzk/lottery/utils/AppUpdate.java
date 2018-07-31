package com.wyzk.lottery.utils;

import android.app.Activity;
import android.support.v7.app.AlertDialog;

import com.wyzk.lottery.model.AppUpdateBean;
import com.wyzk.lottery.network.Network;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import util.UpdateAppUtils;


public class AppUpdate {
    static AlertDialog myDialog = null;

    public static void checkUpdateVersion(final Activity activity) {
        if (myDialog == null) {
            myDialog = new AlertDialog.Builder(activity).create();
        }

        Network.getNetworkInstance().getUserApi()
                .update("http://120.77.252.48/file/version.json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AppUpdateBean>() {
                    @Override
                    public void accept(AppUpdateBean appBean) throws Exception {
                        UpdateAppUtils.from(activity)
                                .serverVersionCode(appBean.getVerCode())  //服务器versionCode
                                .serverVersionName(appBean.getVerName()) //服务器versionName
                                .apkPath(appBean.getDownloadUrl()) //最新apk下载地址
                                .update();
                    }
                });
    }

}