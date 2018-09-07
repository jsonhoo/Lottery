/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.wyzk.lottery.event.DeviceEvent;
import com.wyzk.lottery.event.Event;


/**
 * Database table for devices involved in a event.
 */
public class TableDeviceEvents implements BaseColumns {

    public static final String TABLE_NAME = "deviceEvents";

    public static final String COLUMN_DEVICE_EVENT_ID = "deviceEventID";
    public static final String COLUMN_DEVICE_ID = "deviceID";
    public static final String COLUMN_EVENT_TIME = "time";
    public static final String COLUMN_EVENT_REPEAT_MILLS = "repeat_mills";


    public static String[] getColumnsNames() {

        String[] columns = {
                TableDeviceEvents._ID,
                TableDeviceEvents.COLUMN_DEVICE_EVENT_ID,
                TableDeviceEvents.COLUMN_DEVICE_ID,
                TableDeviceEvents.COLUMN_EVENT_TIME,
                TableDeviceEvents.COLUMN_EVENT_REPEAT_MILLS,
        };

        return columns;
    }


    public static ContentValues createContentValues(Event event, int position) {

        DeviceEvent deviceEvent = event.getDeviceEvents().get(position);

        // _ID not included since it is Auto-incremental
        ContentValues values = new ContentValues();
        values.put(TableDeviceEvents._ID, event.getId());
        values.put(TableDeviceEvents.COLUMN_DEVICE_EVENT_ID, deviceEvent.getDeviceEventId());
        values.put(TableDeviceEvents.COLUMN_DEVICE_ID, deviceEvent.getDeviceId());
        values.put(TableDeviceEvents.COLUMN_EVENT_TIME, deviceEvent.getTime());
        values.put(TableDeviceEvents.COLUMN_EVENT_REPEAT_MILLS, deviceEvent.getRepeatMls());
        return values;
    }

    public static DeviceEvent getDeviceEventFromCursor(Cursor cursor) {

        DeviceEvent deviceEvent = new DeviceEvent();
        int eventID = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceEvents.COLUMN_DEVICE_EVENT_ID));
        int deviceId = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceEvents.COLUMN_DEVICE_ID));
        long time = cursor.getLong(cursor.getColumnIndexOrThrow(TableDeviceEvents.COLUMN_EVENT_TIME));
        long repeatTime = cursor.getLong(cursor.getColumnIndexOrThrow(TableDeviceEvents.COLUMN_EVENT_REPEAT_MILLS));

        deviceEvent.setDeviceEventId(eventID);
        deviceEvent.setDeviceId(deviceId);
        deviceEvent.setTime(time);
        deviceEvent.setRepeatMls(repeatTime);

        return deviceEvent;
    }
}
