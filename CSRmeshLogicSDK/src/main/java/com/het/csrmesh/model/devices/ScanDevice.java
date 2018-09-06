package com.het.csrmesh.model.devices;

public class ScanDevice extends Device implements Comparable<ScanDevice> {
    private static final long TIME_SCANINFO_VALID = 5 * 1000;

    public String uuid;
    public int rssi;
    public int uuidHash;
    public long timeStamp;
    public int ttl;
    public AppearanceDevice appearance;

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

    @Override
    public int getType() {
        return TYPE_UNKNOWN;
    }

    @Override
    public boolean isFavourite() {
        return false;
    }

    @Override
    public void setFavourite(boolean favourite) {
    }

    public int getUuidHash() {
        return this.uuidHash;
    }

    public boolean isInfoValid() {
        return ((System.currentTimeMillis() - this.timeStamp) < TIME_SCANINFO_VALID);
    }

    @Override
    public int compareTo(ScanDevice info) {
        final int LESS_THAN = -1;
        final int GREATER_THAN = 1;
        final int EQUAL = 0;
        if (this.ttl > info.ttl) {
            return LESS_THAN;
        } else if (this.ttl < info.ttl) {
            return GREATER_THAN;
        } else if (this.rssi > info.rssi) {
            return LESS_THAN;
        } else if (this.rssi < info.rssi) {
            return GREATER_THAN;
        } else {
            return EQUAL;
        }
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
}
