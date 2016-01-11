package com.nantian.custom;

import java.util.HashMap;
import java.util.Map;

public class FormPostResult 
{
	private boolean success;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Map<String, Object> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, Object> errors) {
		this.errors = errors;
	}

	private Map<String, Object> errors = new HashMap<String, Object>();

	private Map<String,Object> rtnval=new HashMap<String, Object>();
	
	public Map<String, Object> getRtnval() {
		return rtnval;
	}

	public void setRtnval(Map<String, Object> rtnval) {
		this.rtnval = rtnval;
	}

	public FormPostResult(boolean success)
	{
		this.success=success;
	}
	
	public FormPostResult(boolean success,Map<String, Object>errors)
	{
		this.success=success;
		this.errors=errors;
	}
	
	public void addErros(String key,Object object)
	{
		errors.put(key, object);
	}
	
	public void addRtnVal(String key,Object object)
	{
		rtnval.put(key, object);
	}
}
