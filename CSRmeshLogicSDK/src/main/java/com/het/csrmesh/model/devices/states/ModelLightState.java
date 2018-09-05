/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.model.devices.states;

/**
 *
 */
public class ModelLightState extends ModelState {

    public static final String LIGHT_STATE_KEY = "LIGHT_STATE_KEY";

    private int color = 0;
    private int lightLevel = 0;
    private long lightTimeStamp = 0;

    // Constructor
    public ModelLightState() {
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public int getLightLevel() {
        return lightLevel;
    }

    public void setLightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
    }

    public long getLightTimeStamp() {
        return lightTimeStamp;
    }

    public void setLightTimeStamp(long lightTimeStamp) {
        this.lightTimeStamp = lightTimeStamp;
    }

}
