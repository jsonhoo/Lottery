/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.wyzk.lottery.event;

import android.os.Bundle;

public class MeshSystemEvent extends MeshEvent {

    public enum SystemEvent {
        SERVICE_SHUTDOWN,
        CHANNEL_READY,
        BRIDGE_CONNECTED,
        BRIDGE_DISCONNECTED,
        DEVICE_SCANNED,
        CHANNEL_NOT_READY,
        PLACE_CHANGED,
        WEAR_CONNECTED,
        GATEWAY_NOT_CONFIGURED,
        GATEWAY_DISCOVERED,
        GATEWAY_SETUP_POPUP_CLOSED,
        GATEWAY_GUEST_CONTROLLER_SETUP_POPUP_CLOSED,
        GATEWAY_FILE_POPUP_CLOSED,
        GATEWAY_FILE_INFO_POPUP_CLOSED,
        GATEWAY_FILE_CREATED_POPUP_CLOSED,
        GATEWAY_FILE_DELETED_POPUP_CLOSED,
        GATEWAY_FILE_NOT_FOUND_POPUP_CLOSED,
        REST_AUTHENTICATED,
        REST_AUTHENTICATION_FAILED,
        REST_AUTHENTICATION_EXPIRED,
        REST_NOT_AUTHENTICATED,
        REST_AUTHENTICATION_INPROGRESS,
        AREA_CHANGED,
        DEVICE_CHANGED,
        BT_REQUEST,
        DEVICE_STATUS_CHANGE
    }

    public SystemEvent what;

    public MeshSystemEvent(SystemEvent what) {
        this.what = what;
    }

    public MeshSystemEvent(SystemEvent what, Bundle data) {
        this.what = what;
        this.data = data;
    }

}
