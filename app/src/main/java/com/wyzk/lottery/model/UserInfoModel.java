package com.wyzk.lottery.model;

import java.io.Serializable;


public class UserInfoModel implements Serializable {
    private String username;
    private String realname;
    private int sex;
    private int integralValue;
    private String password;
    private String inviteCode;

    public UserInfoModel() {
    }

    public UserInfoModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getIntegralValue() {
        return integralValue;
    }

    public void setIntegralValue(int integralValue) {
        this.integralValue = integralValue;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    @Override
    public String toString() {
        return "UserInfoModel{" +
                "username='" + username + '\'' +
                ", realname='" + realname + '\'' +
                ", sex=" + sex +
                ", integralValue=" + integralValue +
                ", password='" + password + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                '}';
    }
}
