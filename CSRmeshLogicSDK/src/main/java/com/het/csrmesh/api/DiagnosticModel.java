/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

import android.os.Bundle;

import com.csr.csrmesh2.MeshConstants;
import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshRequestEvent;

/**
 * This model permits the collection of local diagnostics for the purpose of accumulation of this information for further
 * global data analysis. The diagnostic model is only about reporting information
 */
public class DiagnosticModel {

    public static int getStats(int deviceId, int flag) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DIAGNOSTIC_FLAG, flag);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.DIAGNOSTIC_GET_STATS, data));
        return id;
    }



    static void handleRequest(MeshRequestEvent event) {
        int libId = -1;
        int internalId;
        int flag ,deviceId;

        switch (event.what) {
            case DIAGNOSTIC_GET_STATS:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                flag = event.data.getInt(MeshConstants.EXTRA_DIAGNOSTIC_FLAG);
                // Do API call
                libId = DiagnosticModel.getStats(deviceId, flag);
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
