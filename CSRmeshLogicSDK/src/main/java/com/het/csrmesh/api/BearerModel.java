/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

import android.os.Bundle;

import com.csr.csrmesh2.BearerModelApi;
import com.csr.csrmesh2.MeshConstants;
import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshRequestEvent;

/**
 * The Bearer Model is used to control which bearers a given device has enabled and which bearers will be used to relay.
 */
public class BearerModel {


    public static int setState(final int deviceId, int bearerRelayActive, int bearerEnabled, int promiscuous) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_RELAY_ENABLED, bearerRelayActive);
        data.putInt(MeshConstants.EXTRA_BEARER_ENABLED, bearerEnabled);
        data.putInt(MeshConstants.EXTRA_PROMISCUOUS, promiscuous);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.BEARER_SET_STATE, data));
        return id;
    }


    public static int getState(final int deviceId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.BEARER_GET_STATE, data));
        return id;
    }


    static void handleRequest(MeshRequestEvent event) {

        int libId = -1;
        int internalId;
        int deviceId;
        int bearerRelayActive;
        int bearerEnabled;
        int promiscuous;

        switch (event.what) {

            case BEARER_SET_STATE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                bearerRelayActive = event.data.getInt(MeshConstants.EXTRA_RELAY_ENABLED);
                bearerEnabled = event.data.getInt(MeshConstants.EXTRA_BEARER_ENABLED);
                promiscuous = event.data.getInt(MeshConstants.EXTRA_PROMISCUOUS);
                // Do API call
                libId = BearerModelApi.setState(deviceId, bearerRelayActive, bearerEnabled, promiscuous);
                break;

            case BEARER_GET_STATE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                // Do API call
                libId = BearerModelApi.getState(deviceId);
                break;

            default:
                break;
        }
        internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
        MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
    }
}
