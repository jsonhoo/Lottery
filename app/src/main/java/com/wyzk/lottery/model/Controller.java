package com.wyzk.lottery.model;


import com.wyzk.lottery.constant.Constants;

/**
 *
 */
public class Controller {

    private int id = -1;
    private String name = "";
    private long updateDate = -1;

    private int deviceHash;
    private String uuid = "";
    private byte[] dmKey;
    private long authCode = Constants.INVALID_VALUE;
    protected boolean isAssociated;

    private int deviceID;
    private int placeID;

    public Controller() {
    }

    public Controller(final String name, final int updateDate) {
        this.name = name;
        this.updateDate = updateDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public int getDeviceHash() {
        return deviceHash;
    }

    public void setDeviceHash(int deviceHash) {
        this.deviceHash = deviceHash;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public byte[] getDmKey() {
        return dmKey;
    }

    public void setDmKey(byte[] dmKey) {
        this.dmKey = dmKey;
    }

    public long getAuthCode() {
        return authCode;
    }

    public void setAuthCode(long authCode) {
        this.authCode = authCode;
    }

    public boolean isAssociated() {
        return isAssociated;
    }

    public void setAssociated(boolean associated) {
        this.isAssociated = associated;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public int getPlaceID() {
        return placeID;
    }

    public void setPlaceID(int placeID) {
        this.placeID = placeID;
    }
}
