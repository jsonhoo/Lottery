package com.wyzk.lottery.model;


import java.io.Serializable;
import java.util.List;

public class ExchangeModel implements Serializable {

    private int total;
    private int page;
    private List<ExchangeItem> rows;


    public class ExchangeItem implements Serializable {
        private String exchangeNo;
        private int exchangeValue;
        private String exchangeStatus;

        private String userPayAccountBank;
        private String userPayAccount;
        private String userPayAccountName;
        private String userPayAccountType;


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
    }
}
