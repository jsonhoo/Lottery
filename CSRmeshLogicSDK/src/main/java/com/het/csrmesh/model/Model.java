/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.model;

/**
 *
 */
public class Model {

    private int id = -1;
    private int modelNumber;
    private int modelInstance;
    private int nAreaInstances;
    private byte[] areaIDs;
    private int deviceID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(int modelNumber) {
        this.modelNumber = modelNumber;
    }

    public int getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(int modelInstance) {
        this.modelInstance = modelInstance;
    }

    public int getnAreaInstances() {
        return nAreaInstances;
    }

    public void setnAreaInstances(int nAreaInstances) {
        this.nAreaInstances = nAreaInstances;
    }

    public byte[] getAreaIDs() {
        return areaIDs;
    }

    public void setAreaIDs(byte[] areaIDs) {
        this.areaIDs = areaIDs;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;

        if (object != null && object instanceof Model) {
            isEqual = (this.id == ((Model) object).id);
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
