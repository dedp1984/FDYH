package com.nantian.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nantian.custom.AccountType;
import com.nantian.custom.Result;
import com.nantian.custom.Utils;
import com.nantian.domain.BaseAccount;
import com.nantian.domain.BindAccountToManager;
import com.nantian.domain.FinanceDetail;
import com.nantian.domain.FinanceProductType;
import com.nantian.domain.SysAccount;
import com.nantian.domain.SysAccountFeatureKey;
import com.nantian.service.BaseAccountService;
import com.nantian.service.FinanceProductService;
import com.nantian.service.SysAccountService;

@Controller
public class FinanceProductController
{
	@Resource
	private FinanceProductService financeProductService;
	@Resource
	private BaseAccountService baseAccountService;
	@Resource
	private SysAccountService sysAccountService;
	
	@RequestMapping("/financeProduct/queryFinanceProductTypeList")
	@ResponseBody
	public Result queryFinanceProductTypeList(String name,String type,@RequestParam(value="page")String startPage,
												@RequestParam(value="limit")String pageSize)
	{
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		List<FinanceProductType> list=financeProductService.queryFinanceProductTypeListByName(name,type);
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("/financeProduct/addFinanceProductType")
	@ResponseBody
	public Result addFinanceProductType(String name,String type,Boolean isjsrj,Boolean isjsckye)
	{
		if(isjsckye!=null)
			isjsrj=true;
		if(financeProductService.addFinanceProductType(type, name, isjsrj, isjsckye)==1)
		{
			return new Result(true);
		}
		return new Result(false);
	}
	@RequestMapping("/financeProduct/deleteFinanceProductType")
	@ResponseBody
	public Result deleteFinanceProductType(String id)
	{
		if(financeProductService.delFinanceProductType(id)==1)
		{
			return new Result(true);
		}
		return new Result(false);
	}
	@RequestMapping("/financeProduct/updateFinanceProductType")
	@ResponseBody
	public Result updateFinanceProductType(String id,String name,String type,Boolean isjsrj,Boolean isjsckye)
	{
		if(isjsckye!=null)
			isjsrj=true;
		if(financeProductService.updateFinanceProductType(id, type, name, isjsrj, isjsckye)==1)
		{
			return new Result(true);
		}
		return new Result(false);
	}
	
	@RequestMapping("/financeProduct/queryFinanceDetailList")
	@ResponseBody
	public Result queryFinanceDetailList(String accountid,String accountname,@RequestParam(value="page")String startPage,
												@RequestParam(value="limit")String pageSize,
												HttpSession session)
	{
		SysAccount sysAccount =(SysAccount)session.getAttribute("user");
		List<SysAccountFeatureKey>  busifeatures=sysAccountService.querySysAccountFeature(sysAccount.getAccountid(), "1",null);
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		
		List<BaseAccount> list=baseAccountService.queryBaseAccountList(accountid, accountname, null, sysAccount.getBranchid(),null,
				null,null,sysAccount.getAccountid());
		for(int i=0;i<list.size();i++)
		{
			BaseAccount baseAccount=list.get(i);
			List<FinanceDetail> financeList=baseAccount.getFinanceDetail();
			for(int j=0;j<financeList.size();j++)
			{
				FinanceDetail finance=financeList.get(j);
				if(finance.getLastmodifyid().equals(sysAccount.getAccountid()))
				{
					finance.setEditable(true);
				}
				else
				{
					finance.setEditable(false);
				}
				financeList.set(j, finance);
			}
			baseAccount.setFinanceDetail(financeList);
			list.set(i, baseAccount);
		}
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("/financeProduct/addFinanceDetail")
	@ResponseBody
	public Result addFinanceDetail(String accountid,
									String accountname,
									String productid,
									String productbatch,
									String tranamt,
									String iszy,
									String startdate,
									String enddate,
									String channel,
									String type,
									String bindlist,HttpSession session) throws ParseException
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		BaseAccount baseAccount=new BaseAccount();
		baseAccount.setBranchid(sysAccount.getBranchid());
		baseAccount.setAccountid(accountid);
		baseAccount.setAccountname(accountname);
		baseAccount.setAccounttype(type);
		FinanceDetail detail=new FinanceDetail();
		String saleId = Utils.get16UUID();
		detail.setSaleid(saleId);
		detail.setAccountid(accountid);
		detail.setProductid(productid);
		detail.setProductbatch(productbatch);
		detail.setTranamt(Double.valueOf(tranamt));
		detail.setYeardayavg(0.00);
		detail.setStartdate(Utils.str82date(startdate));
		detail.setEnddate(Utils.str82date(enddate));
		detail.setChannel(channel);
		detail.setIszy(iszy);
		detail.setLastmodifyid(sysAccount.getAccountid());
		detail.setLastmodifydate(new Date());
		ArrayList<BindAccountToManager> bindArrayList=new ArrayList<BindAccountToManager>();
		String tmp1[]=bindlist.substring(0, bindlist.length()-1).split("#");
		for(int i=0;i<tmp1.length;i++)
		{
			String bind[]=tmp1[i].toString().split(",");
			BindAccountToManager binded=new BindAccountToManager();
			binded.setAccountid(accountid);
			binded.setManagerid(bind[1]);
			binded.setPercent(Double.valueOf(bind[2])/100.00);
			binded.setType(bind[3]);
			bindArrayList.add(binded);
		}
		if(financeProductService.addFinanceDetail(baseAccount, detail, bindArrayList)!=0)
		{
			return new Result(true);
		}
		return new Result(false);
		
	}
	
	@RequestMapping("/financeProduct/updateFinanceDetail")
	@ResponseBody
	public Result updateFinanceDetail(String accountid,
									String accountname,
									String productid,
									String productbatch,
									String tranamt,
									String iszy,
									String startdate,
									String enddate,
									String channel,
									String type,
									String saleid,
									String bindlist,HttpSession session) throws ParseException
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		BaseAccount baseAccount=new BaseAccount();
		baseAccount.setBranchid(sysAccount.getBranchid());
		baseAccount.setAccountid(accountid);
		baseAccount.setAccountname(accountname);
		baseAccount.setAccounttype(type);
		FinanceDetail detail=new FinanceDetail();
		detail.setSaleid(saleid);
		detail.setAccountid(accountid);
		detail.setProductid(productid);
		detail.setProductbatch(productbatch);
		detail.setTranamt(Double.valueOf(tranamt));
		detail.setYeardayavg(0.00);
		detail.setStartdate(Utils.str82date(startdate));
		detail.setEnddate(Utils.str82date(enddate));
		detail.setChannel(channel);
		detail.setIszy(iszy);
		detail.setLastmodifyid(sysAccount.getAccountid());
		detail.setLastmodifydate(new Date());
		ArrayList<BindAccountToManager> bindArrayList=new ArrayList<BindAccountToManager>();
		String tmp1[]=bindlist.substring(0, bindlist.length()-1).split("#");
		for(int i=0;i<tmp1.length;i++)
		{
			String bind[]=tmp1[i].toString().split(",");
			BindAccountToManager binded=new BindAccountToManager();
			binded.setAccountid(accountid);
			binded.setManagerid(bind[1]);
			binded.setPercent(Double.valueOf(bind[2])/100.00);
			binded.setType(bind[3]);
			bindArrayList.add(binded);
		}
		if(financeProductService.updateFinanceDetail(baseAccount, detail, bindArrayList)!=0)
		{
			return new Result(true);
		}
		return new Result(false);
		
	}
	
