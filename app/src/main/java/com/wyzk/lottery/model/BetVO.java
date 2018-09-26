package com.wyzk.lottery.model;

import java.io.Serializable;

public class BetVO implements Serializable {

    private Integer positionId;
    private Integer betValue;

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Integer getBetValue() {
        return betValue;
    }

    public void setBetValue(Integer betValue) {
        this.betValue = betValue;
    }
}
