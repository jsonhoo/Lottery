package com.wyzk.lottery.model;

import java.io.Serializable;

/**
 * Created by uuxia-mac on 2017/12/31.
 */

public class MQPlayerBean implements Serializable {

    /**
     * roomRoundId : 13
     * status : 2 1 房间状态（1：下注中，2：封盘中，3：结算中，4：结算完成，5：作废）
     * type = 2 下注，type=0房间状态信息，type=1牌信息
     */

    private int roomRoundId;
    private int cardId;
    private int position;
    private int type;
    private int status;
    private int betValue;


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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MQPlayerBean{" +
                "roomRoundId=" + roomRoundId +
                ", cardId=" + cardId +
                ", position=" + position +
                ", type=" + type +
                ", status=" + status +
                ", betValue=" + betValue +
                '}';
    }
}
