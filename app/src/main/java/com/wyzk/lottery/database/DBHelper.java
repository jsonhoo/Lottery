/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.wyzk.lottery.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.wyzk.lottery.database.tables.TableAreas;
import com.wyzk.lottery.database.tables.TableControllers;
import com.wyzk.lottery.database.tables.TableDeviceEvents;
import com.wyzk.lottery.database.tables.TableDevices;
import com.wyzk.lottery.database.tables.TableEvents;
import com.wyzk.lottery.database.tables.TableGateways;
import com.wyzk.lottery.database.tables.TablePlaces;
import com.wyzk.lottery.database.tables.TableSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class that is used to initialize/update/delete the database.
 */
public class DBHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String TAG = "DBHelper";

    // Database version and name
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "meshet.db";

    private final Context mContext;

    // Tables creation strings
    private static final String TYPE_BLOB = " BLOB";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_TEXT = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_DEVICES_TABLE =
            "CREATE TABLE " + TableDevices.TABLE_NAME + " (" +
                    TableDevices._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableDevices.COLUMN_NAME_DEVICE_HASH + TYPE_INTEGER + COMMA_SEP +
                    TableDevices.COLUMN_NAME_DEVICE_ID + TYPE_INTEGER + COMMA_SEP +
                    TableDevices.COLUMN_NAME_NAME + TYPE_TEXT + COMMA_SEP +
                    TableDevices.COLUMN_NAME_APPEARANCE + TYPE_INTEGER + COMMA_SEP +
                    TableDevices.COLUMN_NAME_MODEL_HIGH + TYPE_INTEGER + COMMA_SEP +
                    TableDevices.COLUMN_NAME_MODEL_LOW + TYPE_INTEGER + COMMA_SEP +
                    TableDevices.COLUMN_NAME_N_GROUPS + TYPE_INTEGER + COMMA_SEP +
