/*
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 */

package com.wyzk.lottery.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.wyzk.lottery.model.Gateway;


/**
 * Database table for gateways.
 */
public class TableGateways implements BaseColumns {

    public static final String TABLE_NAME = "gateway";

    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_HOST = "host";
    public static final String COLUMN_NAME_PORT = "port";
    public static final String COLUMN_NAME_UUID = "uuid";
    public static final String COLUMN_NAME_BASEPATH = "basePath";
    public static final String COLUMN_NAME_STATE = "state";
    public static final String COLUMN_NAME_DHM_KEY = "dmKey";
    public static final String COLUMN_NAME_DEVICE_HASH = "deviceHash";
    public static final String COLUMN_NAME_DEVICE_ID = "deviceID";
    public static final String COLUMN_NAME_PLACE_ID = "placeID";

    public static String[] getColumnsNames() {

        String[] columns = {
                TableGateways._ID,
                TableGateways.COLUMN_NAME_NAME,
                TableGateways.COLUMN_NAME_HOST,
                TableGateways.COLUMN_NAME_PORT,
                TableGateways.COLUMN_NAME_UUID,
                TableGateways.COLUMN_NAME_BASEPATH,
                TableGateways.COLUMN_NAME_STATE,
                TableGateways.COLUMN_NAME_DHM_KEY,
                TableGateways.COLUMN_NAME_DEVICE_HASH,
                TableGateways.COLUMN_NAME_DEVICE_ID,
                TableGateways.COLUMN_NAME_PLACE_ID,
        };

        return columns;
    }

    public static ContentValues createContentValues(Gateway gateway) {

        // _ID not included since it is Auto-incremental
        ContentValues values = new ContentValues();
        values.put(TableGateways.COLUMN_NAME_NAME, gateway.getName());
        values.put(TableGateways.COLUMN_NAME_HOST, gateway.getHost());
        values.put(TableGateways.COLUMN_NAME_PORT, gateway.getPort());
        values.put(TableGateways.COLUMN_NAME_UUID, gateway.getUuid());
        values.put(TableGateways.COLUMN_NAME_BASEPATH, gateway.getBasePath());
        values.put(TableGateways.COLUMN_NAME_STATE, gateway.getState());
        values.put(TableGateways.COLUMN_NAME_DHM_KEY, gateway.getDhmKey());
        values.put(TableGateways.COLUMN_NAME_DEVICE_HASH, gateway.getDeviceHash());
        values.put(TableGateways.COLUMN_NAME_DEVICE_ID, gateway.getDeviceID());
        values.put(TableGateways.COLUMN_NAME_PLACE_ID, gateway.getPlaceID());

        return values;
    }


    public static Gateway getGatewayFromCursor(Cursor cursor) {

        Gateway gateway = new Gateway();

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableGateways._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_NAME));
        String host = cursor.getString(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_HOST));
        String port = cursor.getString(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_PORT));
        String uuid = cursor.getString(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_UUID));
        String basePath = cursor.getString(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_BASEPATH));
        int state = cursor.getInt(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_STATE));
        byte[] dhmKey = cursor.getBlob(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_DHM_KEY));
        int deviceHash = cursor.getInt(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_DEVICE_HASH));
        int deviceID = cursor.getInt(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_DEVICE_ID));
        int placeID = cursor.getInt(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_PLACE_ID));

        gateway.setId(id);
        gateway.setName(name);
        gateway.setHost(host);
        gateway.setPort(port);
        gateway.setUuid(uuid);
        gateway.setBasePath(basePath);
        gateway.setState(state);
        gateway.setDhmKey(dhmKey);
        gateway.setDeviceHash(deviceHash);
        gateway.setDeviceID(deviceID);
        gateway.setPlaceID(placeID);

        return gateway;
    }
}
