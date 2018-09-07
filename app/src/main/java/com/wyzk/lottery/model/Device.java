package com.wyzk.lottery.model;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/

import com.csr.csrmesh2.AttentionModelApi;
import com.csr.csrmesh2.BearerModelApi;
import com.csr.csrmesh2.FirmwareModelApi;
import com.csr.csrmesh2.GroupModelApi;
import com.csr.csrmesh2.LightModelApi;
import com.csr.csrmesh2.PingModelApi;
import com.csr.csrmesh2.PowerModelApi;
import com.csr.csrmesh2.SwitchModelApi;
import com.wyzk.lottery.constant.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public  class Device implements Serializable{

    // TODO: Temporarily TYPE values are based on the appearance number - develop a more sophisticated
    //       mechanism using Models, Actuators to define what type of Device it is.
    public static final int TYPE_UNKNOWN = 0;
    private final int NUM_BITS_MODEL_SUPPORTED = 64;
    private final int MODEL_SUPPORT_HIGHEST_BIT_POSITION = NUM_BITS_MODEL_SUPPORTED - 1;


    public int databaseId = Constants.INVALID_VALUE;
    public int deviceHash;
    public int deviceId;
    public String name;
    public int appearance = Constants.INVALID_VALUE;
    public long modelHigh;
    public long modelLow;
    public long uuidHigh = Constants.INVALID_VALUE;
    public long uuidLow = Constants.INVALID_VALUE;
    public int numGroups = Constants.INVALID_VALUE;


    public String uuid;
    //    pubic int[] groups = new int[0];
    public String dmKey;
    public long authCode = Constants.INVALID_VALUE;
    public int model;
    public int placeId;

    public boolean isFavourite;
    public boolean isAssociated;

    public HashMap<String, ModelState> deviceStates = new HashMap<>();
    // requested by key
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
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

    public String getDmKey() {
        return dmKey;
    }

    public void setDmKey(String dmKey) {
        this.dmKey = dmKey;
    }

    public UUID getUuid() {
        if (uuid != null) return UUID.fromString(uuid);
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

    public int getPlaceID() {
        return placeId;
    }

    public void setPlaceID(int placeId) {
        this.placeId = placeId;
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
    }

    /**
     * Check if the device supports all of the specified models.
     *
     * @param modelNumber Variable length argument list of model numbers.
     * @return True if the device supports all specified models.
     */
    public boolean isModelSupported(int... modelNumber) {

        // if no modelNumber, then return true as the device is "supporting" all
        // the modelNumbers.
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

    /**
     * Check if the device supports at least one of the specified models.
     *
     * @param modelNumber Variable length argument list of model numbers.
     * @return True if the device supports at least one of the specified models.
     */
    public boolean isAnyModelSupported(int... modelNumber) {

        // if we don't have any modelNumber to check, the device "support" all
        // the specified models.
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

    /**
     * Get a list of the names of all the models that the device supports.
     *
     * @return list of models supported
     */
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


    /**
     * Return the state according by a key.
     *
     * @param key
     * @return state
     */
    public ModelState getState(String key) {
        return deviceStates.get(key);
    }

    public List<ModelState> getAllStates() {
        List<ModelState> allStates = new ArrayList<>(deviceStates.values());
        return allStates;
    }

    /**
     * Set a new state by key.
     *
     * @param key
     * @param state
     */
    public void setState(String key, ModelState state) {
        deviceStates.put(key, state);
    }


    @Override
    public String toString() {
        return "Device{" +
                "name='" + name + '\'' +
                ", databaseId=" + databaseId +
                ", deviceHash=" + deviceHash +
                ", deviceId=" + deviceId +
                ", modelHigh=" + modelHigh +
                ", uuidHigh=" + uuidHigh +
                ", modelLow=" + modelLow +
                ", uuidLow=" + uuidLow +
                ", numGroups=" + numGroups +
                ", uuid='" + uuid + '\'' +
                ", dmKey='" + dmKey + '\'' +
                ", authCode=" + authCode +
                ", model=" + model +
                ", placeId=" + placeId +
                '}';
    }
}
