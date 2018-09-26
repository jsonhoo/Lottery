package com.wyzk.lottery.model;

import java.io.Serializable;
import java.util.List;

public class UserBetVO implements Serializable {

    private Integer roomId;
    private Integer roundId;
    private List<BetVO> betDetail;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getRoundId() {
        return roundId;
    }

    public void setRoundId(Integer roundId) {
        this.roundId = roundId;
    }

    public List<BetVO> getBetDetail() {
        return betDetail;
    }

    public void setBetDetail(List<BetVO> betDetail) {
        this.betDetail = betDetail;
    }
}
