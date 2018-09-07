/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.api;

import android.os.Bundle;

import com.csr.csrmesh2.MeshConstants;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.event.MeshRequestEvent;

import java.util.UUID;

/**
 * This class covers the APIs related to association such as associateDevice, resetDevice, Attention, etc.
 */
public class Association {
    public static void discoverDevices(boolean enabled) {
        Bundle data = new Bundle();
        data.putBoolean(MeshConstants.EXTRA_ENABLED, enabled);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.DISCOVER_DEVICES, data));
    }

    public static int associateDevice(int deviceHash, long authorizationCode, boolean authorizationCodeKnown, int deviceId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshConstants.EXTRA_UUIDHASH_31, deviceHash);
        data.putLong(MeshConstants.EXTRA_AUTH_CODE, authorizationCode);
        data.putBoolean(MeshConstants.EXTRA_AUTH_CODE_KNOWN, authorizationCodeKnown);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ASSOCIATE_DEVICE, data));
        return id;
    }

    public static void startAdvertising(String shortName, UUID uuid, boolean useAuthCode, long authCode) {
        Bundle data = new Bundle();
        data.putString(MeshConstants.EXTRA_SHORTNAME, shortName);
        data.putString(MeshConstants.EXTRA_UUID, uuid.toString());
        data.putBoolean(MeshConstants.EXTRA_AUTH_CODE_KNOWN, useAuthCode);
        data.putLong(MeshConstants.EXTRA_AUTH_CODE, authCode);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.START_ADVERTISING, data));
    }

    public static void stopAdvertising() {
        Bundle data = new Bundle();
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.STOP_ADVERTISING, data));
    }


    public static void attentionPreAssociation(int deviceHash, boolean enabled, int duration) {
    Bundle data = new Bundle();
    data.putInt(MeshConstants.EXTRA_UUIDHASH_31, deviceHash);
    data.putBoolean(MeshConstants.EXTRA_ENABLED, enabled);
    data.putInt(MeshConstants.EXTRA_DURATION, duration);
    LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ATTENTION_PRE_ASSOCIATION, data));
    }

    public static void resetDevice(int deviceId, byte[] resetKey) {
        Bundle data = new Bundle();
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putByteArray(MeshConstants.EXTRA_RESET_KEY, resetKey);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.MASP_RESET, data));
    }

    /*package*/
    static void handleRequest(MeshRequestEvent event) {
        int libId = 0;
        switch (event.what) {
            case DISCOVER_DEVICES:
                MeshLibraryManager.getInstance().getMeshService().setDeviceDiscoveryFilterEnabled(event.data.getBoolean(MeshConstants.EXTRA_ENABLED));
                break;
            case ATTENTION_PRE_ASSOCIATION:
                MeshLibraryManager.getInstance().getMeshService().setAttentionPreAssociation(
                        event.data.getInt(MeshConstants.EXTRA_UUIDHASH_31),
                        event.data.getBoolean(MeshConstants.EXTRA_ENABLED),
                        event.data.getInt(MeshConstants.EXTRA_DURATION));
                break;
            case STOP_ADVERTISING:
                MeshLibraryManager.getInstance().getMeshService().stopAdvertiseForAssociation();
                break;
            case START_ADVERTISING:
                MeshLibraryManager.getInstance().getMeshService().advertiseForAssociation(
                        event.data.getString(MeshConstants.EXTRA_SHORTNAME),
                        UUID.fromString(event.data.getString(MeshConstants.EXTRA_UUID)),
                        event.data.getBoolean(MeshConstants.EXTRA_AUTH_CODE_KNOWN),
                        event.data.getLong(MeshConstants.EXTRA_AUTH_CODE));
                break;
            case ASSOCIATE_DEVICE:
                libId = MeshLibraryManager.getInstance().getMeshService().associateDevice(
                        event.data.getInt(MeshConstants.EXTRA_UUIDHASH_31),
                        event.data.getLong(MeshConstants.EXTRA_AUTH_CODE),
                        event.data.getBoolean(MeshConstants.EXTRA_AUTH_CODE_KNOWN),
                        event.data.getInt(MeshConstants.EXTRA_DEVICE_ID));
                break;
            case MASP_RESET:
                MeshLibraryManager.getInstance().getMeshService().resetDevice(
                        event.data.getInt(MeshConstants.EXTRA_DEVICE_ID),
                        event.data.getByteArray(MeshConstants.EXTRA_RESET_KEY));
                break;

        }
        if (libId != 0) {
            int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
