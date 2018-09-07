/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.wyzk.lottery.database.DeviceFactory;
import com.wyzk.lottery.model.Device;


/**
 * Database table for devices.
 */
public class TableDevices implements BaseColumns {

    public static final String TABLE_NAME = "devices";

    public static final String COLUMN_NAME_DEVICE_HASH = "deviceHash";
    public static final String COLUMN_NAME_DEVICE_ID = "deviceId";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_APPEARANCE = "appearance";
    public static final String COLUMN_NAME_MODEL_HIGH = "modelHigh";
    public static final String COLUMN_NAME_MODEL_LOW = "modelLow";
    public static final String COLUMN_NAME_N_GROUPS = "nGroups";
    //    public static final String COLUMN_NAME_GROUPS = "groups";
    public static final String COLUMN_NAME_UUID = "uuid";
    public static final String COLUMN_NAME_DM_KEY = "dmKey";
    public static final String COLUMN_NAME_AUTH_CODE = "authCode";
    public static final String COLUMN_NAME_MODEL = "model";
    public static final String COLUMN_NAME_PLACE_ID = "placeId";
    public static final String COLUMN_NAME_IS_FAVOURITE = "isFavourite";
    public static final String COLUMN_NAME_IS_ASSOCIATED = "isAssociated";


    public static String[] getColumnsNames() {

        String[] columns = {
                TableDevices._ID,
                TableDevices.COLUMN_NAME_DEVICE_HASH,
                TableDevices.COLUMN_NAME_DEVICE_ID,
                TableDevices.COLUMN_NAME_NAME,
                TableDevices.COLUMN_NAME_APPEARANCE,
                TableDevices.COLUMN_NAME_MODEL_HIGH,
                TableDevices.COLUMN_NAME_MODEL_LOW,
                TableDevices.COLUMN_NAME_N_GROUPS,
//                TableDevices.COLUMN_NAME_GROUPS,
                TableDevices.COLUMN_NAME_UUID,
                TableDevices.COLUMN_NAME_DM_KEY,
                TableDevices.COLUMN_NAME_AUTH_CODE,
                TableDevices.COLUMN_NAME_MODEL,
                TableDevices.COLUMN_NAME_PLACE_ID,
                TableDevices.COLUMN_NAME_IS_FAVOURITE,
                TableDevices.COLUMN_NAME_IS_ASSOCIATED
        };

        return columns;
    }


    public static ContentValues createContentValues(Device device) {

        // _ID not included since it is Auto-incremental
        ContentValues values = new ContentValues();
        values.put(TableDevices.COLUMN_NAME_DEVICE_HASH, device.getDeviceHash());
        values.put(TableDevices.COLUMN_NAME_DEVICE_ID, device.getDeviceId());
        values.put(TableDevices.COLUMN_NAME_NAME, device.getName());
        values.put(TableDevices.COLUMN_NAME_APPEARANCE, device.getAppearance());
        values.put(TableDevices.COLUMN_NAME_MODEL_HIGH, device.getModelHigh());
        values.put(TableDevices.COLUMN_NAME_MODEL_LOW, device.getModelLow());
        values.put(TableDevices.COLUMN_NAME_N_GROUPS, device.getNumGroups());
        values.put(TableDevices.COLUMN_NAME_UUID, device.getUuid().toString());
//        values.put(TableDevices.COLUMN_NAME_GROUPS, device.getGroupsByteArray());
        values.put(TableDevices.COLUMN_NAME_DM_KEY, device.getDmKey());
        values.put(TableDevices.COLUMN_NAME_AUTH_CODE, device.getAuthCode());
        values.put(TableDevices.COLUMN_NAME_MODEL, device.getModel());
        values.put(TableDevices.COLUMN_NAME_PLACE_ID, device.getPlaceID());
        values.put(TableDevices.COLUMN_NAME_IS_FAVOURITE, device.isFavourite());
        values.put(TableDevices.COLUMN_NAME_IS_ASSOCIATED, device.isAssociated());

        return values;
    }

    public static Device getDeviceFromCursor(Cursor cursor) {

        Device device = null;

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices._ID));
        int deviceHash = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_DEVICE_HASH));
        int deviceId = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_DEVICE_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_NAME));
        int appearance = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_APPEARANCE));
        int modelHigh = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_MODEL_HIGH));
        int modelLow = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_MODEL_LOW));
        int numberGroups = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_N_GROUPS));
//        byte[] groups = cursor.getBlob(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_GROUPS));
        String dmKey = cursor.getString(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_DM_KEY));
        long authCode = cursor.getLong(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_AUTH_CODE));
        int model = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_MODEL));
        String uuid = cursor.getString(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_UUID));
        int placeID = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_PLACE_ID));
        int favouriteValue = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_IS_FAVOURITE));
        int associatedValue = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_IS_ASSOCIATED));

        // Create an instance of the appropriate type of device based on the appearance value
        device = DeviceFactory.getDevice(appearance);

        device.setDatabaseId(id);
        device.setDeviceHash(deviceHash);
        device.setDeviceId(deviceId);
        device.setName(name);
        device.setAppearance(appearance);
        device.setModelHigh(modelHigh);
        device.setModelLow(modelLow);
        device.setNumGroups(numberGroups);
        device.setUuid(uuid);
//        device.setGroups(ApplicationUtils.byteArrayToIntArray(groups));
        device.setDmKey(dmKey);
        device.setAuthCode(authCode);
        device.setModel(model);
        device.setPlaceID(placeID);
        device.setFavourite((favouriteValue == 1) ? true : false);
        device.setAssociated((associatedValue == 1) ? true : false);

        return device;
    }
}
