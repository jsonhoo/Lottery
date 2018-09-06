package com.wyzk.lottery.model;

import java.io.Serializable;

public class TokenModel implements Serializable {
    private String userId;
    private String token;
    private String isAdmin;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "TokenModel{" +
                "userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                ", isAdmin='" + isAdmin + '\'' +
                '}';
    }
}
