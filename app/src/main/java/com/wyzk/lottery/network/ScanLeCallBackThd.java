package com.wyzk.lottery.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/
public class ScanLeCallBackThd {
    private static final String TAG = ScanLeCallBackThd.class.getSimpleName();
    protected Handler handler = new Handler(Looper.getMainLooper());
    private static final int SCANNING_PERIOD_MS = 10 * 1000;
    private int timeout = SCANNING_PERIOD_MS;
    private Context context;

    public ScanLeCall getScanLeCall() {
        return scanLeCall;
    }

    public void setScanLeCall(ScanLeCall scanLeCall) {
        this.scanLeCall = scanLeCall;
    }

    private ScanLeCall scanLeCall;

    public ScanLeCallBackThd(int time, Context context,ScanLeCall scanLeCall) {
        this.timeout = time;
        this.context = context;
        this.scanLeCall = scanLeCall;
    }


    public void notifyScanStarted() {
        if (this.timeout > 0L) {
            this.removeHandlerMsg();
            AutoScanBindManager.getInstence(context).setDeviceScanEnabled(true);
            this.handler.postDelayed(() -> {
                scanLeCall.onTimeOut();
                AutoScanBindManager.getInstence(context).setDeviceScanEnabled(false);
            }, this.timeout);
        }
    }

    public void removeHandlerMsg() {
        this.handler.removeCallbacksAndMessages((Object) null);
    }
}
