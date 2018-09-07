/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.api;

import android.os.Bundle;

import com.csr.csrmesh2.AttentionModelApi;
import com.csr.csrmesh2.MeshConstants;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.event.MeshRequestEvent;

/**
 * The Attention Model allows a device to be asked to make itself noticeable such that a human could help pinpoint the physical device.
 */
public class AttentionModel {

    public static int setState(final int deviceId, boolean attractAttention, int duration) {

        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putBoolean(MeshConstants.EXTRA_ATTRACT_ATTENTION, attractAttention);
        data.putInt(MeshConstants.EXTRA_DURATION, duration);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ATTENTION_SET_STATE, data));
        return id;
    }


    static void handleRequest(MeshRequestEvent event) {

        int libId = -1;
        int internalId;
        int deviceId;
        boolean attractAttention;
        int duration;

        switch (event.what) {

            case ATTENTION_SET_STATE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                attractAttention = event.data.getBoolean(MeshConstants.EXTRA_ATTRACT_ATTENTION);
                duration = event.data.getInt(MeshConstants.EXTRA_DURATION);

                // Do API call
                libId = AttentionModelApi.setState(deviceId, attractAttention, duration);
                break;

            default:
                break;
        }
        internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
        MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
    }
}
