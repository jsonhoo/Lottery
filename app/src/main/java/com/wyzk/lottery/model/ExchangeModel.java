package com.wyzk.lottery.model;


import java.io.Serializable;
import java.util.List;

public class ExchangeModel implements Serializable {

    private int total;
    private int page;
    private List<ExchangeItem> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<ExchangeItem> getRows() {
        return rows;
    }

    public void setRows(List<ExchangeItem> rows) {
        this.rows = rows;
    }

    public class ExchangeItem implements Serializable {
        private String exchangeNo;
        private int exchangeValue;
        private String exchangeId;
        private String exchangeStatus;
        private String username;
        private String userPayAccountBank;
        private String userPayAccount;
        private String userPayAccountName;
        private String userPayAccountType;
        private long createTime;

        public String getExchangeId() {
            return exchangeId;
        }

        public void setExchangeId(String exchangeId) {
            this.exchangeId = exchangeId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getExchangeNo() {
            return exchangeNo;
        }

        public void setExchangeNo(String exchangeNo) {
            this.exchangeNo = exchangeNo;
        }

        public int getExchangeValue() {
            return exchangeValue;
        }

        public void setExchangeValue(int exchangeValue) {
            this.exchangeValue = exchangeValue;
        }

        public String getExchangeStatus() {
            return exchangeStatus;
        }

        public void setExchangeStatus(String exchangeStatus) {
            this.exchangeStatus = exchangeStatus;
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

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
