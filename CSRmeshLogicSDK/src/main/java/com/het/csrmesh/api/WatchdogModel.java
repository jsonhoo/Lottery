/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

import android.os.Bundle;

import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.WatchdogModelApi;
import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshRequestEvent;

/**
 /**
 * The purpose of the Watchdog Model is to ensure that devices that generally aren’t listening for mesh messages will
 * listen for a short period of time after they send a watchdog message; and so that devices can transmit random
 * messages into the network to simulate normal activity and therefore protect against traffic analysis attacks.
 *
 */
public class WatchdogModel {

    public static int setMessage(int deviceId, int rspSize, int[] randomData) {

        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_RSP_SIZE, rspSize);
        data.putIntArray(MeshConstants.EXTRA_RANDOM_DATA, randomData);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.WATCHDOG_MESSAGE, data));
        return id;
    }

    public static int setInterval(int deviceId,int interval, int activeAfterTime) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_INTERVAL, interval);
        data.putInt(MeshConstants.EXTRA_ACTIVE_AFTER_TIME, activeAfterTime);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.WATCHDOG_SET_INTERVAL, data));
        return id;
    }


    static void handleRequest(MeshRequestEvent event) {
        int libId = -1;
        int internalId;
        int deviceId, rspSize,interval,activeAfterTime;
        byte [] randomData;

        switch (event.what) {
            case WATCHDOG_MESSAGE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                rspSize = event.data.getInt(MeshConstants.EXTRA_RSP_SIZE);
                randomData = event.data.getByteArray(MeshConstants.EXTRA_RANDOM_DATA);
                // Do API call
                libId = WatchdogModelApi.setMessage(deviceId, rspSize,randomData);
                break;

            case WATCHDOG_SET_INTERVAL:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                interval = event.data.getInt(MeshConstants.EXTRA_INTERVAL);
                activeAfterTime = event.data.getInt(MeshConstants.EXTRA_ACTIVE_AFTER_TIME);
                // Do API call
                libId = WatchdogModel.setInterval(deviceId, interval, activeAfterTime);
                break;

            default:
                break;
        }

        if (libId != -1) {
            internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
