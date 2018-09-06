/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.model;

/**
 *
 */
public class Gateway {

    public final static int STATE_DISASSOCIATED = 0;
    public final static int STATE_ASSOCIATED = 1;
    public final static int STATE_LOCAL = 2;
    public final static int STATE_CLOUD = 3;

    private int id = -1;
    private String name = "";
    private String host = "";
    private String port = "";
    private String uuid = "";
    private String basePath = "";
    private int state = -1;
    protected byte[] dhmKey;
    protected int deviceHash;
    private int deviceID;
    private int placeID;

    public Gateway() {
    }

    public Gateway(final String name, final String host, final String port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public Gateway(final String name, final String host, final String port, final String uuid, final String basePath) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.uuid = uuid;
        this.basePath = basePath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setDeviceID(int deviceId) {
        this.deviceID = deviceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaceID() {
        return placeID;
    }

    public void setPlaceID(int placeID) {
        this.placeID = placeID;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    public byte[] getDhmKey() {
        return dhmKey;
    }

    public void setDhmKey(byte[] dmKey) {
        this.dhmKey = dmKey;
    }

}
