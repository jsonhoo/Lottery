package com.het.csrmesh.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.het.csrmesh.api.MeshLibraryManager;
import com.het.csrmesh.utils.ConnectionUtils;

import java.lang.ref.WeakReference;

/**
 * BroadcastReceiver which is triggered whenever there is a connection change - it will trigger the
 * SearchSelectedGatewayService to identify which GW should be selected
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    private final String TAG = "ConnectionChangeReceive";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // Skip it if app is not being initiated.
        if (MeshLibraryManager.getInstance() == null || !MeshLibraryManager.getInstance().isServiceAvailable()) {
            return;
        }

        // If we are offline change channel to Bluetooth and launch toast
        int connectionType = ConnectionUtils.getConnectionType(context);
        if (connectionType == ConnectionUtils.TYPE_OFFLINE) {

            MeshLibraryManager.getInstance().setBluetoothChannelEnabled();
            MeshLibraryManager.getInstance().setSelectedGatewayUUID("");
        }
        // Set internet state in MeshLibraryManager
        mIsInternetAvailableHandler = new IsInternetAvailableHandler(this);
        ConnectionUtils.isInternetAvailable(mIsInternetAvailableHandler);
    }


    Handler mIsInternetAvailableHandler;

    private static class IsInternetAvailableHandler extends Handler {
        private final WeakReference<ConnectionChangeReceiver> mParent;

        IsInternetAvailableHandler(ConnectionChangeReceiver connectionChangeReceiver) {
            this.mParent = new WeakReference<>(connectionChangeReceiver);
        }

        @Override
        public void handleMessage(Message msg) {
            boolean isInternetAvailable = msg.getData().getBoolean(ConnectionUtils.INTERNET_AVAILABLE);
            MeshLibraryManager.getInstance().setIsInternetAvailable(isInternetAvailable);
        }
    }

}
