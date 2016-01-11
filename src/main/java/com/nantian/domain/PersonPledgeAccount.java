package com.nantian.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersonPledgeAccount {
    private String saleid;

	private String accountid;

	private Double tranamt;

	private Date startdate;

	private Date enddate;

	private String lastmodifybranchid;

	private String lastmodifyacctid;

	private Date lastmodifydate;
	
	private SysBranch branch;
	
	private BaseAccount baseAccount;
	
	public BaseAccount getBaseAccount()
	{
		return baseAccount;
	}

	public void setBaseAccount(BaseAccount baseAccount)
	{
		this.baseAccount = baseAccount;
	}

	public SysBranch getBranch()
	{
		return branch;
	}

	public void setBranch(SysBranch branch)
	{
		this.branch = branch;
	}

	public List<BindAccountToManager> getBinds()
	{
		return binds;
	}

	public void setBinds(List<BindAccountToManager> binds)
	{
		this.binds = binds;
	}

	private List<BindAccountToManager> binds=new ArrayList<BindAccountToManager>();

	public String getSaleid() {
		return saleid;
	}

	public void setSaleid(String saleid) {
		this.saleid = saleid;
	}

	public String getAccountid() {
		return accountid;
	}

	public void setAccountid(String accountid) {
		this.accountid = accountid;
	}

	public Double getTranamt() {
		return tranamt;
	}

	public void setTranamt(Double tranamt) {
		this.tranamt = tranamt;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public String getLastmodifybranchid() {
		return lastmodifybranchid;
	}

	public void setLastmodifybranchid(String lastmodifybranchid) {
		this.lastmodifybranchid = lastmodifybranchid;
	}

	public String getLastmodifyacctid() {
		return lastmodifyacctid;
	}

	public void setLastmodifyacctid(String lastmodifyacctid) {
		this.lastmodifyacctid = lastmodifyacctid;
	}

	public Date getLastmodifydate() {
		return lastmodifydate;
	}

	public void setLastmodifydate(Date lastmodifydate) {
		this.lastmodifydate = lastmodifydate;
	}

	
}