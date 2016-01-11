package com.nantian.domain;

import java.util.Date;

public class FinanceDetail {
    

	private String saleid;

	private String accountid;

	private String productid;

	private String productbatch;

	private Double tranamt;

	private Double yeardayavg;

	private Date startdate;

	private Date enddate;

	private String channel;

	private String lastmodifyid;
	
	private String lastmodifyname;

	private Date lastmodifydate;

	private String iszy;
	
	private boolean editable;

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

	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}

	public String getProductbatch() {
		return productbatch;
	}

	public void setProductbatch(String productbatch) {
		this.productbatch = productbatch;
	}

	public Double getTranamt() {
		return tranamt;
	}

	public void setTranamt(Double tranamt) {
		this.tranamt = tranamt;
	}

	public Double getYeardayavg() {
		return yeardayavg;
	}

	public void setYeardayavg(Double yeardayavg) {
		this.yeardayavg = yeardayavg;
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

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getLastmodifyid() {
		return lastmodifyid;
	}

	public void setLastmodifyid(String lastmodifyid) {
		this.lastmodifyid = lastmodifyid;
	}

	public Date getLastmodifydate() {
		return lastmodifydate;
	}

	public void setLastmodifydate(Date lastmodifydate) {
		this.lastmodifydate = lastmodifydate;
	}

	public String getIszy() {
		return iszy;
	}

	public void setIszy(String iszy) {
		this.iszy = iszy;
	}

	public String getLastmodifyname()
	{
		return lastmodifyname;
	}

	public void setLastmodifyname(String lastmodifyname)
	{
		this.lastmodifyname = lastmodifyname;
	}

	public boolean isEditable()
	{
		return editable;
	}

	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}
}