package com.het.csrmesh.model.devices;

import com.csr.csrmesh2.MeshConstants;
import com.het.csrmesh.utils.ApplicationUtils;

import java.util.Arrays;

public class AppearanceDevice {
    private byte[] mAppearanceCode;
    private String mShortName;
    private int mAppearance;

    public static int CONTROLLER_APPEARANCE = MeshConstants.CONTROLLER_APPEARANCE;
    public static int LIGHT_APPEARANCE = MeshConstants.LIGHT_APPEARANCE;
    public static int HEATER_APPEARANCE = MeshConstants.HEATER_APPEARANCE;
    public static int SENSOR_APPEARANCE = MeshConstants.SENSOR_APPEARANCE;
    public static int GATEWAY_APPEARANCE = MeshConstants.GATEWAY_APPEARANCE;


    public AppearanceDevice(byte[] appearanceCode, String shortName) {
        setAppearanceCode(appearanceCode);
        setShortName(shortName);

        mAppearance = ApplicationUtils.convertBytesToInteger(appearanceCode, false);
    }

    public AppearanceDevice(int appearance, String mShortName) {
        mAppearance = appearance;
        setShortName(mShortName == null ? getNameByAppearance() : mShortName);
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String mShortName) {
        this.mShortName = mShortName;
    }

    public byte[] getAppearanceCode() {
        return mAppearanceCode;
    }

    public void setAppearanceCode(byte[] mAppearanceCode) {
        this.mAppearanceCode = mAppearanceCode;
    }

    public int getAppearanceType() {
        return mAppearance;
    }

    private String getNameByAppearance() {
        if (mAppearance == LIGHT_APPEARANCE) {
            return "Light";
        } else if (mAppearance == HEATER_APPEARANCE) {
            return "Heater";
        } else if (mAppearance == SENSOR_APPEARANCE) {
            return "Sensor";
        } else if (mAppearance == CONTROLLER_APPEARANCE) {
            return "Controller";
        } else {
            return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "AppearanceDevice{" +
                "mAppearanceCode=" + Arrays.toString(mAppearanceCode) +
                ", mShortName='" + mShortName + '\'' +
                ", mAppearance=" + mAppearance +
                '}';
    }
}
