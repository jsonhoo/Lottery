package com.het.csrmesh;

import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class App {

    public static Bus bus;

    private App() {
    }

    public static void init(Context context) {
        if (bus == null) {
            synchronized (App.class) {
                if (bus == null) {
                    bus = new Bus(ThreadEnforcer.ANY);
                }
            }
        }
    }
}
