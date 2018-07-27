package com.wyzk.lottery.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.wyzk.lottery.event.NetworkEvent;

import org.greenrobot.eventbus.EventBus;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    private static boolean flag = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (isNetworkAvailable(context) && !flag) {
                flag = true;
                EventBus.getDefault().post(new NetworkEvent(true));
            }
            if (!isNetworkAvailable(context) && flag) {
                flag = false;
                EventBus.getDefault().post(new NetworkEvent(false));
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable();
    }
}
