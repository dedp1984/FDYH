package com.nantian.domain;

public class ProcessBind extends ProcessBindKey {
    private Double percent;

    private String type;
    
    private String managername;
    
    public String getManagername()
	{
		return managername;
	}

	public void setManagername(String managername)
	{
		this.managername = managername;
	}

	private String branchname;
    
    private String branchid;
    public String getBranchname()
	{
		return branchname;
	}

	public void setBranchname(String branchname)
	{
		this.branchname = branchname;
	}

	public String getBranchid()
	{
		return branchid;
	}

	public void setBranchid(String branchid)
	{
		this.branchid = branchid;
	}

	public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}