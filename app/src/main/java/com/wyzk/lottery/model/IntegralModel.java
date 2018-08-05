package com.wyzk.lottery.model;

import java.io.Serializable;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * 作者: liuzh<br>
 * 版本: 2.0<br>
 * 日期: 2018-08-02<br>
 * 描述:
 **/
public class IntegralModel implements Serializable {
    private String realname;
    private String username;
    private int integralValue;
    private int integralHistoryValue;
    private int cost;

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIntegralValue() {
        return integralValue;
    }

    public void setIntegralValue(int integralValue) {
        this.integralValue = integralValue;
    }

    public int getIntegralHistoryValue() {
        return integralHistoryValue;
    }

    public void setIntegralHistoryValue(int integralHistoryValue) {
        this.integralHistoryValue = integralHistoryValue;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
