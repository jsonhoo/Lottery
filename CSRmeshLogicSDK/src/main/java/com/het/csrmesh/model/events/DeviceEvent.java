package com.het.csrmesh.model.events;

import com.csr.csrmesh2.MeshAction;

public class DeviceEvent {
    private int deviceEventId = -1;
    private int deviceId = -1;
    private long time = -1;
    private long repeatMls = -1;
    private MeshAction meshAction;


    public int getDeviceEventId() {
        return deviceEventId;
    }

    public void setDeviceEventId(int eventId) {
        this.deviceEventId = eventId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public long getRepeatMls() {
        return repeatMls;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setRepeatMls(long repeatMls) {
        this.repeatMls = repeatMls;
    }

    public MeshAction getMeshAction() {
        return meshAction;
    }

    public void setMeshAction(MeshAction meshAction) {
        this.meshAction = meshAction;
    }


}