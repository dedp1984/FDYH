package com.nantian.domain;

public class AccountBalRecord extends AccountBalRecordKey {
    
	private String branchid;

    private String accountname;

    private String accounttype;

    private String customno;

    private String subcode;

    private String currency;

    private String maccountid;

    private String property;

    private String acctstatus;

    private Double bal;

    private Double yearavg;

    private Double badyearavg;

    private Double m1;

    private Double m2;

    private Double m3;

    private Double m4;

    private Double m5;

    private Double m6;

    private Double m7;

    private Double m8;

    private Double m9;

    private Double m10;

    private Double m11;

    private Double m12;
    
    private Double mrate;
    
    private String fdfs;
    
    private Double fdbl;
    
    private String wjfl;

    public AccountBalRecord()
    {
    	branchid=null;
    	accountname=null;
    	accounttype=null;
    	customno=null;
    	subcode=null;
    	maccountid=null;
    	property=null;
    	acctstatus=null;
    	super.setAccountid(null);
    	super.setGendate(null);
    	bal=0.00;
    	yearavg=0.00;
    	badyearavg=0.00;
    	m1=0.00;
    	m2=0.00;
    	m3=0.00;
    	m4=0.00;
    	m5=0.00;
    	m6=0.00;
    	m7=0.00;
    	m8=0.00;
    	m9=0.00;
    	m10=0.00;
    	m11=0.00;
    	m12=0.00;
    	mrate=0.00;
    	fdfs=null;
    	fdbl=0.00;
    	wjfl=null;
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

    public String getCustomno() {
        return customno;
    }

    public void setCustomno(String customno) {
        this.customno = customno;
    }

    public String getSubcode() {
        return subcode;
    }

    public void setSubcode(String subcode) {
        this.subcode = subcode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMaccountid() {
        return maccountid;
    }

    public void setMaccountid(String maccountid) {
        this.maccountid = maccountid;
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

    public Double getBal() {
        return bal;
    }

    public void setBal(Double bal) {
        this.bal = bal;
    }

    public Double getYearavg() {
        return yearavg;
    }

    public void setYearavg(Double yearavg) {
        this.yearavg = yearavg;
    }

    public Double getBadyearavg() {
        return badyearavg;
    }

    public void setBadyearavg(Double badyearavg) {
        this.badyearavg = badyearavg;
    }

    public Double getM1() {
        return m1;
    }

    public void setM1(Double m1) {
        this.m1 = m1;
    }

    public Double getM2() {
        return m2;
    }

    public void setM2(Double m2) {
        this.m2 = m2;
    }

    public Double getM3() {
        return m3;
    }

    public void setM3(Double m3) {
        this.m3 = m3;
    }

    public Double getM4() {
        return m4;
    }

    public void setM4(Double m4) {
        this.m4 = m4;
    }

    public Double getM5() {
        return m5;
    }

    public void setM5(Double m5) {
        this.m5 = m5;
    }

    public Double getM6() {
        return m6;
    }

    public void setM6(Double m6) {
        this.m6 = m6;
    }

    public Double getM7() {
        return m7;
    }

    public void setM7(Double m7) {
        this.m7 = m7;
    }

    public Double getM8() {
        return m8;
    }

    public void setM8(Double m8) {
        this.m8 = m8;
    }

    public Double getM9() {
        return m9;
    }

    public void setM9(Double m9) {
        this.m9 = m9;
    }

    public Double getM10() {
        return m10;
    }

    public void setM10(Double m10) {
        this.m10 = m10;
    }

    public Double getM11() {
        return m11;
    }

    public void setM11(Double m11) {
        this.m11 = m11;
    }

    public Double getM12() {
        return m12;
    }

    public void setM12(Double m12) {
        this.m12 = m12;
    }
	public Double getMrate()
	{
		return mrate;
	}
	public void setMrate(Double mrate)
	{
		this.mrate = mrate;
	}
	public String getFdfs()
	{
		return fdfs;
	}
	public void setFdfs(String fdfs)
	{
		this.fdfs = fdfs;
	}
	public Double getFdbl()
	{
		return fdbl;
	}
	public void setFdbl(Double fdbl)
	{
		this.fdbl = fdbl;
	}
	public String getWjfl()
	{
		return wjfl;
	}
	public void setWjfl(String wjfl)
	{
		this.wjfl = wjfl;
	}
}