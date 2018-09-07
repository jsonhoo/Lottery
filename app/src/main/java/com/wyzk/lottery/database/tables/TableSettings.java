/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.wyzk.lottery.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.wyzk.lottery.model.Setting;


/**
 * Database table for app settings.
 */
public class TableSettings implements BaseColumns {

    public static final String TABLE_NAME = "settings";

    public static final String COLUMN_NAME_CONCURRENT_CONNECTIONS = "concurrentConnections";
    public static final String COLUMN_NAME_LISTENING_MODE = "listeningMode";
    public static final String COLUMN_NAME_RETRY_COUNT = "retryCount";
    public static final String COLUMN_NAME_RETRY_INTERVAL = "retryInterval";

    // Cloud settings
    public static final String COLUMN_NAME_CLOUD_MESH_ID = "CloudMeshId";
    public static final String COLUMN_NAME_CLOUD_TENANT_ID = "CloudTenantId";


    public static String[] getColumnsNames() {

        String[] columns = {
                TableSettings._ID,
                TableSettings.COLUMN_NAME_CONCURRENT_CONNECTIONS,
                TableSettings.COLUMN_NAME_LISTENING_MODE,
                TableSettings.COLUMN_NAME_RETRY_COUNT,
                TableSettings.COLUMN_NAME_RETRY_INTERVAL,
                TableSettings.COLUMN_NAME_CLOUD_MESH_ID,
                TableSettings.COLUMN_NAME_CLOUD_TENANT_ID
        };

        return columns;
    }

    public static ContentValues createContentValues(Setting setting) {

        // _ID not included since it is Auto-incremental
        ContentValues values = new ContentValues();
        values.put(TableSettings.COLUMN_NAME_CONCURRENT_CONNECTIONS, setting.getConcurrentConnections());
        values.put(TableSettings.COLUMN_NAME_LISTENING_MODE, setting.getListeningMode());
        values.put(TableSettings.COLUMN_NAME_RETRY_COUNT, setting.getRetryCount());
        values.put(TableSettings.COLUMN_NAME_RETRY_INTERVAL, setting.getRetryInterval());
        values.put(TableSettings.COLUMN_NAME_CLOUD_MESH_ID, setting.getCloudMeshId());
        values.put(TableSettings.COLUMN_NAME_CLOUD_TENANT_ID, setting.getCloudTenantId());

        return values;
    }


    public static Setting getSettingFromCursor(Cursor cursor) {

        Setting setting = new Setting();

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableSettings._ID));
        int concurrentConnections = cursor.getInt(cursor.getColumnIndexOrThrow(TableSettings.COLUMN_NAME_CONCURRENT_CONNECTIONS));
        int listeningMode = cursor.getInt(cursor.getColumnIndexOrThrow(TableSettings.COLUMN_NAME_LISTENING_MODE));
        int retryCount = cursor.getInt(cursor.getColumnIndexOrThrow(TableSettings.COLUMN_NAME_RETRY_COUNT));
        int retryInterval = cursor.getInt(cursor.getColumnIndexOrThrow(TableSettings.COLUMN_NAME_RETRY_INTERVAL));
        String cloudMeshId = cursor.getString(cursor.getColumnIndexOrThrow(TableSettings.COLUMN_NAME_CLOUD_MESH_ID));
        String cloudTenantId = cursor.getString(cursor.getColumnIndexOrThrow(TableSettings.COLUMN_NAME_CLOUD_TENANT_ID));

        setting.setId(id);
        setting.setConcurrentConnections(concurrentConnections);
        setting.setListeningMode(listeningMode);
        setting.setRetryCount(retryCount);
        setting.setRetryInterval(retryInterval);
        setting.setCloudMeshId(cloudMeshId);
        setting.setCloudTenantId(cloudTenantId);

        return setting;
    }
}