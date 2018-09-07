/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.api;

import android.os.Bundle;

import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.SensorModelApi;
import com.csr.csrmesh2.SensorValue;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.event.MeshRequestEvent;


/*
 * The Sensor Model broadcasts values to other devices in the mesh network. These values are either based on the
 * physical measurement of something, for example the current air temperature, or based on a desired value for such a
 * physical measurement. These desired values could then be used by devices locally such that a feedback loop is
 * created. For example, a fan could receive both the current air temperature and the desired air temperature from
 * other devices and would turn on when the current air temperature was higher than the desired air temperature.
 *
 */
public class SensorModel {
    public static int getValue(int deviceId, SensorValue.SensorType type1, SensorValue.SensorType type2) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE1, type1.ordinal());

        if (type2 != null && type2 != SensorValue.SensorType.UNKNOWN) {
            data.putInt(MeshConstants.EXTRA_SENSOR_TYPE2, type2.ordinal());
        }

        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.SENSOR_GET_VALUE, data));
        return id;
    }

    public static int setValue(int deviceId, SensorValue value1, SensorValue value2, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putParcelable(MeshConstants.EXTRA_SENSOR_VALUE1, value1);
        data.putParcelable(MeshConstants.EXTRA_SENSOR_VALUE2, value2);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.SENSOR_SET_VALUE, data));
        return id;
    }

    public static int getTypes(int deviceId, SensorValue.SensorType firstType) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE1, firstType.ordinal());
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.SENSOR_GET_TYPES, data));
        return id;
    }

    public static int setState(int deviceId, SensorValue.SensorType sensorType, int repeatInterval) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE1, sensorType.ordinal());
        data.putInt(MeshConstants.EXTRA_REPEAT_INTERVAL, repeatInterval);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.SENSOR_SET_STATE, data));
        return id;
    }

    public static int getState(final int deviceId, int sensorType) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE1, sensorType);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.SENSOR_GET_STATE, data));
        return id;
    }

    static void handleRequest(MeshRequestEvent event) {
        int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
        int libId = 0;

        switch (event.what) {
            case SENSOR_GET_VALUE:
            {
                int type1 = event.data.getInt(MeshConstants.EXTRA_SENSOR_TYPE1);
                int type2 = event.data.getInt(MeshConstants.EXTRA_SENSOR_TYPE2, -1);
                libId = SensorModelApi.getValue(deviceId, SensorValue.SensorType.values()[type1], type2 == -1? null: SensorValue.SensorType.values()[type2]);

                break;
            }
            case SENSOR_SET_VALUE:
            {
                SensorValue value1 = event.data.getParcelable(MeshConstants.EXTRA_SENSOR_VALUE1);
                SensorValue value2 = event.data.getParcelable(MeshConstants.EXTRA_SENSOR_VALUE2);
                boolean ack = event.data.getBoolean(MeshConstants.EXTRA_ACKNOWLEDGED);
                libId = SensorModelApi.setValue(deviceId, value1, value2, ack);
                break;
            }
            case SENSOR_GET_TYPES:
            {
                int type1 = event.data.getInt(MeshConstants.EXTRA_SENSOR_TYPE1);
                libId = SensorModelApi.getTypes(deviceId, SensorValue.SensorType.values()[type1]);
                break;
            }
            case SENSOR_SET_STATE:
            {
                int type1 = event.data.getInt(MeshConstants.EXTRA_SENSOR_TYPE1);
                int repeatInterval = event.data.getInt(MeshConstants.EXTRA_REPEAT_INTERVAL);
                libId = SensorModelApi.setState(deviceId, SensorValue.SensorType.values()[type1], repeatInterval);
                break;
            }
            case SENSOR_GET_STATE:
            {
                int type1 = event.data.getInt(MeshConstants.EXTRA_SENSOR_TYPE1);
                libId = SensorModelApi.getState(deviceId, SensorValue.SensorType.values()[type1]);
                break;
            }
        }
        if (libId != 0) {
            int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
