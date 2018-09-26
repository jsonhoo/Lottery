package com.wyzk.lottery.model;

import java.io.Serializable;

/**
 * Created by uuxia-mac on 2017/12/31.
 */

public class MQPlayerBean implements Serializable {

    /**
     * roomRoundId : 13
     * status : 2 1 房间状态（1：下注中，2：封盘中，3：结算中，4：结算完成，5：作废）
     */

    private int roomRoundId;
    private int status;

    private int betValue;
    private int position;

    private int cardId;


    public int getRoomRoundId() {
        return roomRoundId;
    }

    public void setRoomRoundId(int roomRoundId) {
        this.roomRoundId = roomRoundId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBetValue() {
        return betValue;
    }

    public void setBetValue(int betValue) {
        this.betValue = betValue;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    @Override
    public String toString() {
        return "MQPlayerBean{" +
                "roomRoundId=" + roomRoundId +
                ", status=" + status +
                ", betValue=" + betValue +
                ", position=" + position +
                '}';
    }
}
