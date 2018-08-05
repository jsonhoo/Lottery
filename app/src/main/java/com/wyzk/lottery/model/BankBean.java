package com.wyzk.lottery.model;


import java.io.Serializable;

public class BankBean implements Serializable {
    private String remarkCode;
    private String userManagerPayAccountId;
    private String managerPayAccountBank;
    private String managerPayAccountName;
    private String managerPayAccountType;
    private String managerPayAccount;

    public String getRemarkCode() {
        return remarkCode;
    }

    public void setRemarkCode(String remarkCode) {
        this.remarkCode = remarkCode;
    }

    public String getUserManagerPayAccountId() {
        return userManagerPayAccountId;
    }

    public void setUserManagerPayAccountId(String userManagerPayAccountId) {
        this.userManagerPayAccountId = userManagerPayAccountId;
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

    @Override
    public String toString() {
        return "BankBean{" +
                "remarkCode='" + remarkCode + '\'' +
                ", userManagerPayAccountId='" + userManagerPayAccountId + '\'' +
                ", managerPayAccountBank='" + managerPayAccountBank + '\'' +
                ", managerPayAccountName='" + managerPayAccountName + '\'' +
                ", managerPayAccountType='" + managerPayAccountType + '\'' +
                ", managerPayAccount='" + managerPayAccount + '\'' +
                '}';
    }
}
