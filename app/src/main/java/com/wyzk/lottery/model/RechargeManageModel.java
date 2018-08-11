package com.wyzk.lottery.model;

import java.io.Serializable;
import java.util.List;

public class RechargeManageModel {

    private int total;
    private int page;
    private int records;

    private List<RechargeManageModel.RechargeItem> rows;

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

    public List<RechargeItem> getRows() {
        return rows;
    }

    public void setRows(List<RechargeItem> rows) {
        this.rows = rows;
    }


    public class RechargeItem implements Serializable{

        private String username;
        private String chargeId;
        private int chargeValue;
        private int chargeType;
        private long createTime;
        private String remarkCode;
        private String chargeNo;
        private int chargeStatus;
        private String managerPayAccountBank;
        private String managerPayAccountName;
        private int managerPayAccountType;
        private String managerPayAccount;


        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getChargeId() {
            return chargeId;
        }

        public void setChargeId(String chargeId) {
            this.chargeId = chargeId;
        }

        public int getChargeValue() {
            return chargeValue;
        }

        public void setChargeValue(int chargeValue) {
            this.chargeValue = chargeValue;
        }

        public int getChargeType() {
            return chargeType;
        }

        public void setChargeType(int chargeType) {
            this.chargeType = chargeType;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getRemarkCode() {
            return remarkCode;
        }

        public void setRemarkCode(String remarkCode) {
            this.remarkCode = remarkCode;
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

        public int getManagerPayAccountType() {
            return managerPayAccountType;
        }

        public void setManagerPayAccountType(int managerPayAccountType) {
            this.managerPayAccountType = managerPayAccountType;
        }

        public String getManagerPayAccount() {
            return managerPayAccount;
        }

        public void setManagerPayAccount(String managerPayAccount) {
            this.managerPayAccount = managerPayAccount;
        }
    }

}
