/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.wyzk.lottery.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.R;
import com.wyzk.lottery.api.MeshLibraryManager;
import com.wyzk.lottery.database.tables.TableAreas;
import com.wyzk.lottery.database.tables.TableControllers;
import com.wyzk.lottery.database.tables.TableDeviceEvents;
import com.wyzk.lottery.database.tables.TableDevices;
import com.wyzk.lottery.database.tables.TableEvents;
import com.wyzk.lottery.database.tables.TableGateways;
import com.wyzk.lottery.database.tables.TableSettings;
import com.wyzk.lottery.event.DeviceEvent;
import com.wyzk.lottery.event.Event;
import com.wyzk.lottery.event.MeshSystemEvent;
import com.wyzk.lottery.model.Area;
import com.wyzk.lottery.model.Device;
import com.wyzk.lottery.model.Gateway;
import com.wyzk.lottery.model.Setting;
import com.wyzk.lottery.utils.ApplicationUtils;
import com.wyzk.lottery.utils.ByteUtils;
import com.wyzk.lottery.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Class used to access to the database. This class also contains the methods to retrieve info from the database.
 */
public class DBManager {

    // Logcat tag
    private static final String TAG = "DBDataSource";

    private LotteryApplication mApp;

    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;
    private static DBManager mDBManager;

    public static DBManager getInstence(LotteryApplication app) {
        if (mDBManager == null) {
            mDBManager = new DBManager(app);
        }
        return mDBManager;
    }

