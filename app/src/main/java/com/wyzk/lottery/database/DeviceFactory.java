/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.wyzk.lottery.database;


import com.wyzk.lottery.model.AppearanceDevice;
import com.wyzk.lottery.model.Device;

/**
 *
 */
public class DeviceFactory {

    public static Device getDevice(final int appearance) {

        if (appearance == AppearanceDevice.LIGHT_APPEARANCE) {
            return new UnknownDevice();
        }else {
            return new UnknownDevice();
        }
    }
}
