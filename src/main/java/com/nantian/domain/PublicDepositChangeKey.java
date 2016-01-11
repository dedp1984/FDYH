package com.nantian.domain;

import java.util.Date;

public class PublicDepositChangeKey {
    private String accountname;

    private Date gendate;

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public Date getGendate() {
        return gendate;
    }

    public void setGendate(Date gendate) {
        this.gendate = gendate;
    }
}