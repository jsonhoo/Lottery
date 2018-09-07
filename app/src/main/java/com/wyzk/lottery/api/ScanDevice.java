package com.wyzk.lottery.api;

import com.wyzk.lottery.model.AppearanceDevice;
import com.wyzk.lottery.model.Device;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/

public class ScanDevice extends Device implements Comparable<ScanDevice> {

    private static final long TIME_SCANINFO_VALID = 5 * 1000; // 5 secs

    public String uuid;
    public int rssi;
    public int uuidHash;
    public long timeStamp;
    public int ttl;
    public AppearanceDevice appearance;

    // Constructor
    public ScanDevice(String uuid, int rssi, int uuidHash, int ttl) {
        this.uuid = uuid;
        this.rssi = rssi;
        this.uuidHash = uuidHash;
        this.ttl = ttl;
        updated();
    }

    public void updated() {
        this.timeStamp = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        if (appearance != null) {
            return appearance.getShortName();
        }
        return null;
    }

    public int getType() {
        return TYPE_UNKNOWN;
    }

    public int getUuidHash() {
        return this.uuidHash;
    }

    /**
     * This method check if the timeStamp of the last update is still valid or not (time<TIME_SCANINFO_VALID).
     *
     * @return true if the info is still valid
     */
    public boolean isInfoValid() {
        return ((System.currentTimeMillis() - this.timeStamp) < TIME_SCANINFO_VALID);
    }

    @Override
    public int compareTo(ScanDevice info) {
        final int LESS_THAN = -1;
        final int GREATER_THAN = 1;
        final int EQUAL = 0;

        // Compare to is used for sorting the list in ascending order.
        // Smaller number of hops (highest TTL) should be at the top of the list.
        // For items with the same TTL, largest signal strength (highest RSSI) should be at the top of the list.
        if (this.ttl > info.ttl) {
            return LESS_THAN;
        }
        else if (this.ttl < info.ttl) {
            return GREATER_THAN;
        }
        else if (this.rssi > info.rssi) {
            return LESS_THAN;
        }
        else if (this.rssi < info.rssi) {
            return GREATER_THAN;
        }
        else {
            return EQUAL;
        }
    }

    @Override
    public String toString() {
        return "ScanDevice{" +
                "uuid='" + uuid + '\'' +
                ", rssi=" + rssi +
                ", uuidHash=" + uuidHash +
                ", timeStamp=" + timeStamp +
                ", ttl=" + ttl +
                ", appearance=" + appearance +
                '}';
    }

    public void setAppearance(AppearanceDevice scanAppearance) {
        appearance = scanAppearance;
    }

    public boolean hasAppearance() {
        return appearance != null;
    }

    public AppearanceDevice getAppearanceDevice() {
        return appearance;
    }

}
