/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

import android.os.Bundle;

import com.csr.csrmesh2.ActuatorModelApi;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.SensorValue;
import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshRequestEvent;

/**
 * The Actuator Model sets values on other devices in the mesh network. These
 * values are either based on the physical property of something, for example
 * the current air temperature, or based on an abstract value for such a
 * physical property.
 *
 */
public class ActuatorModel {

    public static int setValue(final int deviceId, SensorValue value1, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putParcelable(MeshConstants.EXTRA_SENSOR_VALUE1, value1);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ACTUATOR_SET_VALUE, data));
        return id;
    }

    public static int getTypes(final int deviceId, int firstType) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE, firstType);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ACTUATOR_GET_TYPES, data));
        return id;
    }


    static void handleRequest(MeshRequestEvent event) {

        int libId = -1;
        int internalId;
        int deviceId;
        SensorValue value1;
        int firstType;
        boolean acknowledged;

        switch (event.what) {

            case ACTUATOR_SET_VALUE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                value1 = event.data.getParcelable(MeshConstants.EXTRA_SENSOR_VALUE1);
                acknowledged = event.data.getBoolean(MeshConstants.EXTRA_ACKNOWLEDGED);
                // Do API call
                libId = ActuatorModelApi.setValue(deviceId, value1, acknowledged);
                break;

            case ACTUATOR_GET_TYPES:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                firstType = event.data.getInt(MeshConstants.EXTRA_SENSOR_TYPE);
                // Do API call
                libId = ActuatorModelApi.getTypes(deviceId, SensorValue.SensorType.values()[firstType]);
                break;

            default:
                break;
        }
        internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
        MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
    }
}
