package com.nantian.domain;

public class FinanceProductType {
    private String id;

    private String type;

    private String name;
    
    private String comment;

    public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	private Boolean isjsrj;

    private Boolean isjsckye;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsjsrj() {
        return isjsrj;
    }

    public void setIsjsrj(Boolean isjsrj) {
        this.isjsrj = isjsrj;
    }

    public Boolean getIsjsckye() {
        return isjsckye;
    }

    public void setIsjsckye(Boolean isjsckye) {
        this.isjsckye = isjsckye;
    }
}