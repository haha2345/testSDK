package com.example.cbnosdk;

public class CBNOEntity {
    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public CBNOEntity(String phoneNum, String coId, String loanCode, String loanName, String loanUserName, String loanUserIdnum, String loanMoney, String loanRatio, String loanFromTo) {
        this.phoneNum = phoneNum;
        this.coId = coId;
        this.loanCode = loanCode;
        this.loanName = loanName;
        this.loanUserName = loanUserName;
        this.loanUserIdnum = loanUserIdnum;
        this.loanMoney = loanMoney;
        this.loanRatio = loanRatio;
        this.loanFromTo = loanFromTo;
    }

    public String getCoId() {
        return coId;
    }

    public void setCoId(String coId) {
        this.coId = coId;
    }

    public String getLoanCode() {
        return loanCode;
    }

    public void setLoanCode(String loanCode) {
        this.loanCode = loanCode;
    }

    public String getLoanName() {
        return loanName;
    }

    public void setLoanName(String loanName) {
        this.loanName = loanName;
    }

    public String getLoanUserName() {
        return loanUserName;
    }

    public void setLoanUserName(String loanUserName) {
        this.loanUserName = loanUserName;
    }

    public String getLoanUserIdnum() {
        return loanUserIdnum;
    }

    public void setLoanUserIdnum(String loanUserIdnum) {
        this.loanUserIdnum = loanUserIdnum;
    }

    public String getLoanMoney() {
        return loanMoney;
    }

    public void setLoanMoney(String loanMoney) {
        this.loanMoney = loanMoney;
    }

    public String getLoanRatio() {
        return loanRatio;
    }

    public void setLoanRatio(String loanRatio) {
        this.loanRatio = loanRatio;
    }

    public String getLoanFromTo() {
        return loanFromTo;
    }

    public void setLoanFromTo(String loanFromTo) {
        this.loanFromTo = loanFromTo;
    }

    private String phoneNum;
    private String coId;
    private String loanCode;
    private String loanName;
    private String loanUserName;
    private String loanUserIdnum;
    private String loanMoney;
    private String loanRatio;
    private String loanFromTo;


}
