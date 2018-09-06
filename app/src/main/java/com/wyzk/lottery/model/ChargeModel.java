package com.wyzk.lottery.model;


import java.io.Serializable;
import java.util.List;

public class ChargeModel implements Serializable {
    private int total;
    private int records;
    private int page;
    private List<ChargeHistoryModel> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<ChargeHistoryModel> getRows() {
        return rows;
    }

    public void setRows(List<ChargeHistoryModel> rows) {
        this.rows = rows;
    }

   public class ChargeHistoryModel implements Serializable {

        private int chargeType;
        private int chargeValue;
        private String chargeNo;

        private int chargeStatus;
        private long createTime;
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

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
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

    @Override
    public String toString() {
        return "ChargeModel{" +
                "total=" + total +
                ", records=" + records +
                ", page=" + page +
                ", rows=" + rows +
                '}';
    }
}