//                    TableDevices.COLUMN_NAME_GROUPS + TYPE_BLOB + COMMA_SEP +
                    TableDevices.COLUMN_NAME_UUID + TYPE_TEXT + COMMA_SEP +
                    TableDevices.COLUMN_NAME_DM_KEY + TYPE_TEXT + COMMA_SEP +
                    TableDevices.COLUMN_NAME_AUTH_CODE + TYPE_INTEGER + COMMA_SEP +

                    TableDevices.COLUMN_NAME_MODEL + TYPE_INTEGER + COMMA_SEP +
                    TableDevices.COLUMN_NAME_PLACE_ID + TYPE_INTEGER + COMMA_SEP +
                    TableDevices.COLUMN_NAME_IS_FAVOURITE + TYPE_INTEGER + COMMA_SEP +
                    TableDevices.COLUMN_NAME_IS_ASSOCIATED + TYPE_INTEGER + COMMA_SEP +
                    "FOREIGN KEY (" + TableDevices.COLUMN_NAME_PLACE_ID + ") REFERENCES "
                    + TablePlaces.TABLE_NAME + "(" + TablePlaces._ID + ")" +
                    " )";

    private static final String SQL_CREATE_AREAS_TABLE =
            "CREATE TABLE " + TableAreas.TABLE_NAME + " (" +
                    TableAreas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableAreas.COLUMN_NAME_AREA_ID + TYPE_INTEGER + COMMA_SEP +
                    TableAreas.COLUMN_NAME_NAME + TYPE_TEXT + COMMA_SEP +
                    TableAreas.COLUMN_NAME_IS_FAVOURITE + TYPE_INTEGER + COMMA_SEP +
                    TableAreas.COLUMN_NAME_PLACE_ID + TYPE_INTEGER + COMMA_SEP +
                    "FOREIGN KEY (" + TableAreas.COLUMN_NAME_PLACE_ID + ") REFERENCES "
                    + TablePlaces.TABLE_NAME + "(" + TablePlaces._ID + ")" +
                    " )";

    private static final String SQL_CREATE_EVENTS_TABLE =
            "CREATE TABLE " + TableEvents.TABLE_NAME + " (" +
                    TableEvents._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableEvents.COLUMN_EVENT_NAME + TYPE_TEXT + COMMA_SEP +
                    TableEvents.COLUMN_EVENT_TYPE + TYPE_INTEGER + COMMA_SEP +
                    TableEvents.COLUMN_EVENT_VALUE + TYPE_INTEGER + COMMA_SEP +
                    TableEvents.COLUMN_EVENT_ACTIVE + TYPE_INTEGER + COMMA_SEP +
                    TableEvents.COLUMN_EVENT_REPEAT_TYPE + TYPE_INTEGER + COMMA_SEP +
                    TableEvents.COLUMN_EVENT_PLACE_ID + TYPE_INTEGER + " )";

    private static final String SQL_CREATE_DEVICE_EVENTS_TABLE =
            "CREATE TABLE " + TableDeviceEvents.TABLE_NAME + " (" +
                    TableDeviceEvents._ID + TYPE_INTEGER + COMMA_SEP +
                    TableDeviceEvents.COLUMN_DEVICE_EVENT_ID + TYPE_INTEGER + COMMA_SEP +
                    TableDeviceEvents.COLUMN_DEVICE_ID + TYPE_INTEGER + COMMA_SEP +
                    TableDeviceEvents.COLUMN_EVENT_TIME + TYPE_INTEGER + COMMA_SEP +
                    TableDeviceEvents.COLUMN_EVENT_REPEAT_MILLS + TYPE_INTEGER + COMMA_SEP +
                    " PRIMARY KEY (" + TableDeviceEvents.COLUMN_DEVICE_EVENT_ID + COMMA_SEP + TableDeviceEvents.COLUMN_DEVICE_ID + ")" + COMMA_SEP +
                    " FOREIGN KEY (" + TableDeviceEvents._ID + ") REFERENCES " + TableEvents.TABLE_NAME + "(" + TableEvents._ID + ") ON DELETE CASCADE);";

    private static final String SQL_CREATE_SETTINGS_TABLE =
            "CREATE TABLE " + TableSettings.TABLE_NAME + " (" +
                    TableSettings._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableSettings.COLUMN_NAME_CONCURRENT_CONNECTIONS + TYPE_INTEGER + COMMA_SEP +
                    TableSettings.COLUMN_NAME_LISTENING_MODE + TYPE_INTEGER + COMMA_SEP +
                    TableSettings.COLUMN_NAME_RETRY_COUNT + TYPE_INTEGER + COMMA_SEP +
                    TableSettings.COLUMN_NAME_RETRY_INTERVAL + TYPE_INTEGER + COMMA_SEP +
                    TableSettings.COLUMN_NAME_CLOUD_MESH_ID + TYPE_TEXT + COMMA_SEP +
                    TableSettings.COLUMN_NAME_CLOUD_TENANT_ID + TYPE_TEXT + " )";

    private static final String SQL_CREATE_PLACES_TABLE =
            "CREATE TABLE " + TablePlaces.TABLE_NAME + " (" +
                    TablePlaces._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TablePlaces.COLUMN_NAME_NAME + TYPE_TEXT + COMMA_SEP +
                    TablePlaces.COLUMN_NAME_PASSPHRASE + TYPE_TEXT + COMMA_SEP +
                    TablePlaces.COLUMN_NAME_NETWORK_KEY + TYPE_BLOB + COMMA_SEP +
                    TablePlaces.COLUMN_NAME_CLOUD_SITE_ID_KEY + TYPE_TEXT + COMMA_SEP +
                    TablePlaces.COLUMN_NAME_ICON_ID + TYPE_INTEGER + COMMA_SEP +
                    TablePlaces.COLUMN_NAME_COLOR + TYPE_INTEGER + COMMA_SEP +
                    TablePlaces.COLUMN_NAME_HOST_CONTROLLER_ID + TYPE_INTEGER + COMMA_SEP +

                    TablePlaces.COLUMN_NAME_SETTINGS_ID + TYPE_INTEGER + COMMA_SEP +
                    "FOREIGN KEY (" + TablePlaces.COLUMN_NAME_SETTINGS_ID + ") REFERENCES "
                    + TableSettings.TABLE_NAME + "(" + TableSettings._ID + ")" +
                    " )";

    private static final String SQL_CREATE_GATEWAY_TABLE =
            "CREATE TABLE " + TableGateways.TABLE_NAME + " (" +
                    TableGateways._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableGateways.COLUMN_NAME_NAME + TYPE_TEXT + COMMA_SEP +
                    TableGateways.COLUMN_NAME_HOST + TYPE_TEXT + COMMA_SEP +
                    TableGateways.COLUMN_NAME_PORT + TYPE_TEXT + COMMA_SEP +
                    TableGateways.COLUMN_NAME_UUID + TYPE_TEXT + COMMA_SEP +
                    TableGateways.COLUMN_NAME_BASEPATH + TYPE_TEXT + COMMA_SEP +
                    TableGateways.COLUMN_NAME_STATE + TYPE_INTEGER + COMMA_SEP +
                    TableGateways.COLUMN_NAME_DHM_KEY + TYPE_BLOB + COMMA_SEP +
                    TableGateways.COLUMN_NAME_DEVICE_HASH + TYPE_INTEGER + COMMA_SEP +
                    TableGateways.COLUMN_NAME_DEVICE_ID + TYPE_INTEGER + COMMA_SEP +

                    TableGateways.COLUMN_NAME_PLACE_ID + TYPE_INTEGER + COMMA_SEP +
                    "FOREIGN KEY (" + TableGateways.COLUMN_NAME_PLACE_ID +
                        ") REFERENCES " + TablePlaces.TABLE_NAME + "(" + TablePlaces._ID + ")" +
                    " )";

    private static final String SQL_CREATE_CONTROLLER_TABLE =
            "CREATE TABLE " + TableControllers.TABLE_NAME + " (" +
                    TableControllers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableControllers.COLUMN_NAME_NAME + TYPE_TEXT + COMMA_SEP +
                    TableControllers.COLUMN_NAME_UPDATE_DATE + TYPE_INTEGER + COMMA_SEP +
                    TableControllers.COLUMN_NAME_DEVICE_HASH + TYPE_INTEGER + COMMA_SEP +
                    TableControllers.COLUMN_NAME_UUID + TYPE_TEXT + COMMA_SEP +
                    TableControllers.COLUMN_NAME_DM_KEY + TYPE_BLOB + COMMA_SEP +
                    TableControllers.COLUMN_NAME_AUTH_CODE + TYPE_INTEGER + COMMA_SEP +
                    TableControllers.COLUMN_NAME_IS_ASSOCIATED + TYPE_INTEGER + COMMA_SEP +
                    TableControllers.COLUMN_NAME_DEVICE_ID + TYPE_INTEGER + COMMA_SEP +
                    TableControllers.COLUMN_NAME_PLACE_ID + TYPE_INTEGER + COMMA_SEP +
                    "FOREIGN KEY (" + TableControllers.COLUMN_NAME_PLACE_ID +
                    ") REFERENCES " + TablePlaces.TABLE_NAME + "(" + TablePlaces._ID + ")" +
                    " )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.w(TAG, "Creating database...");

        // Create tables
        db.execSQL(SQL_CREATE_DEVICES_TABLE);
        db.execSQL(SQL_CREATE_AREAS_TABLE);
        db.execSQL(SQL_CREATE_PLACES_TABLE);
        db.execSQL(SQL_CREATE_EVENTS_TABLE);
        db.execSQL(SQL_CREATE_DEVICE_EVENTS_TABLE);
        db.execSQL(SQL_CREATE_SETTINGS_TABLE);
        db.execSQL(SQL_CREATE_GATEWAY_TABLE);
        db.execSQL(SQL_CREATE_CONTROLLER_TABLE);

        // Populate tables with default inserts
        String insertDatabaseSql = getInsertsDatabaseSql();

        // split by semicolon
        String[] sqlStrings = insertDatabaseSql.split("[;]");

        for (String sqlString : sqlStrings) {
            if (!TextUtils.isEmpty(sqlString)) {
                db.execSQL(sqlString);
            }
        }
    }

    private String getInsertsDatabaseSql() {
        InputStream inputStream;

        try {
            inputStream = mContext.getAssets().open("database/tables_inserts.sql");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }

            return stringBuilder.toString();
        }
        catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return "";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed, all data will be gone!!! Shall we retrieve the data first?
        db.execSQL("DROP TABLE IF EXISTS " + TableDevices.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableAreas.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TablePlaces.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableEvents.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableDeviceEvents.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableSettings.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableGateways.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableControllers.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }
}
