package com.nantian.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SysAccount {
    private String accountid;

    private String accountname;

    private String password;
   
    private String property;
    
    private String branchid;
    
    private String areaid;
    
	private Date birthday;

    private String address;

    private String phone;

    private String email;

    private String status;

    private Date expiredate;
    /**
     * 用户对应机构信息
     * **/
    private SysBranch  branch;
    /**
     * 用户对应角色信息
     * **/
    private List<SysAccountRoleKey> roles=new ArrayList<SysAccountRoleKey>();
    
    /**
     * 用户对应业务范围特性信息
     * **/
    
    public List<SysAccountFeatureKey> busiFeature=new ArrayList<SysAccountFeatureKey>();
    
    public List<SysAccountFeatureKey> getBusiFeature()
	{
		return busiFeature;
	}

	public void setBusiFeature(List<SysAccountFeatureKey> busiFeature)
	{
		this.busiFeature = busiFeature;
	}

	public String getAreaid() {
		return areaid;
	}

	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}
	
	public List<SysAccountRoleKey> getRoles() {
		return roles;
	}

	public void setRoles(List<SysAccountRoleKey> roles) {
		this.roles = roles;
	}

	public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(Date expiredate) {
        this.expiredate = expiredate;
    }


	public String getBranchid() {
		return branchid;
	}

	public void setBranchid(String branchid) {
		this.branchid = branchid;
	}

	public SysBranch getBranch() {
		return branch;
	}

	public void setBranch(SysBranch branch) {
		this.branch = branch;
	}
	
	public String toString(){
		return  "accountid="+accountid+":accountnam="+ accountname+":password="+ password+":property="+ property+":branchid="+ branchid+":areaid="+ areaid+":address="+ address+":phone="+ phone+":email="+ email+":status="+ status;
	}
}