
package com.het.csrmesh.utils;

import android.util.Log;

import com.het.csrmesh.api.MeshLibraryManager;
import com.het.csrmesh.model.listeners.LogLevel;


public class LocalLog {

    public static void e(String i, String v) {
        if (MeshLibraryManager.logLevel.ordinal() >= LogLevel.ERROR.ordinal()) {
            Log.e(i, v);
        }
    }

    public static void w(String i, String v) {
        if (MeshLibraryManager.logLevel.ordinal() >= LogLevel.WARNING.ordinal()) {
            Log.w(i, v);
        }
    }

    public static void i(String i, String v) {
        if (MeshLibraryManager.logLevel.ordinal() >= LogLevel.INFO.ordinal()) {
            Log.i(i, v);
        }
    }

    public static void v(String i, String v) {
        if (MeshLibraryManager.logLevel.ordinal() >= LogLevel.VERBOSE.ordinal()) {
            Log.v(i, v);
        }

    }

    public static void d(String i, String v) {
        if (MeshLibraryManager.logLevel.ordinal() >= LogLevel.DEBUG.ordinal()) {
            Log.d(i, v);
        }
    }

}
