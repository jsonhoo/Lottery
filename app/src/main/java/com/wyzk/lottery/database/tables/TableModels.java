/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.wyzk.lottery.model.Model;


/**
 * Database table for models.
 */
public class TableModels implements BaseColumns {

    public static final String TABLE_NAME = "models";

    public static final String COLUMN_NAME_MODEL_NUMBER = "modelNumber";
    public static final String COLUMN_NAME_MODEL_INSTANCE = "modelInstance";
    public static final String COLUMN_NAME_N_AREA_INSTANCES = "nAreaInstances";
    public static final String COLUMN_NAME_AREA_IDS = "areaIDs";
    public static final String COLUMN_NAME_DEVICE_ID = "deviceID";


    public static String[] getColumnsNames() {

        String[] columns = {
                TableModels._ID,
                TableModels.COLUMN_NAME_MODEL_NUMBER,
                TableModels.COLUMN_NAME_MODEL_INSTANCE,
                TableModels.COLUMN_NAME_N_AREA_INSTANCES,
                TableModels.COLUMN_NAME_AREA_IDS,
                TableModels.COLUMN_NAME_DEVICE_ID
        };

        return columns;
    }


    public static ContentValues createContentValues(Model model) {

        // _ID not included since it is Auto-incremental
        ContentValues values = new ContentValues();
        values.put(TableModels.COLUMN_NAME_MODEL_NUMBER, model.getModelNumber());
        values.put(TableModels.COLUMN_NAME_MODEL_INSTANCE, model.getModelInstance());
        values.put(TableModels.COLUMN_NAME_N_AREA_INSTANCES, model.getnAreaInstances());
        values.put(TableModels.COLUMN_NAME_AREA_IDS, model.getAreaIDs());
        values.put(TableModels.COLUMN_NAME_DEVICE_ID, model.getDeviceID());

        return values;
    }

    public static Model getModelFromCursor(Cursor cursor) {

        Model model = new Model();

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableModels._ID));
        int modelNumber = cursor.getInt(cursor.getColumnIndexOrThrow(TableModels.COLUMN_NAME_MODEL_NUMBER));
        int modelInstance = cursor.getInt(cursor.getColumnIndexOrThrow(TableModels.COLUMN_NAME_MODEL_INSTANCE));
        int nAreaInstances = cursor.getInt(cursor.getColumnIndexOrThrow(TableModels.COLUMN_NAME_N_AREA_INSTANCES));
        byte[] areaIDs = cursor.getBlob(cursor.getColumnIndexOrThrow(TableModels.COLUMN_NAME_AREA_IDS));
        int deviceID = cursor.getInt(cursor.getColumnIndexOrThrow(TableModels.COLUMN_NAME_DEVICE_ID));

        model.setId(id);
        model.setModelNumber(modelNumber);
        model.setModelInstance(modelInstance);
        model.setnAreaInstances(nAreaInstances);
        model.setAreaIDs(areaIDs);
        model.setDeviceID(deviceID);

        return model;
    }
}