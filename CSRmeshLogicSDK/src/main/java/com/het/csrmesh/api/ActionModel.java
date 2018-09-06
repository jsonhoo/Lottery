/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.api;

import android.os.Bundle;

import com.csr.csrmesh2.ActionModelApi;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.SensorValue;
import com.csr.csrmesh2.MeshAction;
import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshRequestEvent;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * The Action Model is used to schedule actions to occur at some time in the future.
 *
 */
public class ActionModel {

    public static int setAction(int deviceId, int actionID, MeshAction action, Calendar absoluteTime, int recurringSeconds, int numberOfRepeats) {

        int internalId = MeshLibraryManager.getInstance().getNextRequestId();
        int libId = ActionModelApi.setAction(deviceId, actionID, action, absoluteTime.getTimeInMillis(), recurringSeconds, numberOfRepeats);
        MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        return internalId;
    }

    public static int setValue(final int deviceId, SensorValue value1, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putParcelable(MeshConstants.EXTRA_SENSOR_VALUE1, value1);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ACTION_SET_ACTION, data));
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

    public static int deleteAction(int deviceId, int actionIDs) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_ACTION_IDS, actionIDs);
        App.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ACTION_DELETE_ACTION, data));
        return id;
    }


    static void handleRequest(MeshRequestEvent event) {

        int libId = -1;
        int internalId;
        int deviceId;

        switch (event.what) {

            case ACTION_DELETE_ACTION:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
                int actionIDs = event.data.getInt(MeshConstants.EXTRA_ACTION_IDS);

                ArrayList<Integer> actionIDsArray = new ArrayList<>();
                for(int i=1; i<32; i++) {
                    if((actionIDs & 1) == 1) {
                        actionIDsArray.add(i);
                    }
                }

                int[] arrayValues = new int[actionIDsArray.size()];
                for (int i=0; i<actionIDsArray.size(); i++) {
                    arrayValues[i] = actionIDsArray.get(i);
                }
                libId = ActionModelApi.deleteAction(deviceId, arrayValues);
                break;


            default:
                break;
        }
        internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
        MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
    }
}