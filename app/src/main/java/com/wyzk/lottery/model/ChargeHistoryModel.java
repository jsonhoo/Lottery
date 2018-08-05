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
public class ChargeHistoryModel implements Serializable {

    private int chargeType;
    private int chargeValue;
    private String chargeNo;

    private int chargeStatus;
    private String createTime;
    private int chargeId;
    private String remarkCode;
    private String managerPayAccountBank;
    private String managerPayAccountName;
    private String managerPayAccountType;
    private String managerPayAccount;

    public int getChargeType() {
        return chargeType;
    }

    public void setChargeType(int chargeType) {
        this.chargeType = chargeType;
    }

    public int getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(int chargeValue) {
        this.chargeValue = chargeValue;
    }

    public String getChargeNo() {
        return chargeNo;
    }

    public void setChargeNo(String chargeNo) {
        this.chargeNo = chargeNo;
    }

    public int getChargeStatus() {
        return chargeStatus;
    }

    public void setChargeStatus(int chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getChargeId() {
        return chargeId;
    }

    public void setChargeId(int chargeId) {
        this.chargeId = chargeId;
    }

    public String getRemarkCode() {
        return remarkCode;
    }

    public void setRemarkCode(String remarkCode) {
        this.remarkCode = remarkCode;
    }

    public String getManagerPayAccountBank() {
        return managerPayAccountBank;
    }

    public void setManagerPayAccountBank(String managerPayAccountBank) {
        this.managerPayAccountBank = managerPayAccountBank;
    }

    public String getManagerPayAccountName() {
        return managerPayAccountName;
    }

    public void setManagerPayAccountName(String managerPayAccountName) {
        this.managerPayAccountName = managerPayAccountName;
    }

    public String getManagerPayAccountType() {
        return managerPayAccountType;
    }

    public void setManagerPayAccountType(String managerPayAccountType) {
        this.managerPayAccountType = managerPayAccountType;
    }

    public String getManagerPayAccount() {
        return managerPayAccount;
    }

    public void setManagerPayAccount(String managerPayAccount) {
        this.managerPayAccount = managerPayAccount;
    }
}
