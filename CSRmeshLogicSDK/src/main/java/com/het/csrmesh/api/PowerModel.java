/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

import android.os.Bundle;

import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.PowerModelApi;
import com.csr.csrmesh2.PowerState;
import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshRequestEvent;


/**
 * The Power Model is used to control the turning on and off of power for a device.
 * A device may have multiple power states: On, Off, and optionally Standby.
 */
public class PowerModel {

    public static int getState(int deviceId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.POWER_GET_STATE, data));
        return id;
    }

    public static int setState(int deviceId, PowerState state, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putSerializable(MeshConstants.EXTRA_POWER_STATE, state);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.POWER_SET_STATE, data));
        return id;
    }

    public static int toggleState(int deviceId, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.POWER_TOGGLE_STATE, data));
        return id;
    }

    static void handleRequest(MeshRequestEvent event) {
        int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
        boolean ack = event.data.getBoolean(MeshConstants.EXTRA_ACKNOWLEDGED);
        int libId = 0;
        switch (event.what) {
            case POWER_GET_STATE:
                libId = PowerModelApi.getState(deviceId);
                break;
            case POWER_SET_STATE:
                PowerState state = (PowerState) event.data.get(MeshConstants.EXTRA_POWER_STATE);
                libId = PowerModelApi.setState(deviceId, state, ack);
                break;
            case POWER_TOGGLE_STATE:
                libId = PowerModelApi.toggleState(deviceId, ack);
                break;
        }
        if (libId != 0) {
            int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
