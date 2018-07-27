package com.wyzk.lottery.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by uuxia-mac on 2017/12/30.
 * 获取房间当前场次
 */

public class RoomRoundModel implements Serializable {

    /**
     * roomRoundId : 1
     * roundState : 1
     * betValue : 1 房间状态（1：下注中，2：封盘中，3：结算中，4：结算完成，5：作废）
     */

    //场次ID
    private int roomRoundId;
    //房间状态
    private int roundState;
    //下注积分
    private int betValue;
    //下注方位
    private int betPosition;
    //倒计时
    private int countdown;

    private List<PlaceBetModel> list;

    public int getRoomRoundId() {
        return roomRoundId;
    }

    public void setRoomRoundId(int roomRoundId) {
        this.roomRoundId = roomRoundId;
    }

    public int getRoundState() {
        return roundState;
    }

    public void setRoundState(int roundState) {
        this.roundState = roundState;
    }

    public int getBetValue() {
        return betValue;
    }

    public void setBetValue(int betValue) {
        this.betValue = betValue;
    }

    public int getBetPosition() {
        return betPosition;
    }

    public void setBetPosition(int betPosition) {
        this.betPosition = betPosition;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public List<PlaceBetModel> getList() {
        return list;
    }

    public void setList(List<PlaceBetModel> list) {
        this.list = list;
    }

    public class PlaceBetModel {
        private int position;
        private int betValue;
        private int count;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getBetValue() {
            return betValue;
        }

        public void setBetValue(int betValue) {
            this.betValue = betValue;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    @Override
    public String toString() {
        return "RoomRoundModel{" +
                "roomRoundId=" + roomRoundId +
                ", roundState=" + roundState +
                ", betValue=" + betValue +
                ", betPosition=" + betPosition +
                ", countdown=" + countdown +
                ", list=" + list +
                '}';
    }
}
