package com.nantian.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BaseAccount {
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

	private String subaccttype;

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

	public String getSubaccttype() {
		return subaccttype;
	}

	public void setSubaccttype(String subaccttype) {
		this.subaccttype = subaccttype;
	}

	
	private SysBranch branch;
    
    private double percent;
    
    public double getPercent()
	{
		return percent;
	}

	public void setPercent(double percent)
	{
		this.percent = percent;
	}

	public SysBranch getBranch()
	{
		return branch;
	}

	public void setBranch(SysBranch branch)
	{
		this.branch = branch;
	}
    
    private List<HashMap<String,Object>> binds=new ArrayList<HashMap<String,Object>>();


    public List<HashMap<String,Object>> getBinds()
	{
		return binds;
	}

	public void setBinds(List<HashMap<String,Object>> binds)
	{
		this.binds = binds;
	}

	private List<FinanceDetail> financeDetail=new ArrayList<FinanceDetail>();
	
	

	public List<FinanceDetail> getFinanceDetail()
	{
		return financeDetail;
	}

	public void setFinanceDetail(List<FinanceDetail> financeDetail)
	{
		this.financeDetail = financeDetail;
	}

	private List<PersonPledgeAccount> pledgeDetail=new ArrayList<PersonPledgeAccount>();
	
	public List<PersonPledgeAccount> getPledgeDetail()
	{
		return pledgeDetail;
	}

	public void setPledgeDetail(List<PersonPledgeAccount> pledgeDetail)
	{
		this.pledgeDetail = pledgeDetail;
	}
   
}