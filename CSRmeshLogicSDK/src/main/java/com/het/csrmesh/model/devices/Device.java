/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.model.devices;

import com.csr.csrmesh2.AttentionModelApi;
import com.csr.csrmesh2.BearerModelApi;
import com.csr.csrmesh2.FirmwareModelApi;
import com.csr.csrmesh2.GroupModelApi;
import com.csr.csrmesh2.LightModelApi;
import com.csr.csrmesh2.PingModelApi;
import com.csr.csrmesh2.PowerModelApi;
import com.csr.csrmesh2.SwitchModelApi;
import com.het.csrmesh.constant.Constants;
import com.het.csrmesh.model.devices.states.ModelState;
import com.het.csrmesh.utils.ApplicationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public abstract class Device implements Serializable {

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_LIGHT = 1;
    public static final int TYPE_TEMPERATURE = 2;
    public static final int TYPE_SENSOR = 4;

    private final int NUM_BITS_MODEL_SUPPORTED = 64;
    private final int MODEL_SUPPORT_HIGHEST_BIT_POSITION = NUM_BITS_MODEL_SUPPORTED - 1;

    protected int databaseId = Constants.INVALID_VALUE;
    protected int deviceHash;
    protected int deviceID;
    protected String name;
    protected int appearance = Constants.INVALID_VALUE;
    protected long modelHigh;
    protected long modelLow;
    protected long uuidHigh = Constants.INVALID_VALUE;
    protected long uuidLow = Constants.INVALID_VALUE;
    protected int numGroups = Constants.INVALID_VALUE;
    protected int[] groups = new int[0];
    protected byte[] dmKey;
    protected long authCode = Constants.INVALID_VALUE;
    protected int model;
    protected String placeID;
    protected boolean isFavourite;
    protected boolean isAssociated;

    protected HashMap<String, ModelState> deviceStates = new HashMap<>();

    public abstract int getType();


    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int id) {
        this.databaseId = id;
    }

    public int getDeviceHash() {
        return deviceHash;
    }

    public void setDeviceHash(int deviceHash) {
        this.deviceHash = deviceHash;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAppearance() {
        return appearance;
    }

    public void setAppearance(int appearance) {
        this.appearance = appearance;
    }

    public long getModelHigh() {
        return modelHigh;
    }

    public void setModelHigh(long modelHigh) {
        this.modelHigh = modelHigh;
    }

    public long getModelLow() {
        return modelLow;
    }

    public void setModelLow(long modelLow) {
        this.modelLow = modelLow;
    }

    public long getUuidHigh() {
        return uuidHigh;
    }

    public void setUuidHigh(long uuidHigh) {
        this.uuidHigh = uuidHigh;
    }

    public long getUuidLow() {
        return uuidLow;
    }

    public void setUuidLow(long uuidLow) {
        this.uuidLow = uuidLow;
    }

    public byte[] getDmKey() {
        return dmKey;
    }

    public void setDmKey(byte[] dmKey) {
        this.dmKey = dmKey;
    }

    public UUID getUuid() {
        if (uuidLow == Constants.INVALID_VALUE || uuidHigh == Constants.INVALID_VALUE) {
            return null;
        } else {
            return new UUID(uuidHigh, uuidLow);
        }
    }

    public long getAuthCode() {
        return authCode;
    }

    public void setAuthCode(long authCode) {
        this.authCode = authCode;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        this.isFavourite = favourite;
    }

    public boolean isAssociated() {
        return isAssociated;
    }

    public void setAssociated(boolean associated) {
        this.isAssociated = associated;
    }

    public int getNumGroups() {
        return numGroups;
    }

    public void setNumGroups(int numGroups) {
        this.numGroups = numGroups;
        resetGroupsArray();
    }

    public int[] getGroups() {
        return groups;
    }

    public byte[] getGroupsByteArray() {
        return ApplicationUtils.intArrayToByteArray(groups);
    }

    public void setGroups(int[] groups) {
        if (groups == null) {
            return;
        }
        this.groups = groups;
    }

    public void setGroup(int index, int value) {
        if (index < groups.length) {
            groups[index] = value;
        }
    }

    public List<Integer> getGroupsList() {
        if (groups == null) {
            return null;
        } else {
            List<Integer> intList = new ArrayList<Integer>();
            for (int index = 0; index < groups.length; index++) {
                intList.add(groups[index]);
            }
            return intList;
        }
    }

    public void resetGroupsArray() {
        if (numGroups >= 0) {
            groups = new int[numGroups];
        }
    }

    public boolean isInArea(int area_id) {
        return getGroupsList().contains(area_id);
    }

    public boolean isModelSupported(int... modelNumber) {

        if (modelNumber.length == 0) {
            return true;
        }

        long maskLow = 0;
        long maskHigh = 0;
        for (int n : modelNumber) {
            if (n < NUM_BITS_MODEL_SUPPORTED) {
                maskLow |= (0x01L << n);
            } else {
                maskHigh |= (0x01L << (n - MODEL_SUPPORT_HIGHEST_BIT_POSITION));
            }
        }
        long resultLow = maskLow & modelLow;
        long resultHigh = maskHigh & modelHigh;

        return (resultLow == maskLow && resultHigh == maskHigh);
    }

    public boolean isAnyModelSupported(int... modelNumber) {
        if (modelNumber.length == 0) {
            return true;
        }
        for (int n : modelNumber) {
            long mask = 0;
            long result = 0;
            if (n < NUM_BITS_MODEL_SUPPORTED) {
                mask |= (0x01L << n);
                result = mask & modelLow;
            } else {
                mask |= (0x01L << (n - MODEL_SUPPORT_HIGHEST_BIT_POSITION));
                result = mask & modelHigh;
            }
            if (result == mask) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Integer> getModelsSupported() {
        ArrayList<Integer> modelsSupported = new ArrayList<Integer>();

        for (int i = 0; i < 128; i++) {
            if (isModelSupported(i)) {
                modelsSupported.add(i);
            }
        }

        return modelsSupported;
    }

    public void setModelToSupport(int modelNumber) {
        long mask = 0;
        if (modelNumber < NUM_BITS_MODEL_SUPPORTED) {
            mask = (0x01L << modelNumber);
            modelLow = (mask | modelLow);
        } else {
            mask = (0x01L << (modelNumber - MODEL_SUPPORT_HIGHEST_BIT_POSITION));
            modelHigh = (mask | modelHigh);
        }
    }

    public ArrayList<String> getModelsLabelSupported() {
        int[] modelNumbersSupported = {AttentionModelApi.MODEL_NUMBER,
                BearerModelApi.MODEL_NUMBER, FirmwareModelApi.MODEL_NUMBER,
                GroupModelApi.MODEL_NUMBER, LightModelApi.MODEL_NUMBER,
                PingModelApi.MODEL_NUMBER, PowerModelApi.MODEL_NUMBER,
                SwitchModelApi.MODEL_NUMBER};

        String[] modelLabelsSupported = {"Attention Model", "Bearer Model",
                "Firmware Model", "Group Model", "Light Model", "Ping Model",
                "Power Model", "Switch Model"};
        ArrayList<String> modelsSupported = new ArrayList<String>();

        // return null if size modelNumbersSupported is not the same as
        // modelLabelsSupported
        if (modelNumbersSupported.length != modelLabelsSupported.length) {
            return null;
        }

        for (int index = 0; index < modelNumbersSupported.length; index++) {
            int n = modelNumbersSupported[index];
            if (isAnyModelSupported(n)) {
                modelsSupported.add(modelLabelsSupported[index]);
            }
        }

        return modelsSupported;

    }

    public ModelState getState(String key) {
        return deviceStates.get(key);
    }

    public List<ModelState> getAllStates() {
        List<ModelState> allStates = new ArrayList<>(deviceStates.values());
        return allStates;
    }

    public void setState(String key, ModelState state) {
        deviceStates.put(key, state);
    }

}
