/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

import android.os.Bundle;

import com.csr.csrmesh2.FirmwareModelApi;
import com.csr.csrmesh2.MeshConstants;
import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshRequestEvent;


/**
 * Firmware model is used to query firmware version and request firmware update.
 */
public class FirmwareModel {
    public static void setUpdateRequired(int deviceId) {
        Bundle data = new Bundle();
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.FIRMWARE_UPDATE_REQUIRED, data));
    }

    public static int getVersionInfo(int deviceId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.FIRMWARE_GET_VERSION, data));
        return id;
    }

    static void handleRequest(MeshRequestEvent event) {
        int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
        int libId = 0;
        switch (event.what) {
            case FIRMWARE_UPDATE_REQUIRED:
                FirmwareModelApi.setUpdateRequired(deviceId);
                break;
            case FIRMWARE_GET_VERSION:
                libId = FirmwareModelApi.getVersionInfo(deviceId);
                break;
        }
        if (libId != 0) {
            int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
