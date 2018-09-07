package com.wyzk.lottery.api;

import java.io.Serializable;
import java.util.Arrays;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-09-07<br>
 * 描述:
 **/
public class MeshPacket implements Serializable {
    private static final long serialVersionUID = 369285298572941L;
    private short command;
    private int deviceId = -1;
    private byte[] data;

    public long getMinTimes() {
        return minTimes;
    }

    public void setMinTimes(long minTimes) {
        this.minTimes = minTimes;
    }

    private long minTimes;

    public MeshPacket(int deviceId, byte[] data) {
        this.deviceId = deviceId;
        this.data = data;
    }


    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    private boolean acknowledged ;

    public MeshPacket() {
    }

    public short getCommand() {
        return command;
    }

    public void setCommand(short command) {
        this.command = command;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "MeshPacket{" +
                "command=" + command +
                ", deviceId=" + deviceId +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}