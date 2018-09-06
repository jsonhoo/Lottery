/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.het.csrmesh.events;

import android.os.Bundle;

public class MeshResponseEvent extends MeshEvent {

    public enum ResponseEvent {
        ERROR,
        DEVICE_UUID,
        DEVICE_APPEARANCE,
        ASSOCIATION_PROGRESS,
        LOCAL_ASSOCIATION_PROGRESS,
        LOCAL_DEVICE_ASSOCIATED,
        LOCAL_DEVICE_FAILED,
        TIMEOUT,
        DEVICE_ASSOCIATED,
        MESSAGE_NETWORK_SECURITY_UPDATE,

        // Actuator model
        ACTUATOR_TYPES,
        ACTUATOR_VALUE,

        // Attention model
        ATTENTION_STATE,

        // Battery model
        BATTERY_STATE,

        // Bearer model
        BEARER_STATE,

        // Config model
        CONFIG_PARAMETERS,
        CONFIG_DEVICE_IDENTIFIER,
        CONFIG_INFO,

        // Data model
        DATA_RECEIVE_STREAM,
        DATA_RECEIVE_BLOCK,
        DATA_RECEIVE_STREAM_END,
        DATA_SENT,

        // Firmware model
        FIRMWARE_VERSION_INFO,
        FIRMWARE_UPDATE_ACKNOWLEDGED,

        // Group model
        GROUP_NUMBER_OF_MODEL_GROUPIDS,
        GROUP_MODEL_GROUPID,

        // Light model
        LIGHT_STATE,

        // Ping model
        PING_RESPONSE,

        // Power model
        POWER_STATE,

        // Sensor model
        SENSOR_TYPES,
        SENSOR_STATE,
        SENSOR_VALUE,

        // Time model
        TIME_STATE,

        // Config gateway
        GATEWAY_PROFILE,
        GATEWAY_REMOVE_NETWORK,

        // Config cloud
        TENANT_RESULTS,
        TENANT_CREATED,
        TENANT_INFO,
        TENANT_DELETED,
        TENANT_UPDATED,
        SITE_RESULTS,
        SITE_CREATED,
        SITE_INFO,
        SITE_DELETED,
        SITE_UPDATED,

        // Gateway File
        GATEWAY_FILE,
        GATEWAY_FILE_INFO,
        GATEWAY_FILE_CREATED,
        GATEWAY_FILE_DELETED,

        // Action model
        ACTION_SENT,
        ACTION_DELETED,

        //Large Object Transfer Model
        LOT_INTEREST,

        //Watchdog model
        WATCHDOG_MESSAGE,
        WATCHDOG_INTERVAL,
        //Diagnostic Model
        DIAGNOSTIC_STATE,
        DIAGNOSTIC_STATS,

        TRACKER_FOUND,
        TRACKER_REPORT
    }

    public ResponseEvent what;

    public MeshResponseEvent(ResponseEvent what) {
        this.what = what;
    }

    public MeshResponseEvent(ResponseEvent what, Bundle data) {
        this.what = what;
        this.data = data;
    }
}
