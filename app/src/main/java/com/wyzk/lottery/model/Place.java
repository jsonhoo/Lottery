/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.wyzk.lottery.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Arrays;

/**
 *
 */
@Table(name = "Places")
public class Place extends Model {
    @Column(name = "PlaceId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int placeId= -1;
    @Column(name = "Name")
    private String name = "";
    @Column(name = "Passphrase")
    private String passphrase = "";
    @Column(name = "NetworkKey")
    private byte[] networkKey;
    @Column(name = "CloudSiteID")
    private String cloudSiteID = "";
    @Column(name = "IconID")
    private int iconID;
    @Column(name = "Color")
    private int color;
    @Column(name = "HostControllerID")
    private int hostControllerID;
    @Column(name = "SettingsID")
    private int settingsID;

    public Place() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public byte[] getNetworkKey() {
        return networkKey;
    }

    public void setNetworkKey(byte[] networkKey) {
        this.networkKey = networkKey;
    }

    public String getCloudSiteID() {
        return cloudSiteID;
    }

    public void setCloudSiteID(String cloudSiteID) {
        this.cloudSiteID = cloudSiteID;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getHostControllerID() {
        return hostControllerID;
    }

    public void setHostControllerID(int hostControllerID) {
        this.hostControllerID = hostControllerID;
    }

    public int getSettingsID() {
        return settingsID;
    }

    public void setSettingsID(int settingsID) {
        this.settingsID = settingsID;
    }

    @Override
    public String toString() {
        return "Place{" +
                "placeID=" + placeId +
                ", name='" + name + '\'' +
                ", passphrase='" + passphrase + '\'' +
                ", networkKey=" + Arrays.toString(networkKey) +
                ", cloudSiteID='" + cloudSiteID + '\'' +
                ", iconID=" + iconID +
                ", color=" + color +
                ", hostControllerID=" + hostControllerID +
                ", settingsID=" + settingsID +
                '}';
    }
}