    public DBManager(LotteryApplication app) {
        this.mApp = app;
        mDBHelper = new DBHelper(mApp);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // DEVICES
    ///////////////////////////////////////////////////////////////////////////////////////////////
//
//    public synchronized List<Device> getDevicesInArea(int areaId) {
//        List<Device> deviceList = new ArrayList<>();
//
//        List<Device> allDevices = getAllDevicesList();
//        Area area = getArea(areaId);
//        if (area == null) {
//            return null;
//        }
//
//        for (int i = 0; i < allDevices.size(); i++) {
//            Device device = allDevices.get(i);
//            if (device.getGroupsList().contains(area.getAreaID())) {
//                deviceList.add(device);
//            }
//        }
//
//        return deviceList;
//    }


    private Cursor getAllDevicesIDsCursor() {

        String sortOrder = TableDevices.COLUMN_NAME_DEVICE_ID + " ASC";
        String[] columns = {
                TableDevices.COLUMN_NAME_DEVICE_ID
        };

        String queryString = mApp.getString(R.string.sql_where_clause_place_id);
        String[] queryParameters = new String[]{String.valueOf(Utils.getLatestPlaceIdUsed().getPlaceId())};

        try {
            return executeSelect(TableDevices.TABLE_NAME, columns, queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Integer> getAllDevicesAndControllersIDsList() {

        open();

        List<Integer> idsList = new ArrayList<>();
        Cursor cursorDevices = getAllDevicesIDsCursor();

        if (cursorDevices != null) {
            while (cursorDevices.moveToNext()) {

                int deviceId = cursorDevices.getInt(cursorDevices.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_DEVICE_ID));
                idsList.add(deviceId);
            }
            cursorDevices.close();
        }


        Cursor cursorController = getAllControllersIDsCursor(false);
        if (cursorController != null) {
            while (cursorController.moveToNext()) {

                int deviceId = cursorController.getInt(cursorController.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_DEVICE_ID));
                idsList.add(deviceId);
            }
            cursorController.close();
        }

        close();

        Collections.sort(idsList);

        return idsList;
    }


    private Cursor getAllFavDevicesCursor() {

        String sortOrder = TableDevices._ID + " ASC";

        String queryString = mApp.getString(R.string.sql_where_clause_fav_device_id);
        String[] queryParameters = new String[]{"1", String.valueOf(Utils.getLatestPlaceIdUsed())};

        try {
            return executeSelect(TableDevices.TABLE_NAME, TableDevices.getColumnsNames(), queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private Cursor getAllDevicesCursor() {

        String sortOrder = TableDevices._ID + " ASC";

        String queryString = mApp.getString(R.string.sql_where_clause_place_id);
        if (Utils.getLatestPlaceIdUsed() != null) {
            String[] queryParameters = new String[]{String.valueOf(Utils.getLatestPlaceIdUsed().getPlaceId())};

            try {
                return executeSelect(TableDevices.TABLE_NAME, TableDevices.getColumnsNames(), queryString, queryParameters, sortOrder);
            } catch (SQLException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return null;
    }

    private Cursor getAllEventsByTypeCursor(int type) {

        String sortOrder = TableEvents._ID + " ASC";

        String queryString = mApp.getString(R.string.sql_where_clause_events_id);
        String[] queryParameters = new String[]{type + "", String.valueOf(Utils.getLatestPlaceIdUsed())};

        try {
            return executeSelect(TableEvents.TABLE_NAME, TableEvents.getColumnsNames(), queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private Cursor getAllDeviceEventCursor(int eventId) {

        String sortOrder = TableEvents._ID + " ASC";

        String queryString = mApp.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{eventId + ""};

        try {
            return executeSelect(TableDeviceEvents.TABLE_NAME, TableDeviceEvents.getColumnsNames(), queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Device> getAllFavDevicesList() {
        open();


        List<Device> devicesList = new ArrayList<>();

        Cursor cursor = getAllFavDevicesCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Device device = TableDevices.getDeviceFromCursor(cursor);
                devicesList.add(device);
            }
            cursor.close();
        }
        close();

        devicesList = ApplicationUtils.sortDevicesListAlphabetically(devicesList);

        return devicesList;
    }

    public synchronized List<Device> getAllDevicesList() {

        open();

        List<Device> devicesList = new ArrayList<>();

        Cursor cursor = getAllDevicesCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Device device = TableDevices.getDeviceFromCursor(cursor);
                devicesList.add(device);
            }
            cursor.close();
        }
        close();

        devicesList = ApplicationUtils.sortDevicesListAlphabetically(devicesList);

        return devicesList;
    }


    private Cursor getDeviceCursor(final long id) {

        String queryString = mApp.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableDevices.TABLE_NAME, TableDevices.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Device getDevice(final long id) {

        open();

        Device device = null;
        Cursor cursor = getDeviceCursor(id);


        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // RETRIEVE DEVICE STATES
                device = TableDevices.getDeviceFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return device;
    }

    String KEY_DEVICES_LIST = "devices_list";
    String KEY_DEVICE_ID = "deviceId";
    String KEY_DEVICE_NAME = "name";
    String KEY_DEVICE_IS_ASSOCIATED = "isAssociated";
    String KEY_DEVICE_HASH = "hash";
    String KEY_DEVICE_APPEARANCE = "appearance";
    String KEY_DEVICE_MODEL_LOW = "modelLow";
    String KEY_DEVICE_MODEL_HIGH = "modelHigh";
    String KEY_DEVICE_UUID = "uuid";
    String KEY_DEVICE_NUM_GROUPS = "numgroups";
    String KEY_DEVICE_GROUPS = "groups";
    String KEY_DEVICE_IS_FAVOURITE = "isFavourite";
    String KEY_DEVICE_DHM_KEY = "dhmKey";
    String KEY_DEVICE_AUTH_CODE = "authCode";

    String KEY_AREAS_LIST = "areas_list";
    String KEY_AREA_NAME = "name";
    String KEY_AREA_ID = "areaID";
    String KEY_AREA_IS_FAVOURITE = "isFavourite";

    String KEY_GATEWAYS_LIST = "gateways_list";
    String KEY_GATEWAY_NAME = "name";
    String KEY_GATEWAY_HOST = "host";
    String KEY_GATEWAY_PORT = "port";
    String KEY_GATEWAY_UUID = "uuid";
    String KEY_GATEWAY_BASE_PATH = "basePath";
    String KEY_GATEWAY_STATE = "state";
    String KEY_GATEWAY_DM_KEY = "dmKey";
    String KEY_GATEWAY_DEVICE_HASH = "deviceHash";
    String KEY_GATEWAY_DEVICE_ID = "deviceId";

    String KEY_REST_LIST = "rest_list";
    String KEY_REST_MESH_ID = "cloudMeshID";
    String KEY_REST_SITE = "cloudSiteID";
    String KEY_REST_TENANT = "cloudTenantID";


    /**
     * Get the database as a json format.
     *
     * @return json containing the database.
     */
    public synchronized String getDataBaseAsJson() {
        JSONObject objJson = new JSONObject();

        try {

            // Devices.
            JSONArray jsonDevices = new JSONArray();
            List<Device> devices = getAllDevicesList();

            for (int i = 0; i < devices.size(); i++) {
                Device device = devices.get(i);

                JSONObject deviceJson = new JSONObject();
                deviceJson.put(KEY_DEVICE_ID, device.getDeviceId());
                deviceJson.put(KEY_DEVICE_NAME, device.getName());
                deviceJson.put(KEY_DEVICE_IS_ASSOCIATED, device.isAssociated() ? 1 : 0);
                deviceJson.put(KEY_DEVICE_HASH, device.getDeviceHash());
                deviceJson.put(KEY_DEVICE_APPEARANCE, device.getAppearance());
                deviceJson.put(KEY_DEVICE_MODEL_LOW, device.getModelLow());
                deviceJson.put(KEY_DEVICE_MODEL_HIGH, device.getModelHigh());
                deviceJson.put(KEY_DEVICE_DHM_KEY, Base64.encodeToString(ByteUtils.hexStringToBytes(device.getDmKey()), Base64.DEFAULT));
                deviceJson.put(KEY_DEVICE_NUM_GROUPS, device.getNumGroups());

//                 Array of groups
//                JSONArray jsonGroups = new JSONArray();
//                int[] groups = device.getGroups();
//                for (int j = 0; j < groups.length; j++) {
//                    jsonGroups.put(groups[j]);
//                }
//                deviceJson.put(KEY_DEVICE_GROUPS, jsonGroups);

//                deviceJson.put(KEY_DEVICE_UUID, Base64.encodeToString(device.getUuid(), Base64.DEFAULT));
                deviceJson.put(KEY_DEVICE_AUTH_CODE, device.getAuthCode());

                jsonDevices.put(deviceJson);
            }
            objJson.put(KEY_DEVICES_LIST, jsonDevices);

            // Areas
            JSONArray jsonAreas = new JSONArray();
            List<Area> areas = getAllAreaList();

            for (int i = 0; i < areas.size(); i++) {
                Area area = areas.get(i);
                JSONObject areaJson = new JSONObject();
                areaJson.put(KEY_AREA_NAME, area.getName());
                areaJson.put(KEY_AREA_ID, area.getAreaID());
                areaJson.put(KEY_AREA_IS_FAVOURITE, area.isFavorite() ? 1 : 0);

                jsonAreas.put(areaJson);
            }
            objJson.put(KEY_AREAS_LIST, jsonAreas);

            // GATEWAYS
            JSONArray jsonGateway = new JSONArray();
            List<Gateway> gateways = getAllGatewaysFromCurrentPlace();

            for (int i = 0; i < gateways.size(); i++) {
                Gateway gateway = gateways.get(i);
                JSONObject gatewayJson = new JSONObject();
                gatewayJson.put(KEY_GATEWAY_NAME, gateway.getName());
                gatewayJson.put(KEY_GATEWAY_HOST, gateway.getHost());
                gatewayJson.put(KEY_GATEWAY_PORT, gateway.getPort());
                gatewayJson.put(KEY_GATEWAY_UUID, gateway.getUuid());
                gatewayJson.put(KEY_GATEWAY_BASE_PATH, gateway.getBasePath());
                gatewayJson.put(KEY_GATEWAY_STATE, gateway.getState());
                gatewayJson.put(KEY_GATEWAY_DM_KEY, Base64.encodeToString(gateway.getDhmKey(), Base64.DEFAULT));
                gatewayJson.put(KEY_GATEWAY_DEVICE_HASH, gateway.getDeviceHash());
                gatewayJson.put(KEY_GATEWAY_DEVICE_ID, gateway.getDeviceID());

                jsonGateway.put(gatewayJson);
            }
            objJson.put(KEY_GATEWAYS_LIST, jsonGateway);


            // REST
            JSONArray jsonRest = new JSONArray();

            String tenantId = getFirstSetting().getCloudTenantId();
//            String siteId = getPlace(Utils.getLatestPlaceIdUsed(mApp)).getCloudSiteID();
            String meshId = MeshLibraryManager.getInstance().getMeshId();
            JSONObject restJson = new JSONObject();
            restJson.put(KEY_REST_MESH_ID, meshId);
            restJson.put(KEY_REST_TENANT, tenantId);
//            restJson.put(KEY_REST_SITE, siteId);

            jsonRest.put(restJson);

            objJson.put(KEY_REST_LIST, jsonRest);

        } catch (Exception e) {
            return null;
        }

        return objJson.toString();
    }


    /**
     * Create a list of Rest values from JSON.
     *
     * @param json    JSON string containing the description of the rest settings.
     * @param placeId The place databaseId to use for the rest settings.
     */
    public synchronized void getListRestByJson(String json, int placeId) {

        try {
            JSONObject jsonObj = new JSONObject(json);

            JSONArray jsonArray = jsonObj.getJSONArray(KEY_REST_LIST);

            for (int restIndex = 0; restIndex < jsonArray.length(); restIndex++) {

                String meshId = jsonArray.getJSONObject(restIndex).getString(KEY_REST_MESH_ID);
                String tenantId = jsonArray.getJSONObject(restIndex).getString(KEY_REST_TENANT);
                String siteId = jsonArray.getJSONObject(restIndex).getString(KEY_REST_SITE);

                if (meshId.equals(MeshLibraryManager.getInstance().getMeshId())) {
                    Setting setting = getFirstSetting();
                    setting.setCloudTenantId(tenantId);
                    createOrUpdateSetting(setting);

//                    Place place = getPlace(Utils.getLatestPlaceIdUsed(mApp));
//                    place.setCloudSiteID(siteId);
//                    createOrUpdatePlace(place);

                    LotteryApplication.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.PLACE_CHANGED));
                }
            }
        } catch (JSONException e) {
            Log.e("DBManager", e.getMessage());
        }
    }

    /**
     * Create a list of Gateway objects from JSON.
     *
     * @param json    JSON string containing the description of the gateways.
     * @param placeId The place databaseId to use for the gateways.
     * @return A list of Gateway objects.
     */
    public synchronized List<Gateway> getListGatewaysByJson(String json, int placeId) {
        List<Gateway> gateways = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(json);

            JSONArray jsonArray = jsonObj.getJSONArray(KEY_GATEWAYS_LIST);

            for (int gatewaysIndex = 0; gatewaysIndex < jsonArray.length(); gatewaysIndex++) {
                Gateway gateway = new Gateway();

                String gatewayName = jsonArray.getJSONObject(gatewaysIndex).getString(KEY_GATEWAY_NAME);
                gateway.setName(gatewayName);

                String gatewayHost = jsonArray.getJSONObject(gatewaysIndex).getString(KEY_GATEWAY_HOST);
                gateway.setHost(gatewayHost);

                String gatewayPort = jsonArray.getJSONObject(gatewaysIndex).getString(KEY_GATEWAY_PORT);
                gateway.setPort(gatewayPort);

                String gatewayUuid = jsonArray.getJSONObject(gatewaysIndex).getString(KEY_GATEWAY_UUID);
                gateway.setUuid(gatewayUuid.toLowerCase());

                String gatewayBasePath = jsonArray.getJSONObject(gatewaysIndex).getString(KEY_GATEWAY_BASE_PATH);
                gateway.setBasePath(gatewayBasePath);

                int gatewayState = jsonArray.getJSONObject(gatewaysIndex).getInt(KEY_GATEWAY_STATE);
                gateway.setState(gatewayState);

                byte[] dhm = Base64.decode(jsonArray.getJSONObject(gatewaysIndex).getString(KEY_GATEWAY_DM_KEY), Base64.DEFAULT);
                gateway.setDhmKey(dhm);

                int gatewayDeviceHash = jsonArray.getJSONObject(gatewaysIndex).getInt(KEY_GATEWAY_DEVICE_HASH);
                gateway.setDeviceHash(gatewayDeviceHash);

                int gatewayId = jsonArray.getJSONObject(gatewaysIndex).getInt(KEY_GATEWAY_DEVICE_ID);
                gateway.setDeviceID(gatewayId);

                gateway.setPlaceID(placeId);

                gateways.add(gateway);
            }
        } catch (JSONException e) {
            return null;
        }

        // If there were gateways imported retrieve REST settings
        if (gateways.size() > 0) {
            getListRestByJson(json, placeId);
        }

        return gateways;
    }


    /**
     * Create a list of Area objects from JSON.
     *
     * @param json    JSON string containing the description of the areas.
     * @param placeId The place databaseId to use for the areas.
     * @return A list of Area objects.
     */
    public synchronized List<Area> getListAreasByJson(String json, int placeId) {
        List<Area> areas = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(json);

            JSONArray jsonArray = jsonObj.getJSONArray(KEY_AREAS_LIST);

            for (int areasIndex = 0; areasIndex < jsonArray.length(); areasIndex++) {
                Area area = new Area();

                String areaName = jsonArray.getJSONObject(areasIndex).getString(KEY_AREA_NAME);
                area.setName(areaName);

                int areaId = jsonArray.getJSONObject(areasIndex).getInt(KEY_AREA_ID);
                area.setAreaID(areaId);

                boolean isFavourite = jsonArray.getJSONObject(areasIndex).getInt(KEY_AREA_IS_FAVOURITE) == 0 ? false : true;
                area.setIsFavorite(isFavourite);

                area.setPlaceID(placeId);

                areas.add(area);
            }
        } catch (JSONException e) {
            return null;
        }

        return areas;
    }

    /**
     * Create a list of Device objects from JSON.
     *
     * @param json    JSON string containing the description of the devices.
     * @param placeId The place databaseId to use for the devices.
     * @return A list of Device objects.
     */
    public synchronized List<Device> getListDevicesByJson(String json, int placeId) {
        List<Device> devices = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(json);

            JSONArray jsonArray = jsonObj.getJSONArray(KEY_DEVICES_LIST);

            for (int devicesIndex = 0; devicesIndex < jsonArray.length(); devicesIndex++) {

                UnknownDevice device = new UnknownDevice();

                int deviceId = jsonArray.getJSONObject(devicesIndex).getInt(KEY_DEVICE_ID);
                device.setDeviceId(deviceId);

                String deviceName = jsonArray.getJSONObject(devicesIndex).getString(KEY_DEVICE_NAME);
                device.setName(deviceName);

                int hash = jsonArray.getJSONObject(devicesIndex).getInt(KEY_DEVICE_HASH);
                device.setDeviceHash(hash);

                int appearance = jsonArray.getJSONObject(devicesIndex).getInt(KEY_DEVICE_APPEARANCE);
                device.setAppearance(appearance);

                long modelLow = jsonArray.getJSONObject(devicesIndex).getLong(KEY_DEVICE_MODEL_LOW);
                device.setModelLow(modelLow);

                long modelHigh = jsonArray.getJSONObject(devicesIndex).getLong(KEY_DEVICE_MODEL_HIGH);
                device.setModelHigh(modelHigh);
                boolean isAssociated = jsonArray.getJSONObject(devicesIndex).getInt(KEY_DEVICE_IS_ASSOCIATED) == 0 ? false : true;
                device.setAssociated(isAssociated);

                byte[] dhm = Base64.decode(jsonArray.getJSONObject(devicesIndex).getString(KEY_DEVICE_DHM_KEY), Base64.DEFAULT);
                device.setDmKey(ByteUtils.toHexString(dhm));

                int numGroups = jsonArray.getJSONObject(devicesIndex).getInt(KEY_DEVICE_NUM_GROUPS);
                device.setNumGroups(numGroups);

                JSONArray jsonGroupsArray = jsonArray.getJSONObject(devicesIndex).getJSONArray(KEY_DEVICE_GROUPS);
                if (jsonGroupsArray != null) {
                    int[] groups = new int[jsonGroupsArray.length()];

                    for (int i = 0; i < jsonGroupsArray.length(); i++) {
                        groups[i] = jsonGroupsArray.getInt(i);
                    }
//                    device.setGroups(groups);
                }


                /*byte[] uuid = Base64.decode(jsonArray.getJSONObject(devicesIndex).getString(KEY_DEVICE_UUID), Base64.DEFAULT);
                device.setUuid(uuid);*/

                long authCode = jsonArray.getJSONObject(devicesIndex).getLong(KEY_DEVICE_AUTH_CODE);
                device.setAuthCode(authCode);

                device.setPlaceID(placeId);

                devices.add(device);
            }


        } catch (JSONException e) {
            return null;
        }

        return devices;
    }

    public synchronized Device createOrUpdateDevice(Device device) {

        open();

        boolean response = false;
        Device updatedDevice = null;

        try {
            if ((device.getDatabaseId() != -1) && (getDevice(device.getDatabaseId()) != null)) {
                String queryString = mApp.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(device.getDatabaseId())};

                response = executeUpdate(TableDevices.TABLE_NAME, TableDevices.createContentValues(device), queryString, queryParameters);
                if (response) {
                    updatedDevice = getDevice(device.getDatabaseId());
                }

            } else {

                long idDeviceCreated = executeCreate(TableDevices.TABLE_NAME, TableDevices.createContentValues(device));
                if (idDeviceCreated >= 0) {
                    updatedDevice = getDevice(idDeviceCreated);
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return updatedDevice;
    }


    public synchronized boolean removeDevice(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableDevices.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        close();
        return response;
    }

    public synchronized boolean removeAllDevicesByPlaceId(int placeId) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_place_id);
            String[] queryParameters = new String[]{String.valueOf(placeId)};

            response = executeDelete(TableDevices.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return response;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // AREA
    ///////////////////////////////////////////////////////////////////////////////////////////////


    public synchronized int getNewAreaId() {
        int lastIdUsed = 0;
        List<Area> areas = getAllAreaList();
        for (int i = 0; i < areas.size(); i++) {
            lastIdUsed = Math.max(lastIdUsed, areas.get(i).getAreaID());
        }
        lastIdUsed++;

        return lastIdUsed;
    }

    private Cursor getAllAreasCursor() {

        String sortOrder = TableAreas._ID + " ASC";

        String queryString = mApp.getString(R.string.sql_where_clause_place_id);
        String[] queryParameters = new String[]{String.valueOf(Utils.getLatestPlaceIdUsed())};

        try {
            return executeSelect(TableAreas.TABLE_NAME, TableAreas.getColumnsNames(), queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private Cursor getFavAllAreasCursor() {

        String sortOrder = TableAreas._ID + " ASC";

        String queryString = mApp.getString(R.string.sql_where_clause_fav_device_id);
        String[] queryParameters = new String[]{"1", String.valueOf(Utils.getLatestPlaceIdUsed())};

        try {
            return executeSelect(TableAreas.TABLE_NAME, TableAreas.getColumnsNames(), queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Area> getAllAreaList() {

        open();

        List<Area> areaList = new ArrayList<>();

        Cursor cursor = getAllAreasCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Area area = TableAreas.getAreaFromCursor(cursor);
                areaList.add(area);
            }
            cursor.close();
        }
        close();

        return areaList;
    }

    public synchronized List<Area> getAllFavAreaList() {

        open();

        List<Area> areaList = new ArrayList<>();

        Cursor cursor = getFavAllAreasCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Area area = TableAreas.getAreaFromCursor(cursor);
                areaList.add(area);
            }
            cursor.close();
        }
        close();

        return areaList;
    }


    private Cursor getAreaCursor(final long id) {

        String queryString = mApp.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableAreas.TABLE_NAME, TableAreas.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Area getArea(final long id) {

        open();

        Area area = null;
        Cursor cursor = getAreaCursor(id);

        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                area = TableAreas.getAreaFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return area;
    }

    private Cursor getAreasOfPlaceWithIdCursor(final long placeID) {

        String queryString = mApp.getString(R.string.sql_where_clause_place_id);
        String[] queryParameters = new String[]{String.valueOf(placeID)};

        try {
            return executeSelect(TableAreas.TABLE_NAME, TableAreas.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Area> getAreasOfPlaceWithId(final long placeID) {

        open();

        List<Area> areaList = new ArrayList<>();

        Cursor cursor = getAreasOfPlaceWithIdCursor(placeID);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Area area = TableAreas.getAreaFromCursor(cursor);
                areaList.add(area);
            }
            cursor.close();
        }
        close();

        return areaList;
    }

    private Cursor getAreasByGroupIdCursor(final long areaId) {

        String queryString = mApp.getString(R.string.sql_where_clause_area_id);
        String[] queryParameters = new String[]{String.valueOf(areaId), String.valueOf(Utils.getLatestPlaceIdUsed())};

        try {
            return executeSelect(TableAreas.TABLE_NAME, TableAreas.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Area> getAreasbyGroupsId(List<Integer> ids) {
        open();

        List<Area> areaList = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            Cursor cursor = getAreasByGroupIdCursor(ids.get(i));

            if (cursor != null && cursor.moveToNext()) {
                Area area = TableAreas.getAreaFromCursor(cursor);
                areaList.add(area);
                cursor.close();
            }
            close();
        }
        return areaList;
    }

    public synchronized Area createOrUpdateArea(Area area) {

        open();

        boolean response = false;
        Area updatedArea = null;

        try {
            if ((area.getId() != -1) && (getArea(area.getId()) != null)) {
                String queryString = mApp.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(area.getId())};

                response = executeUpdate(TableAreas.TABLE_NAME, TableAreas.createContentValues(area), queryString, queryParameters);
                if (response) {
                    updatedArea = getArea(area.getId());
                }

            } else {

                long idAreaCreated = executeCreate(TableAreas.TABLE_NAME, TableAreas.createContentValues(area));
                if (idAreaCreated >= 0) {
                    updatedArea = getArea(idAreaCreated);
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return updatedArea;
    }

    public synchronized boolean removeArea(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableAreas.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return response;
    }

    public synchronized boolean removeAreaOfPlaceWithId(final int placeID) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_place_id);
            String[] queryParameters = new String[]{String.valueOf(placeID)};

            response = executeDelete(TableAreas.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return response;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // PLACE
    ///////////////////////////////////////////////////////////////////////////////////////////////


//    private Cursor getAllPlacesCursor() {
//
//        String sortOrder = TablePlaces._ID + " ASC";
//
//        try {
//            return executeSelect(TablePlaces.TABLE_NAME, TablePlaces.getColumnsNames(), null, null, sortOrder);
//        }
//        catch (SQLException e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
//        return null;
//    }
//
//    public synchronized List<Place> getAllPlacesList() {
//
//        open();
//
//        List<Place> placeList = new ArrayList<>();
//
//        Cursor cursor = getAllPlacesCursor();
//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//
//                Place place = TablePlaces.getPlaceFromCursor(cursor);
//                placeList.add(place);
//            }
//            cursor.close();
//        }
//        close();
//
//        return placeList;
//    }
//
//
//    private Cursor getPlaceCursor(final long id) {
//
//        String queryString = mApp.getString(R.string.sql_where_clause_id);
//        String[] queryParameters = new String[]{String.valueOf(id)};
//
//        try {
//            return executeSelect(TablePlaces.TABLE_NAME, TablePlaces.getColumnsNames(), queryString, queryParameters, null);
//        }
//        catch (SQLException e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
//        return null;
//    }
//
//    public synchronized Place getPlace(final long id) {
//
//        open();
//
//        Place place = null;
//        Cursor cursor = getPlaceCursor(id);
//
//        if (cursor != null) {
//            if (cursor.getCount() == 1) {
//                cursor.moveToFirst();
//
//                // RETRIEVE Place STATES
//                place = TablePlaces.getPlaceFromCursor(cursor);
//            }
//            cursor.close();
//        }
//        close();
//        if (place != null){
//            Log.d(TAG, "DbManager getPlace =" + place.toString());
//        }else{
//            Log.d(TAG, "DbManager getPlace = null" );
//        }
//
//        return place;
//    }
//
//
//    public synchronized Place createOrUpdatePlace(Place place) {
//        Log.d(TAG, "DbManager createOrUpdatePlace =" + place.toString());
//        open();
//
//        boolean response = false;
//        Place updatedPlace = null;
//
//        try {
//            if ((place.getId() != -1) && (getPlace(place.getId()) != null)) {
//                String queryString = mApp.getString(R.string.sql_where_clause_id);
//                String[] queryParameters = new String[]{String.valueOf(place.getId())};
//
//                response = executeUpdate(TablePlaces.TABLE_NAME, TablePlaces.createContentValues(place), queryString, queryParameters);
//                if (response) {
//                    updatedPlace = getPlace(place.getId());
//                }
//
//            }
//            else {
//
//                long idPlaceCreated = executeCreate(TablePlaces.TABLE_NAME, TablePlaces.createContentValues(place));
//                if (idPlaceCreated >= 0) {
//                    updatedPlace = getPlace(idPlaceCreated);
//                }
//            }
//
//        }
//        catch (SQLException e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
//
//        close();
//
//        return updatedPlace;
//    }
//
//
//    public synchronized boolean removePlace(final int id) {
//
//        open();
//
//        boolean response = false;
//        try {
//            String queryString = mApp.getString(R.string.sql_where_clause_id);
//            String[] queryParameters = new String[]{String.valueOf(id)};
//
//            response = executeDelete(TablePlaces.TABLE_NAME, queryString, queryParameters);
//
//        }
//        catch (SQLException e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
//
//        close();
//
//        return response;
//    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // SETTING
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Cursor getAllSettingsCursor() {

        String sortOrder = TableSettings._ID + " ASC";

        try {
            return executeSelect(TableSettings.TABLE_NAME, TableSettings.getColumnsNames(), null, null, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Setting> getAllSettingList() {

        open();

        List<Setting> settingsList = new ArrayList<>();

        Cursor cursor = getAllSettingsCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Setting setting = TableSettings.getSettingFromCursor(cursor);
                settingsList.add(setting);
            }
            cursor.close();
        }
        close();

        return settingsList;
    }


    private Cursor getSettingCursor(final long id) {

        String queryString = mApp.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableSettings.TABLE_NAME, TableSettings.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Setting getSetting(final long id) {

        open();

        Setting setting = null;
        Cursor cursor = getSettingCursor(id);

        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                setting = TableSettings.getSettingFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return setting;
    }

    public synchronized Setting getFirstSetting() {
        open();

        Setting setting = null;
        Cursor cursor = getAllSettingsCursor();

        if (cursor != null) {
            cursor.moveToFirst();
            setting = TableSettings.getSettingFromCursor(cursor);
            cursor.close();
        }
        close();

        return setting;
    }


    public synchronized Setting createOrUpdateSetting(Setting setting) {

        open();

        boolean response = false;
        Setting updatedSetting = null;

        try {
            if ((setting.getId() != -1) && (getSetting(setting.getId()) != null)) {
                String queryString = mApp.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(setting.getId())};

                response = executeUpdate(TableSettings.TABLE_NAME, TableSettings.createContentValues(setting), queryString, queryParameters);
                if (response) {
                    updatedSetting = getSetting(setting.getId());
                }

            } else {

                long idSettingCreated = executeCreate(TableSettings.TABLE_NAME, TableSettings.createContentValues(setting));
                if (idSettingCreated >= 0) {
                    updatedSetting = getSetting(idSettingCreated);
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return updatedSetting;
    }

    public synchronized boolean removeSetting(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableSettings.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return response;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // GATEWAYS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Cursor getAllGatewaysIDsCursor(final boolean justFromSelectedPlace) {

        String sortOrder = TableGateways.COLUMN_NAME_DEVICE_ID + " ASC";
        String[] columns = {
                TableGateways.COLUMN_NAME_DEVICE_ID
        };

        String queryString = justFromSelectedPlace ? mApp.getString(R.string.sql_where_clause_place_id) : null;
        String[] queryParameters = justFromSelectedPlace ? new String[]{String.valueOf(Utils.getLatestPlaceIdUsed())} : null;

        try {
            return executeSelect(TableGateways.TABLE_NAME, columns, queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Integer> getAllGatewaysIDsList() {

        return getAllGatewaysIDsList(false);
    }

    public synchronized List<Integer> getAllGatewaysIDsFromCurrentPlace() {

        return getAllGatewaysIDsList(true);
    }

    private List<Integer> getAllGatewaysIDsList(final boolean justFromSelectedPlace) {

        open();

        List<Integer> idsList = new ArrayList<>();
        Cursor cursor = getAllGatewaysIDsCursor(justFromSelectedPlace);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                int deviceID = cursor.getInt(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_DEVICE_ID));
                idsList.add(deviceID);
            }
            cursor.close();
        }
        close();

        return idsList;
    }


    private Cursor getAllGatewaysCursor(final boolean justFromSelectedPlace) {

        String sortOrder = TableGateways._ID + " ASC";

        String queryString = justFromSelectedPlace ? mApp.getString(R.string.sql_where_clause_place_id) : null;
        String[] queryParameters = justFromSelectedPlace ? new String[]{String.valueOf(Utils.getLatestPlaceIdUsed())} : null;

        try {
            return executeSelect(TableGateways.TABLE_NAME, TableGateways.getColumnsNames(), queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }


    public synchronized List<Gateway> getAllGatewaysList() {

        return getAllGatewaysList(false);
    }

    public synchronized List<Gateway> getAllGatewaysFromCurrentPlace() {

        return getAllGatewaysList(true);
    }

    private List<Gateway> getAllGatewaysList(final boolean justFromSelectedPlace) {

        open();

        List<Gateway> gatewaysList = new ArrayList<>();

        Cursor cursor = getAllGatewaysCursor(justFromSelectedPlace);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Gateway gateway = TableGateways.getGatewayFromCursor(cursor);
                gatewaysList.add(gateway);
            }
            cursor.close();
        }
        close();

        gatewaysList = ApplicationUtils.sortGatewaysListAlphabetically(gatewaysList);

        return gatewaysList;
    }


    private Cursor getNewestGatewayIdCursor() {
        String sortOrder = TableGateways.COLUMN_NAME_DEVICE_ID + " ASC";

        String queryString = null;
        String[] queryParameters = null;

        try {
            return executeSelect(TableGateways.TABLE_NAME, TableGateways.getColumnsNames(), queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized int getNewestGatewayId() {

        open();

        int newestGatewayId = 0;
        Cursor cursor = getNewestGatewayIdCursor();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            // RETRIEVE Gateway
            Gateway gateway = TableGateways.getGatewayFromCursor(cursor);
            newestGatewayId = gateway.getDeviceID();
            cursor.close();
        }

        close();

        return newestGatewayId;
    }


    private Cursor getGatewayCursor(final long id) {

        String queryString = mApp.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableGateways.TABLE_NAME, TableGateways.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Gateway getGateway(final long id) {

        open();

        Gateway gateway = null;
        Cursor cursor = getGatewayCursor(id);


        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // RETRIEVE Gateway STATES
                gateway = TableGateways.getGatewayFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return gateway;
    }

    private Cursor getGatewayWithUUIDCursor(final String uuid) {

        String queryString = mApp.getString(R.string.sql_where_clause_uuid);
        String[] queryParameters = new String[]{String.valueOf(uuid)};

        try {
            return executeSelect(TableGateways.TABLE_NAME, TableGateways.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Gateway getSelectedGateway() {

        open();

        Gateway gateway = null;
        Cursor cursor = getGatewayWithUUIDCursor(MeshLibraryManager.getInstance().getSelectedGatewayUUID());


        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // RETRIEVE Gateway STATES
                gateway = TableGateways.getGatewayFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return gateway;
    }

    private Cursor getGatewayWithDeviceIdCursor(final long id) {

        String queryString = mApp.getString(R.string.sql_where_clause_device_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableGateways.TABLE_NAME, TableGateways.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Gateway getGatewayWithDeviceId(final long id) {

        open();

        Gateway gateway = null;
        Cursor cursor = getGatewayWithDeviceIdCursor(id);


        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // RETRIEVE Gateway STATES
                gateway = TableGateways.getGatewayFromCursor(cursor);
            } else {
                Log.e(TAG, "More than one GW found with that deviceID!");
            }
            cursor.close();
        }
        close();

        return gateway;
    }


    public synchronized Gateway createOrUpdateGateway(Gateway gateway) {

        open();

        boolean response = false;
        Gateway updatedGateway = null;

        try {
            if ((gateway.getId() != -1) && (getGateway(gateway.getId()) != null)) {
                String queryString = mApp.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(gateway.getId())};

                response = executeUpdate(TableGateways.TABLE_NAME, TableGateways.createContentValues(gateway), queryString, queryParameters);
                if (response) {
                    updatedGateway = getGateway(gateway.getId());
                }

            } else {

                long idGatewayCreated = executeCreate(TableGateways.TABLE_NAME, TableGateways.createContentValues(gateway));
                if (idGatewayCreated >= 0) {
                    updatedGateway = getGateway(idGatewayCreated);
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return updatedGateway;
    }


    public synchronized boolean removeGateway(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableGateways.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return response;
    }

    public synchronized boolean removeAllGatewaysByPlaceId(int placeId) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_place_id);
            String[] queryParameters = new String[]{String.valueOf(placeId)};

            response = executeDelete(TableGateways.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return response;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROLLERS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Cursor getAllControllersIDsCursor(final boolean justFromSelectedPlace) {

        String sortOrder = TableControllers.COLUMN_NAME_DEVICE_ID + " ASC";
        String[] columns = {
                TableControllers.COLUMN_NAME_DEVICE_ID
        };

        String queryString = justFromSelectedPlace ? mApp.getString(R.string.sql_where_clause_place_id) : null;
        String[] queryParameters = justFromSelectedPlace ? new String[]{String.valueOf(Utils.getLatestPlaceIdUsed())} : null;

        try {
            return executeSelect(TableControllers.TABLE_NAME, columns, queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Integer> getAllControllersIDsList() {

        return getAllControllersIDsList(false);
    }

    public synchronized List<Integer> getAllControllersIDsFromCurrentPlace() {

        return getAllControllersIDsList(true);
    }

    private List<Integer> getAllControllersIDsList(final boolean justFromSelectedPlace) {

        open();

        List<Integer> idsList = new ArrayList<>();
        Cursor cursor = getAllControllersIDsCursor(justFromSelectedPlace);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                int deviceID = cursor.getInt(cursor.getColumnIndexOrThrow(TableControllers.COLUMN_NAME_DEVICE_ID));
                idsList.add(deviceID);
            }
            cursor.close();
        }
        close();

        return idsList;
    }


    private Cursor getAllControllersCursor(final boolean justFromSelectedPlace) {

        String sortOrder = TableControllers._ID + " ASC";

        String queryString = justFromSelectedPlace ? mApp.getString(R.string.sql_where_clause_place_id) : null;
        String[] queryParameters = justFromSelectedPlace ? new String[]{String.valueOf(Utils.getLatestPlaceIdUsed())} : null;

        try {
            return executeSelect(TableControllers.TABLE_NAME, TableControllers.getColumnsNames(), queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private Cursor getControllerCursor(final long id) {

        String queryString = mApp.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableControllers.TABLE_NAME, TableControllers.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private Cursor getControllerWithDeviceIdCursor(final long id) {

        String queryString = mApp.getString(R.string.sql_where_clause_device_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableControllers.TABLE_NAME, TableControllers.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }


    public synchronized boolean removeController(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableControllers.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return response;
    }

    public synchronized boolean removeAllControllersByPlaceId(int placeId) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_place_id);
            String[] queryParameters = new String[]{String.valueOf(placeId)};

            response = executeDelete(TableControllers.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return response;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // EVENTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Cursor getAllEventsCursor() {

        String sortOrder = TableEvents._ID + " ASC";

        String queryString = mApp.getString(R.string.sql_where_clause_place_id);
        String[] queryParameters = new String[]{String.valueOf(Utils.getLatestPlaceIdUsed())};

        try {
            return executeSelect(TableEvents.TABLE_NAME, TableEvents.getColumnsNames(), queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Event> getAllEvents() {
        open();

        List<Event> eventsList = new ArrayList<>();

        Cursor cursor = getAllEventsCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Event event = TableEvents.getEventFromCursor(cursor);
                // Get EventDevices from the database.
                {
                    Cursor deviceEventCursor = getDeviceEventCursor(event.getId());
                    if (deviceEventCursor != null) {
                        while (deviceEventCursor.moveToNext()) {

                            DeviceEvent deviceEvent = TableDeviceEvents.getDeviceEventFromCursor(deviceEventCursor);
                            event.getDeviceEvents().add(deviceEvent);
                        }
                    }
                }
                eventsList.add(event);
            }
            cursor.close();
        }
        close();

        return eventsList;
    }

    private Cursor getEventCursor(final long id) {

        String queryString = mApp.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableEvents.TABLE_NAME, TableEvents.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private Cursor getDeviceEventCursor(final long id) {

        String queryString = mApp.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableDeviceEvents.TABLE_NAME, TableDeviceEvents.getColumnsNames(), queryString, queryParameters, null);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Event getEvent(final long id) {

        open();

        Event event = null;
        Cursor cursor = getEventCursor(id);


        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // Retrieving event
                event = TableEvents.getEventFromCursor(cursor);

                // Retrieving eventDevices
                Cursor deviceEventCursor = getDeviceEventCursor(id);
                if (deviceEventCursor != null) {
                    while (deviceEventCursor.moveToNext()) {

                        DeviceEvent deviceEvent = TableDeviceEvents.getDeviceEventFromCursor(deviceEventCursor);
                        event.getDeviceEvents().add(deviceEvent);
                    }
                    cursor.close();
                }
            }
            cursor.close();
        }
        close();

        return event;
    }


    public synchronized List<Event> getAllEventsByType(int type) {
        open();

        List<Event> eventsList = new ArrayList<>();

        Cursor cursor = getAllEventsByTypeCursor(type);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Event event = TableEvents.getEventFromCursor(cursor);
                // get eventDevice for this event.
                Cursor deviceEventCursor = getAllDeviceEventCursor(event.getId());
                if (deviceEventCursor != null) {
                    while (deviceEventCursor.moveToNext()) {
                        DeviceEvent deviceEvent = TableDeviceEvents.getDeviceEventFromCursor(deviceEventCursor);
                        event.getDeviceEvents().add(deviceEvent);
                    }
                }

                eventsList.add(event);
            }
            cursor.close();
        }
        close();

        return eventsList;
    }

    public synchronized Event createOrUpdateEvent(Event event) {

        Event updatedEvent = getEvent(event.getId());
        Cursor deviceEventCursor = getAllDeviceEventCursor(event.getId());

        open();
        mDatabase.beginTransaction();

        boolean response = false;

        try {
            // CREATE or UPDATE
            if ((event.getId() != -1) && updatedEvent != null) {
                String queryString = mApp.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(event.getId())};

                response = executeUpdate(TableEvents.TABLE_NAME, TableEvents.createContentValues(event), queryString, queryParameters);
                if (response) {
                    if (updatedEvent != null) {

                        if (deviceEventCursor.getCount() > 0) {

                            // Delete deviceEvent before adding them again.
                            response = executeDelete(TableDeviceEvents.TABLE_NAME, queryString, queryParameters);
                            if (response) {
                                for (int i = 0; i < event.getDeviceEvents().size(); i++) {
                                    int idEventCreated = (int) executeCreate(TableDeviceEvents.TABLE_NAME, TableDeviceEvents.createContentValues(event, i));
                                    if (idEventCreated < 0) {
                                        mDatabase.endTransaction();
                                        close();
                                        return null;
                                    }
                                }
                            } else {
                                // ERROR
                                mDatabase.endTransaction();
                                close();
                                return null;
                            }
                        } else {

                            // If there aren't deviceEvents (individually deleted before), recreate all deviceEvents
                            for (int i = 0; i < event.getDeviceEvents().size(); i++) {
                                int idEventCreated = (int) executeCreate(TableDeviceEvents.TABLE_NAME, TableDeviceEvents.createContentValues(event, i));
                                if (idEventCreated < 0) {
                                    mDatabase.endTransaction();
                                    close();
                                    return null;
                                }
                            }
                        }


                        mDatabase.setTransactionSuccessful();
                        mDatabase.endTransaction();
                        close();
                        return updatedEvent;

                    } else {
                        // ERROR
                        mDatabase.endTransaction();
                        close();
                        return null;
                    }

                } else {
                    // ERROR
                    mDatabase.endTransaction();
                    close();
                    return null;
                }

            } else {

                int idEventCreated = (int) executeCreate(TableEvents.TABLE_NAME, TableEvents.createContentValues(event));
                if (idEventCreated >= 0) {

                    // Create the deviceEvents rows.
                    event.setId(idEventCreated);
                    for (int i = 0; i < event.getDeviceEvents().size(); i++) {
                        idEventCreated = (int) executeCreate(TableDeviceEvents.TABLE_NAME, TableDeviceEvents.createContentValues(event, i));
                        if (idEventCreated < 0) {
                            mDatabase.endTransaction();
                            close();
                            return null;
                        }
                    }
                    mDatabase.setTransactionSuccessful();
                    mDatabase.endTransaction();
                    close();
                    return event;

                } else {
                    // ERROR
                    mDatabase.endTransaction();
                    close();
                    return null;
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mDatabase.endTransaction();
            close();
        }

        return updatedEvent;
    }

    public synchronized boolean removeEvent(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableEvents.TABLE_NAME, queryString, queryParameters);
            if (!response)
                return response;

            response = executeDelete(TableDeviceEvents.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return response;
    }


    public synchronized boolean removeDeviceEventsOfEvent(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = mApp.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableDeviceEvents.TABLE_NAME, queryString, queryParameters);
            if (!response)
                return response;

            response = executeDelete(TableDeviceEvents.TABLE_NAME, queryString, queryParameters);

        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return response;
    }


    private Cursor getDeviceEventIdsByDeviceIdCursor(final int deviceID) {

        String sortOrder = TableDeviceEvents.COLUMN_DEVICE_EVENT_ID + " ASC";

        String queryString = mApp.getString(R.string.sql_where_clause_device_id);
        String[] queryParameters = new String[]{String.valueOf(deviceID)};

        try {
            return executeSelect(TableDeviceEvents.TABLE_NAME, TableDeviceEvents.getColumnsNames(), queryString, queryParameters, sortOrder);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Integer> getNewIdsForDeviceEvent(final int deviceID, final int numberOfNewEvents) {

        open();

        Cursor cursor = getDeviceEventIdsByDeviceIdCursor(deviceID);
        List<Integer> newIds = new ArrayList<>();
        int deviceEventIdIndex = 1;

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                DeviceEvent deviceEvent = TableDeviceEvents.getDeviceEventFromCursor(cursor);

                // Fill the gaps of IDs not used in the current DB until reach the numberOfNewEvents
                if (deviceEventIdIndex < deviceEvent.getDeviceEventId()) {
                    for (int i = deviceEventIdIndex; i < deviceEvent.getDeviceEventId(); i++) {
                        newIds.add(i);
                        if (newIds.size() == numberOfNewEvents) break;
                    }
                    deviceEventIdIndex = deviceEvent.getDeviceEventId() + 1;
                } else {
                    deviceEventIdIndex++;
                }

                if (newIds.size() == numberOfNewEvents) break;
            }

            // If numberOfNewEvents not filled yet fill the necessary number of IDs to complete
            if (newIds.size() < numberOfNewEvents) {
                int numberOfRemainingIds = numberOfNewEvents - newIds.size();
                for (int i = deviceEventIdIndex; i < deviceEventIdIndex + numberOfRemainingIds; i++) {
                    newIds.add(i);
                }
            }

        } else {
            // Create as many new IDs as required by numberOfNewEvents
            for (int i = deviceEventIdIndex; i <= numberOfNewEvents; i++) {
                newIds.add(i);
            }
        }
        cursor.close();

        return newIds;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Primary DB operations
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param sql
     * @throws SQLException
     */
    private void executeSql(String sql) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        mDatabase.execSQL(sql);
    }

    /**
     * @param sql
     * @param whereArgs
     * @return
     * @throws SQLException
     */
    private Cursor executeSelectQuery(String sql, String[] whereArgs) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        Cursor cursor = mDatabase.rawQuery(sql, whereArgs);

        return cursor;
    }

    /**
     * @param tableName
     * @param columns
     * @param whereClause
     * @param whereArgs
     * @param orderBy
     * @return
     * @throws SQLException
     */
    private Cursor executeSelect(String tableName, String[] columns, String whereClause, String[] whereArgs, String orderBy) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        Cursor cursor = mDatabase.query(tableName, columns, whereClause, whereArgs, null, null, orderBy);

        return cursor;
    }

    /**
     * @param tableName
     * @param contentValues
     * @return
     * @throws SQLException
     */
    private long executeCreate(String tableName, ContentValues contentValues) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        long rowId = mDatabase.insert(tableName, null, contentValues);

        return rowId;
    }

    /**
     * @param tableName
     * @param contentValues
     * @return
     * @throws SQLException
     */
    private boolean executeReplace(String tableName, ContentValues contentValues) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        long rowId = mDatabase.replace(tableName, null, contentValues);

        return (rowId > 0);
    }

    /**
     * @param tableName
     * @param contentValues
     * @param whereClause
     * @param whereArgs
     * @return
     * @throws SQLException
     */
    private boolean executeUpdate(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        long numberOfRowsAffected = mDatabase.update(tableName, contentValues, whereClause, whereArgs);

        return (numberOfRowsAffected > 0);
    }

    /**
     * @param tableName
     * @param whereClause
     * @param whereArgs
     * @return
     * @throws SQLException
     */
    private boolean executeDelete(String tableName, String whereClause, String[] whereArgs) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        long numberOfRowsAffected = mDatabase.delete(tableName, whereClause, whereArgs);

        return (numberOfRowsAffected > 0);
    }

    private void open() throws SQLException {
        mDatabase = mDBHelper.getWritableDatabase();
    }

    private void close() {
        mDBHelper.close();
    }
}

