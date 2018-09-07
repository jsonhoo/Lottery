/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.event;

import android.os.Bundle;

public class MeshRequestEvent extends MeshEvent {

    public enum RequestEvent {
        // System
        SET_MESH_BEARER,
        SET_MESH_CONNECTIONS_SETTINGS,
        ASSOCIATE_DEVICE,
        ASSOCIATE_GATEWAY,
        DISCOVER_DEVICES,
        ATTENTION_PRE_ASSOCIATION,
        MASP_RESET,
        START_BROWSING_GATEWAYS,
        STOP_BROWSING_GATEWAYS,
        SET_GATEWAY_PARAMS,
        SET_CONTINUOUS_SCANNING,
        KILL_TRANSACTION,
        SET_CONTROLLER_ADDRESS,
        START_ADVERTISING,
        STOP_ADVERTISING,


        // Actuator model
        ACTUATOR_GET_TYPES,
        ACTUATOR_SET_VALUE,

        // Attention model
        ATTENTION_SET_STATE,

        // Battery model
        BATTERY_GET_STATE,

        // Bearer model
        BEARER_GET_STATE,
        BEARER_SET_STATE,

        // Config model
        CONFIG_DISCOVER_DEVICE,
        CONFIG_GET_INFO,
        CONFIG_GET_PARAMETERS,
        CONFIG_SET_PARAMETERS,
        CONFIG_RESET_DEVICE,
        CONFIG_SET_DEVICE_IDENTIFIER,

        // Data model
        DATA_SEND_DATA,

        // Firmware model
        FIRMWARE_UPDATE_REQUIRED,
        FIRMWARE_GET_VERSION,

        // Group model
        GROUP_GET_MODEL_GROUP_ID,
        GROUP_GET_NUMBER_OF_MODEL_GROUP_IDS,
        GROUP_SET_MODEL_GROUP_ID,

        // Light model
        LIGHT_GET_STATE,
        LIGHT_SET_COLOR_TEMPERATURE,
        LIGHT_SET_LEVEL,
        LIGHT_SET_POWER_LEVEL,
        LIGHT_SET_RGB,
        LIGHT_SET_WHITE,

        // Ping model
        PING_REQUEST,

        // Power model
        POWER_GET_STATE,
        POWER_SET_STATE,
        POWER_TOGGLE_STATE,

        // Sensor model
        SENSOR_GET_STATE,
        SENSOR_GET_TYPES,
        SENSOR_GET_VALUE,
        SENSOR_SET_STATE,
        SENSOR_SET_VALUE,

        // Config gateway
        GATEWAY_GET_PROFILE,
        GATEWAY_REMOVE_NETWORK,

        // Config cloud
        CLOUD_GET_TENANTS,
        CLOUD_CREATE_TENANT,
        CLOUD_GET_TENANT_INFO,
        CLOUD_DELETE_TENANT,
        CLOUD_UPDATE_TENANT,
        CLOUD_GET_SITES,
        CLOUD_CREATE_SITE,
        CLOUD_GET_SITE_INFO,
        CLOUD_DELETE_SITE,
        CLOUD_UPDATE_SITE,

        // Time model
        TIME_SET_STATE,
        TIME_GET_STATE,
        TIME_BROADCAST,

        // Action model
        ACTION_SET_ACTION,
        ACTION_DELETE_ACTION,

        //Large Object Transfer Model
        LOT_ANNOUNCE,
        LOT_INTEREST,

        //Watchdog model
        WATCHDOG_MESSAGE,
        WATCHDOG_SET_INTERVAL,

        //Diagnostic Model
        DIAGNOSTIC_GET_STATS,
    }



    public RequestEvent what;

    public MeshRequestEvent(RequestEvent what) {
        this.what = what;
    }

    public MeshRequestEvent(RequestEvent what, Bundle data) {
        this.what = what;
        this.data = data;
    }
}
