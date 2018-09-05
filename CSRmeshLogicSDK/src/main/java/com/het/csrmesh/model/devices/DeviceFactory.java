/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.model.devices;

/**
 *
 */
public class DeviceFactory {

    public static Device getDevice(final int appearance) {

        if (appearance == AppearanceDevice.LIGHT_APPEARANCE) {
            return new LightDevice();
        }
        else if (appearance == AppearanceDevice.HEATER_APPEARANCE ) {
            return new TemperatureDevice();
        }
        else if (appearance == AppearanceDevice.SENSOR_APPEARANCE) {
            return new SensorDevice();
        }
        else {
            return new UnknownDevice();
        }
    }
}
