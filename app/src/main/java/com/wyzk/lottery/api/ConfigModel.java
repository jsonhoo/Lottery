/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.api;

import android.os.Bundle;

import com.csr.csrmesh2.ConfigModelApi;
import com.csr.csrmesh2.DeviceInfo;
import com.csr.csrmesh2.MeshConstants;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.event.MeshRequestEvent;

/**
 * The Config Model is used to configure CSRmesh devices. This includes discovering information about devices such as the set of models a device supports.
 */
public class ConfigModel {

    public static void discoverDevice(final int deviceId) {
        Bundle data = new Bundle();
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CONFIG_DISCOVER_DEVICE, data));
    }

    public static int getInfo(final int deviceId, DeviceInfo infoType) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_DEVICE_INFO_TYPE, infoType.ordinal());
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CONFIG_GET_INFO, data));
        return id;
    }

    public static void resetDevice(int deviceId, long deviceHash, byte[] dmKey) {
        Bundle data = new Bundle();
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putLong(MeshConstants.EXTRA_UUIDHASH_64, deviceHash);
        data.putByteArray(MeshConstants.EXTRA_RESET_KEY, dmKey);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CONFIG_RESET_DEVICE, data));
    }

    public static void setDeviceId(int deviceId, long uuidHash, final int newDeviceId) {
        Bundle data = new Bundle();
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putLong(MeshConstants.EXTRA_UUIDHASH_64, uuidHash);
        data.putInt(MeshConstants.EXTRA_NEW_DEVICE_ID, newDeviceId);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CONFIG_SET_DEVICE_IDENTIFIER, data));
    }

    public static int getParameters(final int deviceId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CONFIG_GET_PARAMETERS, data));
        return id;
    }

    public static int setParameters(final int deviceId, int txInterval, int txDuration, int rxDutyCycle, int txPower, int ttl) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_TX_INTERVAL, txInterval);
        data.putInt(MeshConstants.EXTRA_TX_DURATION, txDuration);
        data.putInt(MeshConstants.EXTRA_RX_DUTY_CYCLE, rxDutyCycle);
        data.putInt(MeshConstants.EXTRA_TX_POWER, txPower);
        data.putInt(MeshConstants.EXTRA_TTL, ttl);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.CONFIG_SET_PARAMETERS, data));
        return id;
    }


    static void handleRequest(MeshRequestEvent event) {

        int libId = -1;
        int internalId;
        int deviceId;
        int deviceInfo;
        long uuidHash;
        int newDeviceId;
        int txInterval;
        int txDuration;
        int rxDutyCycle;
        int txPower;
        int ttl;

        switch (event.what) {

            case CONFIG_DISCOVER_DEVICE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                // Do API call
                ConfigModelApi.discoverDevice(deviceId);
                break;

            case CONFIG_GET_INFO:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                deviceInfo = event.data.getInt(MeshConstants.EXTRA_DEVICE_INFO_TYPE);
                // Do API call
                libId = ConfigModelApi.getInfo(deviceId, DeviceInfo.values()[deviceInfo]);
                break;

            case CONFIG_RESET_DEVICE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                long deviceHash = event.data.getLong(MeshConstants.EXTRA_UUIDHASH_64);
                byte[] dmKey = event.data.getByteArray(MeshConstants.EXTRA_RESET_KEY);
                // Do API call
                ConfigModelApi.resetDevice(deviceId, deviceHash, dmKey);
                break;

            case CONFIG_SET_DEVICE_IDENTIFIER:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                uuidHash = event.data.getLong(MeshConstants.EXTRA_UUIDHASH_64);
                newDeviceId = event.data.getInt(MeshConstants.EXTRA_NEW_DEVICE_ID);
                // Do API call
                ConfigModelApi.setDeviceId(deviceId, uuidHash, newDeviceId);
                break;

            case CONFIG_GET_PARAMETERS:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                // Do API call
                libId = ConfigModelApi.getParameters(deviceId);
                break;

            case CONFIG_SET_PARAMETERS:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                txInterval = event.data.getInt(MeshConstants.EXTRA_TX_INTERVAL);
                txDuration = event.data.getInt(MeshConstants.EXTRA_TX_DURATION);
                rxDutyCycle = event.data.getInt(MeshConstants.EXTRA_RX_DUTY_CYCLE);
                txPower = event.data.getInt(MeshConstants.EXTRA_TX_POWER);
                ttl = event.data.getInt(MeshConstants.EXTRA_TTL);
                // Do API call
                libId = ConfigModelApi.setParameters(deviceId, txInterval, txDuration, rxDutyCycle, txPower, ttl);
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
