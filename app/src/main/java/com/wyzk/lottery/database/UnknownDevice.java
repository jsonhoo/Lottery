/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.wyzk.lottery.database;


import com.wyzk.lottery.model.Device;

/**
 *
 */
public class UnknownDevice extends Device {

    // Constructor
    public UnknownDevice() {
    }

    public int getType() {
        return TYPE_UNKNOWN;
    }

}
