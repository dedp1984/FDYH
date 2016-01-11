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
import com.nantian.domain.BaseAccount;
import com.nantian.domain.BindAccountToManager;
import com.nantian.domain.ProcessBind;
import com.nantian.domain.SysAccount;
import com.nantian.domain.SysAccountFeatureKey;
import com.nantian.service.BaseAccountService;
import com.nantian.service.SysAccountService;

@Controller
public class BaseAccountController
{
	@Resource
	private BaseAccountService baseAccountService;
	@Resource
	private SysAccountService sysAccountService;
	
	@RequestMapping("/baseAccount/queryBaseAccountList")
	@ResponseBody
	public Result queryBaseAccountList(String accountid ,String accountname,String accounttype,String managerid,@RequestParam(value="page")String startPage,
										@RequestParam(value="limit")String pageSize,
										HttpSession session)
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		List<SysAccountFeatureKey>  busifeatures=sysAccountService.querySysAccountFeature(sysAccount.getAccountid(), "1",null);
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		List<BaseAccount> list=baseAccountService.queryBaseAccountList(accountid, accountname, accounttype,null,managerid,Utils.getAccoutTypeListByFeatures(busifeatures),null,null);
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("/baseAccount/queryBaseAccountByAccountId")
	@ResponseBody
	public Result queryBaseAccountByAccountId(String accountid)
	{
		BaseAccount baseAccount=baseAccountService.queryBaseAccountByAccountId(accountid);
		if(baseAccount!=null)
		{
			Result result=new Result(true);
			result.setItems(baseAccount);
			return result;
		}
		else
		{
			return new Result(false);
		}
	}
	
	@RequestMapping("/BaseAccount/updateBindAndAcct")
	@ResponseBody
	public Result updateBindAccountToManager(String branchid,String branchname,String accountid,String accountname,String accounttype,String bindlist,HttpSession session)
	{
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
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(baseAccountService.updateBindAccountToManager(branchid, accountid, accountname, accounttype, bindArrayList)==1)
		{
			return new Result(true);
		}
		else
		{
			return new Result(false);
		}
		
	}
	@RequestMapping("/BaseAccount/addBindAndAcct")
	@ResponseBody
	public Result addBindAccountToManager(String branchid,String branchname,String accountid,String accountname,String accounttype,String bindlist,HttpSession session)
	{
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
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(baseAccountService.addBindAccountToManager(branchid, accountid, accountname, accounttype, bindArrayList)==1)
		{
			return new Result(true);
		}
		else
		{
			return new Result(false);
		}
		
	}
	@RequestMapping("/BaseAccount/deleteBindAndAcct")
	@ResponseBody
	public Result deleteBindAccountToManager(String accountid)
	{
		if(baseAccountService.deleteBindAccountToManager(accountid)!=0)
		{
			return new Result(true);
		}
		else
		{
			return new Result(false);
		}
	}
	@RequestMapping("/BaseAccount/transManagerBind")
	@ResponseBody
	public Result transManagerBind(String paramstr,String changebranchid,String managerid)
	{
		
		
		try
		{
			baseAccountService.transManagerBinds(paramstr,changebranchid,managerid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new Result(false);
		}
		return new Result(true);
	}
	
	
}
