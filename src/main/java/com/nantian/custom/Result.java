package com.nantian.custom;

import java.util.HashMap;
import java.util.Map;

public class Result 
{
	/**
	 * 处理结果
	 * true-成功
	 * false-失败
	 * **/
	private boolean success;
	/**
	 * 错误信息
	 * 默认值errmsg-错误提示信息
	 * 默认值errcode-错误代码
	 * **/
	private Map<String, Object> errors = new HashMap<String, Object>();
	/**
	 * 多页查询当前页记录条数
	 * **/
	private String totalsize;
	/**
	 * 多页查询当前页记录信息 
	 * **/
	private Object items;
	
	public Result()
	{
		
	}
	
	public Result(boolean flag)
	{
		this.success=flag;
	}
	public Result(boolean flag,String errmsg)
	{
		this.success=flag;
		this.errors.put("errmsg", errmsg);
	}
	
	public Object getItems() {
		return items;
	}
	public void setItems(Object items) {
		this.items = items;
	}
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
	public String getTotalsize() {
		return totalsize;
	}
	public void setTotalsize(String totalsize) {
		this.totalsize = totalsize;
	}
	
	public void addErros(String key,Object object)
	{
		errors.put(key, object);
	}
}
