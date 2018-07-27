package com.wyzk.lottery.model;

import java.io.Serializable;

public class WinLoseModel implements Serializable {
    private int winLoseValue;

    public int getWinLoseValue() {
        return winLoseValue;
    }

    public void setWinLoseValue(int winLoseValue) {
        this.winLoseValue = winLoseValue;
    }

    @Override
    public String toString() {
        return "WinLoseModel{" +
                "winLoseValue='" + winLoseValue + '\'' +
                '}';
    }
}
