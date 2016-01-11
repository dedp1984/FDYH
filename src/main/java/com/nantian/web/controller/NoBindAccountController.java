package com.nantian.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nantian.custom.Result;
import com.nantian.custom.Utils;
import com.nantian.domain.NoBindAccount;
import com.nantian.domain.ProcessAccount;
import com.nantian.domain.SysAccount;
import com.nantian.domain.SysAccountFeatureKey;
import com.nantian.service.NoBindAccountService;
import com.nantian.service.SysAccountService;
import com.nantian.domain.ProcessBind;

@Controller
public class NoBindAccountController
{
	@Resource
	private NoBindAccountService noBindAccountService;
	@Resource
	private SysAccountService sysAccountService;
	
	@RequestMapping("/noBindAccount/queryNoBindAccountList")
	@ResponseBody
	public Result queryNoBindAccountListByBranchid(String accountid,String accountname,String accounttypes,@RequestParam(value="page")String startPage,
																@RequestParam(value="limit")String pageSize,HttpSession session)
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		
		ArrayList<String> acctTypeList=new ArrayList<String>();
		if(accounttypes!=null&&!accounttypes.equals(""))
		{
			String[] types=accounttypes.split(",");
			for(int i=0;i<types.length;i++)
			{
				acctTypeList.add(types[i]);
			}
		}
		else
		{
			List<SysAccountFeatureKey>  busifeatures=sysAccountService.querySysAccountFeature(sysAccount.getAccountid(), "1",null);
			acctTypeList=Utils.getAccoutTypeListByFeatures(busifeatures);
		}
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		String branchid="";
		if(Utils.isAuthorQueryAllBranchData(sysAccount)==false)
		{
			branchid=sysAccount.getBranchid();
		}
		List<NoBindAccount> list=noBindAccountService.queryNoBindAccountListByBranchid(branchid,accountid,accountname,acctTypeList);
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("/noBindAccount/submitBindAccountToManager")
	@ResponseBody
	public Result submitBindAccountToManager(String branchid,String branchname,String accountid,String accountname,String accounttype,String bindlist,HttpSession session)
	{
		ArrayList<ProcessBind> bindArrayList=new ArrayList<ProcessBind>();
		String tmp1[]=bindlist.substring(0, bindlist.length()-1).split("#");
		for(int i=0;i<tmp1.length;i++)
		{
			String bind[]=tmp1[i].toString().split(",");
			ProcessBind processBind=new ProcessBind();
			processBind.setAccountid(accountid);
			processBind.setManagerid(bind[1]);
			processBind.setPercent(Double.valueOf(bind[2])/100.00);
			processBind.setType(bind[3]);
			bindArrayList.add(processBind);
		}
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(noBindAccountService.submitBindAccountToManager(branchid, accountid, accountname, accounttype, sysAccount.getAccountid(), bindArrayList)==1)
		{
			return new Result(true);
		}
		else
		{
			return new Result(false);
		}
		
	}
	@RequestMapping("/noBindAccount/submitBindAccountToBranch")
	@ResponseBody
	public Result submitBindAccountToBranch(String branchid,String branchname,String accountid,String accountname,String accounttype,HttpSession session)
	{
		try
		{
			noBindAccountService.submitBindAccountToBranch(branchid, accountid, accountname, accounttype);
		}
		catch(Exception e)
		{
			return new Result(false);
		}
		return new Result(true);
	}
	@RequestMapping("/nobindaccount/queryUnCheckBindAccountList")
	@ResponseBody
	public Result queryUnCheckBindAccountList(@RequestParam(value="page")String startPage,
												@RequestParam(value="limit")String pageSize,
												String accountid,
												String accountname,
												HttpSession session)
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		
		List<SysAccountFeatureKey>  busifeatures=sysAccountService.querySysAccountFeature(sysAccount.getAccountid(), "1",null);
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		List<ProcessAccount> list=noBindAccountService.queryUnCheckBindAccount(accountid, accountname,Utils.getAccoutTypeListByFeatures(busifeatures));
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	
	@RequestMapping("/nobindaccount/checkpass")
	@ResponseBody
	public Result checkPass(String accountid,boolean passed,HttpSession session)
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(noBindAccountService.checkBindAccount(accountid, passed, sysAccount.getAccountid())==1)
		{
			return new Result(true);
		}
		else
		{
			return new Result(false);
		}
	}
	
	@RequestMapping("/nobindaccount/queryCheckdBindAccountList")
	@ResponseBody
	public Result queryCheckdBindAccountList(@RequestParam(value="page")String startPage,
												@RequestParam(value="limit")String pageSize,
												String accountid,
												String accountname,
												String accounttype,
												String status,
												HttpSession session)
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
	
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		List<ProcessAccount> list=noBindAccountService.queryCheckBindAccountByUserid(accountid, accountname, sysAccount.getAccountid(),status,accounttype);
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	
	@RequestMapping("/noBindAccount/reSubmitBindAccountToManager")
	@ResponseBody
	public Result reSubmitBindAccountToManager(String branchid,String branchname,String accountid,String accountname,String accounttype,String bindlist,HttpSession session)
	{
		ArrayList<ProcessBind> bindArrayList=new ArrayList<ProcessBind>();
		String tmp1[]=bindlist.substring(0, bindlist.length()-1).split("#");
		for(int i=0;i<tmp1.length;i++)
		{
			String bind[]=tmp1[i].toString().split(",");
			ProcessBind processBind=new ProcessBind();
			processBind.setAccountid(accountid);
			processBind.setManagerid(bind[1]);
			processBind.setPercent(Double.valueOf(bind[2])/100.00);
			processBind.setType(bind[3]);
			bindArrayList.add(processBind);
		}
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(noBindAccountService.reSubmitBindAccountToManager(branchid, accountid, accountname, accounttype, sysAccount.getAccountid(), bindArrayList)==1)
		{
			return new Result(true);
		}
		else
		{
			return new Result(false);
		}
		
	}
	
}
