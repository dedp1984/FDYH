package com.nantian.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcessAccount {
    private String accountid;

    private String branchid;

    private String accountname;

    private String accounttype;

    private String maccountid;

    private String customno;

    private String currency;

    private Date opendate;

    private Date enddate;

    private String subcode;

    private String property;

    private String acctstatus;

    private String status;

    private String submitid;

    private Date submitdate;

    private String checkid;

    private Date checkdate;

    private String refusereason;
    
    private SysBranch branch;
    
    public SysBranch getBranch()
	{
		return branch;
	}

	public void setBranch(SysBranch branch)
	{
		this.branch = branch;
	}
    
    private List<ProcessBind> binds=new ArrayList<ProcessBind>();


	public List<ProcessBind> getBinds()
	{
		return binds;
	}

	public void setBinds(List<ProcessBind> binds)
	{
		this.binds = binds;
	}
    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getBranchid() {
        return branchid;
    }

    public void setBranchid(String branchid) {
        this.branchid = branchid;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getMaccountid() {
        return maccountid;
    }

    public void setMaccountid(String maccountid) {
        this.maccountid = maccountid;
    }

    public String getCustomno() {
        return customno;
    }

    public void setCustomno(String customno) {
        this.customno = customno;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getOpendate() {
        return opendate;
    }

    public void setOpendate(Date opendate) {
        this.opendate = opendate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getSubcode() {
        return subcode;
    }

    public void setSubcode(String subcode) {
        this.subcode = subcode;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getAcctstatus() {
        return acctstatus;
    }

    public void setAcctstatus(String acctstatus) {
        this.acctstatus = acctstatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmitid() {
        return submitid;
    }

    public void setSubmitid(String submitid) {
        this.submitid = submitid;
    }

    public Date getSubmitdate() {
        return submitdate;
    }

    public void setSubmitdate(Date submitdate) {
        this.submitdate = submitdate;
    }

    public String getCheckid() {
        return checkid;
    }

    public void setCheckid(String checkid) {
        this.checkid = checkid;
    }

    public Date getCheckdate() {
        return checkdate;
    }

    public void setCheckdate(Date checkdate) {
        this.checkdate = checkdate;
    }

    public String getRefusereason() {
        return refusereason;
    }

    public void setRefusereason(String refusereason) {
        this.refusereason = refusereason;
    }
}