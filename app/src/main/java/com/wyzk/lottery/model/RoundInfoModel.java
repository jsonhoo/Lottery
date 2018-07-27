package com.wyzk.lottery.model;

import java.io.Serializable;

public class RoundInfoModel implements Serializable {
    private String roomRoundId;
    private String eastNorth;
    private String east;
    private String eastSouth;
    private String westNorth;
    private String west;
    private String westSouth;
    private String winLostValue;

    public String getRoomRoundId() {
        return roomRoundId;
    }

    public void setRoomRoundId(String roomRoundId) {
        this.roomRoundId = roomRoundId;
    }

    public String getEastNorth() {
        return eastNorth == null ? "" : eastNorth;
    }

    public void setEastNorth(String eastNorth) {
        this.eastNorth = eastNorth;
    }

    public String getEast() {
        return east == null ? "" : east;
    }

    public void setEast(String east) {
        this.east = east;
    }

    public String getEastSouth() {
        return eastSouth == null ? "" : eastSouth;
    }

    public void setEastSouth(String eastSouth) {
        this.eastSouth = eastSouth;
    }

    public String getWestNorth() {
        return westNorth == null ? "" : westNorth;
    }

    public void setWestNorth(String westNorth) {
        this.westNorth = westNorth;
    }

    public String getWest() {
        return west == null ? "" : west;
    }

    public void setWest(String west) {
        this.west = west;
    }

    public String getWestSouth() {
        return westSouth == null ? "" : westSouth;
    }

    public void setWestSouth(String westSouth) {
        this.westSouth = westSouth;
    }

    public String getWinLostValue() {
        return winLostValue == null ? "" : winLostValue;
    }

    public void setWinLostValue(String winLostValue) {
        this.winLostValue = winLostValue;
    }
}
