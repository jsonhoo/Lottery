package com.wyzk.lottery.model;


import java.io.Serializable;

public class AccountBean implements Serializable {

    private String userPayAccountId;
    private String userPayAccountBank;
    private String userPayAccount;
    private String userPayAccountName;
    private String userPayAccountType;
    private String userId;
    private int status;

    public String getUserPayAccountId() {
        return userPayAccountId;
    }

    public void setUserPayAccountId(String userPayAccountId) {
        this.userPayAccountId = userPayAccountId;
    }

    public String getUserPayAccountBank() {
        return userPayAccountBank;
    }

    public void setUserPayAccountBank(String userPayAccountBank) {
        this.userPayAccountBank = userPayAccountBank;
    }

    public String getUserPayAccount() {
        return userPayAccount;
    }

    public void setUserPayAccount(String userPayAccount) {
        this.userPayAccount = userPayAccount;
    }

    public String getUserPayAccountName() {
        return userPayAccountName;
    }

    public void setUserPayAccountName(String userPayAccountName) {
        this.userPayAccountName = userPayAccountName;
    }

    public String getUserPayAccountType() {
        return userPayAccountType;
    }

    public void setUserPayAccountType(String userPayAccountType) {
        this.userPayAccountType = userPayAccountType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AccountBean{" +
                "userPayAccountId='" + userPayAccountId + '\'' +
                ", userPayAccountBank='" + userPayAccountBank + '\'' +
                ", userPayAccount='" + userPayAccount + '\'' +
                ", userPayAccountName='" + userPayAccountName + '\'' +
                ", userPayAccountType='" + userPayAccountType + '\'' +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                '}';
    }
}