	@RequestMapping("/financeProduct/deleteFinanceDetail")
	@ResponseBody
	public Result deleteFinanceDetail(String accountid,String saleid)
	{
		if(financeProductService.deleteFinanceDetail(saleid,accountid)!=0)
		{
			return new Result(true);
		}
		return new Result(false);
	}
	@RequestMapping("/financeProduct/queryFinanceTypeList")
	@ResponseBody
	public Result queryFinanceTypeList(HttpSession session)
	{
		List<Map> list=new ArrayList<Map>();
		HashMap<String,String> publicFinance=new HashMap<String,String>();
		publicFinance.put("value", "6");
		publicFinance.put("name", "对公理财");
		HashMap<String,String> personFinance=new HashMap<String,String>();
		personFinance.put("value", "7");
		personFinance.put("name", "个人理财");
		HashMap<String,String> allFinance=new HashMap<String,String>();
		allFinance.put("value", "6,7");
		allFinance.put("name", "全部");
		
		list.add(publicFinance);
		list.add(personFinance);
		/*
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		List<SysAccountFeatureKey>  busifeatures=sysAccountService.querySysAccountFeature(sysAccount.getAccountid(), "1",null);
		for(int i=0;i<busifeatures.size();i++)
		{
			SysAccountFeatureKey feature=busifeatures.get(i);
			if(feature.getType().equals("1"))
			{
				if(feature.getValue().equals("1"))
				{
					list.add(publicFinance);
				}
				else
				{
					list.add(personFinance);
				}
			}
		}*/
		Result result=new Result(true);
		result.setItems(list);
		return result;
	}
}
