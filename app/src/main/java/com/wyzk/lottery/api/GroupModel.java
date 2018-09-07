/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.api;

import android.os.Bundle;

import com.csr.csrmesh2.GroupModelApi;
import com.csr.csrmesh2.MeshConstants;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.event.MeshRequestEvent;


/**
 * The Group Model is used to query and configure groups that CSRmesh devices belong to.
 */
public class GroupModel {
    public static int getNumberOfModelGroupIds(final int deviceId, int modelNo) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_MODEL_NO, modelNo);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.GROUP_GET_NUMBER_OF_MODEL_GROUP_IDS, data));
        return id;
    }

    public static int setModelGroupId(final int deviceId, int modelNo, int groupIndex, int instance, int groupId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_MODEL_NO, modelNo);
        data.putInt(MeshConstants.EXTRA_GROUP_INDEX, groupIndex);
        data.putInt(MeshConstants.EXTRA_MODEL_INSTANCE, instance);
        data.putInt(MeshConstants.EXTRA_GROUP_ID, groupId);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.GROUP_SET_MODEL_GROUP_ID, data));
        return id;
    }

    public static int getModelGroupId(int deviceId, int modelNo, int groupIndex) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_MODEL_NO, modelNo);
        data.putInt(MeshConstants.EXTRA_GROUP_INDEX, groupIndex);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.GROUP_GET_MODEL_GROUP_ID, data));
        return id;
    }

    static void handleRequest(MeshRequestEvent event) {
        int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
        int modelNo = event.data.getInt(MeshConstants.EXTRA_MODEL_NO);
        int groupIndex = event.data.getInt(MeshConstants.EXTRA_GROUP_INDEX);
        int instance = event.data.getInt(MeshConstants.EXTRA_MODEL_INSTANCE);
        int groupId = event.data.getInt(MeshConstants.EXTRA_GROUP_ID);
        int libId = 0;
        switch (event.what) {
            case GROUP_GET_NUMBER_OF_MODEL_GROUP_IDS:
                libId = GroupModelApi.getNumberOfModelGroupIds(deviceId, modelNo);
                break;
            case GROUP_SET_MODEL_GROUP_ID:
                libId = GroupModelApi.setModelGroupId(deviceId, modelNo, groupIndex, instance, groupId);
                break;
            case GROUP_GET_MODEL_GROUP_ID:
                libId = GroupModelApi.getModelGroupId(deviceId, modelNo, groupIndex);
                break;
        }
        if (libId != 0) {
            int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
