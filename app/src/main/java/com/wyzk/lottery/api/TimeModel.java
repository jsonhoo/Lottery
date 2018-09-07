/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.api;

import android.os.Bundle;

import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.TimeModelApi;
import com.uuxia.email.Logc;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.event.MeshRequestEvent;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Time Model is used to report the current time in order to synchronize the time among mesh network.
 */
public final class TimeModel {

    /**
     * Set the desired number of seconds between time broadcasts.
     * <p>
     * Response is MESSAGE_TIME_STATE with data:
     * <ul>
     * <li>EXTRA_TIME_INTERVAL (int)</li> number of seconds between time broadcasts.
     * </ul>
     *
     * @param deviceId
     * @param timeInterval (int) Time in seconds.
     * @return Unique databaseId to identify the request. Included in the response or timeout message.
     */
    public static int setState(int deviceId, int timeInterval) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_TIME_INTERVAL, timeInterval);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.TIME_SET_STATE, data));
        return id;
    }


    /**
     * Get the number of seconds between time broadcasts.
     * <p>
     * Response is MESSAGE_TIME_STATE with data:
     * <ul>
     * <li>EXTRA_TIME_INTERVAL (int)</li> number of seconds between time broadcasts.
     * </ul>
     *
     * @param deviceId
     * @return Unique databaseId to identify the request. Included in the response or timeout message.
     */
    public static int getState(int deviceId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        LotteryApplication.bus.post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.TIME_GET_STATE, data));
        return id;
    }

    /**
     * Broadcast the current time to the mesh network.
     */
    public static void broadcastTime() {
        final int MS_IN_15_MINS = 15 * 60 * 1000;
        final byte utcOffset = (byte) (TimeZone.getDefault().getOffset(Calendar.getInstance().getTimeInMillis()) / MS_IN_15_MINS);
        try {
            TimeModelApi.broadcastTime(Calendar.getInstance().getTimeInMillis(), utcOffset, true);
        } catch (IllegalStateException e) {
            Logc.e(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Logc.e(e.getMessage());
            e.printStackTrace();
        }

    }


    static void handleRequest(MeshRequestEvent event) {
        switch (event.what) {
            case TIME_SET_STATE: {
                int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                int timeInterval = event.data.getInt(MeshConstants.EXTRA_TIME_INTERVAL);
                int libId = TimeModelApi.setState(deviceId, timeInterval);
                int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
                MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
            }
            break;
            case TIME_GET_STATE: {
                int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                int libId = TimeModelApi.getState(deviceId);
                int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
                MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
                break;
            }
            default:
                break;
        }
    }

}
