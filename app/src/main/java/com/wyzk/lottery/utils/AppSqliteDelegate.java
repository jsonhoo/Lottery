package com.wyzk.lottery.utils;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/
public class AppSqliteDelegate {
    private static Application app = null;
    private static Set<Class> activeAndroidModelClasses = new HashSet();
    public static final int DB_VERSION = 5;
    public static final String DB_NAME = "het.db";

    public AppSqliteDelegate() {
    }

    public static Context getAppContext() {
        return app == null?null:app.getApplicationContext();
    }

    public static void initActiveAndroid(Application application) {
        initActiveAndroid(application, "het.db", 5);
    }

    public static void initActiveAndroidRelative(Application application, int version) {
        initActiveAndroid(application, "het.db", 5 + version);
    }

    public static void initActiveAndroid(Application application, int version) {
        int dbVersion = 5;
        if(version > 5) {
            dbVersion = version;
        }

        initActiveAndroid(application, "het.db", dbVersion);
    }

    public static void addModelClass(Class... calsses) {
        if(calsses != null) {
            Collections.addAll(activeAndroidModelClasses, calsses);
        }

    }

    public static void initActiveAndroid(Application application, String dataBaseName, int version) {
        app = application;
        int dbVersion = 5;
        if(version > 5) {
            dbVersion = version;
        }

        Configuration.Builder configuration = new Configuration.Builder(app.getApplicationContext());
        configuration.setDatabaseName(dataBaseName);
        configuration.setDatabaseVersion(dbVersion);
        if(activeAndroidModelClasses != null && activeAndroidModelClasses.size() > 0) {
            Class[] classes = new Class[activeAndroidModelClasses.size()];
            activeAndroidModelClasses.toArray(classes);
            configuration.setModelClasses(classes);
        }

        ActiveAndroid.initialize(configuration.create());
    }

    public static void onTerminate() {
        ActiveAndroid.dispose();
    }
}
