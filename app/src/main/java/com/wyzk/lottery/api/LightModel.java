/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.api;

import android.os.Bundle;

import com.csr.csrmesh2.LightModelApi;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.PowerState;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.event.MeshRequestEvent;

/**
 * The Light Model is used to control devices that typically have dimmable and/or coloured lights.
 */
public class LightModel {

    public static int setRgb(final int deviceId, int colorARGB, int duration, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_COLOR_ARGB, colorARGB);
        data.putInt(MeshConstants.EXTRA_DURATION, duration);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.LIGHT_SET_RGB, data));
        return id;
    }

    public static int getState(final int deviceId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.LIGHT_GET_STATE, data));
        return id;
    }

    public static int setColorTemperature(final int deviceId, int temperature, int duration) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_COLOR_TEMP, temperature);
        data.putInt(MeshConstants.EXTRA_DURATION, duration);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.LIGHT_SET_COLOR_TEMPERATURE, data));
        return id;
    }

    public static int setLevel(final int deviceId, int level, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_LEVEL, level);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.LIGHT_SET_LEVEL, data));
        return id;
    }

    public static int setPowerLevel(final int deviceId, PowerState powerState, int level, int duration, int sustain, int decay, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_POWER_STATE, powerState.ordinal());
        data.putInt(MeshConstants.EXTRA_LEVEL, level);
        data.putInt(MeshConstants.EXTRA_DURATION, duration);
        data.putInt(MeshConstants.EXTRA_SUSTAIN, sustain);
        data.putInt(MeshConstants.EXTRA_DECAY, decay);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.LIGHT_SET_POWER_LEVEL, data));
        return id;
    }

    public static int setWhite(final int deviceId, int level, int duration, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_WHITE_LEVEL, level);
        data.putInt(MeshConstants.EXTRA_DURATION, duration);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.LIGHT_SET_WHITE, data));
        return id;
    }


    static void handleRequest(MeshRequestEvent event) {

        int libId = -1;
        int deviceId;
        int colorARGB;
        int duration;
        int internalId;
        int temperature;
        int level;
        int whiteLevel;
        int sustain;
        int decay;
        boolean acknowledged;
        int powerState;

        switch (event.what) {

            case LIGHT_SET_RGB:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                colorARGB = event.data.getInt(MeshConstants.EXTRA_COLOR_ARGB);
                duration = event.data.getInt(MeshConstants.EXTRA_DURATION);
                acknowledged = event.data.getBoolean(MeshConstants.EXTRA_ACKNOWLEDGED);
                // Do API call
                libId = LightModelApi.setRgb(deviceId, colorARGB, duration, acknowledged);
                break;

            case LIGHT_GET_STATE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                // Do API call
                libId = LightModelApi.getState(deviceId);
                break;

            case LIGHT_SET_COLOR_TEMPERATURE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                temperature = event.data.getInt(MeshConstants.EXTRA_COLOR_TEMP);
                duration = event.data.getInt(MeshConstants.EXTRA_DURATION);
                // Do API call
                libId = LightModelApi.setColorTemperature(deviceId, temperature, duration);
                break;

            case LIGHT_SET_LEVEL:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                level = event.data.getInt(MeshConstants.EXTRA_LEVEL);
                acknowledged = event.data.getBoolean(MeshConstants.EXTRA_ACKNOWLEDGED);
                // Do API call
                libId = LightModelApi.setLevel(deviceId, level, acknowledged);
                break;

            case LIGHT_SET_POWER_LEVEL:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                powerState = event.data.getInt(MeshConstants.EXTRA_POWER_STATE);
                level = event.data.getInt(MeshConstants.EXTRA_LEVEL);
                duration = event.data.getInt(MeshConstants.EXTRA_DURATION);
                sustain = event.data.getInt(MeshConstants.EXTRA_SUSTAIN);
                decay = event.data.getInt(MeshConstants.EXTRA_DECAY);
                acknowledged = event.data.getBoolean(MeshConstants.EXTRA_ACKNOWLEDGED);
                // Do API call
                libId = LightModelApi.setPowerLevel(deviceId, PowerState.values()[powerState], level, duration, sustain, decay, acknowledged);
                break;

            case LIGHT_SET_WHITE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                whiteLevel = event.data.getInt(MeshConstants.EXTRA_WHITE_LEVEL);
                duration = event.data.getInt(MeshConstants.EXTRA_DURATION);
                acknowledged = event.data.getBoolean(MeshConstants.EXTRA_ACKNOWLEDGED);
                libId = LightModelApi.setWhite(deviceId, whiteLevel, duration, acknowledged);
                break;

            default:
                break;
        }
        internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
        MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
    }
}
