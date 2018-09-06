/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.model.devices.states;

import com.csr.csrmesh2.PowerState;

/**
 *
 */
public class ModelPowerState extends ModelState {

    public static final String POWER_STATE_KEY = "POWER_STATE_KEY";

    private PowerState state = PowerState.OFF;

    // Constructor
    public ModelPowerState() {
    }

    public PowerState getState() {
        return state;
    }

    public void setState(int state) {
        if (state >= 0 && state < PowerState.values().length) {
            this.state = PowerState.values()[state];
        }
    }

    public void setState(PowerState state) {
        this.state = state;
    }
}
