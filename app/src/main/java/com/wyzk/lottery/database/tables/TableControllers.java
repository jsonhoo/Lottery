package com.wyzk.lottery.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.wyzk.lottery.model.Controller;


/**
 * Database table for controllers.
 */
public class TableControllers implements BaseColumns {

    public static final String TABLE_NAME = "controllers";

    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_UPDATE_DATE = "updateDate";
    public static final String COLUMN_NAME_DEVICE_HASH = "deviceHash";
    public static final String COLUMN_NAME_UUID = "uuid";
    public static final String COLUMN_NAME_DM_KEY = "dmKey";
    public static final String COLUMN_NAME_AUTH_CODE = "authCode";
    public static final String COLUMN_NAME_IS_ASSOCIATED = "isAssociated";
    public static final String COLUMN_NAME_DEVICE_ID = "deviceID";
    public static final String COLUMN_NAME_PLACE_ID = "placeID";


    public static String[] getColumnsNames() {

        String[] columns = {
                TableControllers._ID,
                TableControllers.COLUMN_NAME_NAME,
                TableControllers.COLUMN_NAME_UPDATE_DATE,
                TableControllers.COLUMN_NAME_DEVICE_HASH,
                TableControllers.COLUMN_NAME_UUID,
                TableControllers.COLUMN_NAME_DM_KEY,
                TableControllers.COLUMN_NAME_AUTH_CODE,
                TableControllers.COLUMN_NAME_IS_ASSOCIATED,
                TableControllers.COLUMN_NAME_DEVICE_ID,
                TableControllers.COLUMN_NAME_PLACE_ID
        };

        return columns;
    }


    public static ContentValues createContentValues(Controller controller) {

        // _ID not included since it is Auto-incremental
        ContentValues values = new ContentValues();
        values.put(TableControllers.COLUMN_NAME_NAME, controller.getName());
        values.put(TableControllers.COLUMN_NAME_UPDATE_DATE, controller.getUpdateDate());
        values.put(TableControllers.COLUMN_NAME_DEVICE_HASH, controller.getDeviceHash());
        values.put(TableControllers.COLUMN_NAME_UUID, controller.getUuid());
        values.put(TableControllers.COLUMN_NAME_DM_KEY, controller.getDmKey());
        values.put(TableControllers.COLUMN_NAME_AUTH_CODE, controller.getAuthCode());
        values.put(TableControllers.COLUMN_NAME_IS_ASSOCIATED, controller.isAssociated());
        values.put(TableControllers.COLUMN_NAME_DEVICE_ID, controller.getDeviceID());
        values.put(TableControllers.COLUMN_NAME_PLACE_ID, controller.getPlaceID());

        return values;
    }


    public static Controller getControllerFromCursor(Cursor cursor) {

        Controller controller = new Controller();

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableControllers._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_NAME));
        long updateDate = cursor.getLong(cursor.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_UPDATE_DATE));
        int deviceHash = cursor.getInt(cursor.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_DEVICE_HASH));
        String uuid = cursor.getString(cursor.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_UUID));
        byte[] dmKey = cursor.getBlob(cursor.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_DM_KEY));
        long authCode = cursor.getLong(cursor.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_AUTH_CODE));
        int associatedValue = cursor.getInt(cursor.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_IS_ASSOCIATED));
        int deviceID = cursor.getInt(cursor.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_DEVICE_ID));
        int placeID = cursor.getInt(cursor.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_PLACE_ID));

        controller.setId(id);
        controller.setName(name);
        controller.setUpdateDate(updateDate);
        controller.setDeviceHash(deviceHash);
        controller.setUuid(uuid);
        controller.setDmKey(dmKey);
        controller.setAuthCode(authCode);
        controller.setAssociated((associatedValue == 1) ? true : false);
        controller.setDeviceID(deviceID);
        controller.setPlaceID(placeID);

        return controller;
    }
}
