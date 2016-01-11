package com.nantian.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nantian.custom.AccountType;
import com.nantian.custom.Result;
import com.nantian.custom.Utils;
import com.nantian.domain.BaseAccount;
import com.nantian.domain.BindAccountToManager;
import com.nantian.domain.FinanceDetail;
import com.nantian.domain.PersonPledgeAccount;
import com.nantian.domain.SysAccount;
import com.nantian.domain.SysAccountFeatureKey;
import com.nantian.service.BaseAccountService;
import com.nantian.service.PledgeService;
import com.nantian.service.SysAccountService;
@Controller
public class PledgeController
{
	@Resource
	private BaseAccountService baseAccountService;
	@Resource
	private SysAccountService sysAccountService;
	@Resource
	private PledgeService pledgeService;
	
	@RequestMapping("/personPledge/queryPersonPledgeDetailList")
	@ResponseBody
	public Result queryPeronPledgeDetailList(String accountid,
											 String accountname,
											 String branchid,
											 String managerid,
											 @RequestParam(value="page")String startPage,
												@RequestParam(value="limit")String pageSize,
												HttpSession session)
	{
		SysAccount sysAccount =(SysAccount)session.getAttribute("user");
		
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		
		List<PersonPledgeAccount> list=pledgeService.queryPledgeList(accountid, accountname, branchid, managerid, sysAccount.getBranchid(), sysAccount.getAccountid());
		
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("/personPledge/addPersonPledgeDetail")
	@ResponseBody
	public Result addPersonPledgeDetail(String accountid,
										String accountname,
										String tranamt,
										String startdate,
										String enddate,
										String bindlist,HttpSession session) throws ParseException
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		BaseAccount baseAccount=new BaseAccount();
		baseAccount.setBranchid(sysAccount.getBranchid());
		baseAccount.setAccountid(accountid);
		baseAccount.setAccountname(accountname);
		baseAccount.setAccounttype(AccountType.PERSON_FIXED_ACCOUNT);
		baseAccount.setSubaccttype("1");
		PersonPledgeAccount detail=new PersonPledgeAccount();
		String saleId = Utils.get16UUID();
		detail.setSaleid(saleId);
		detail.setAccountid(accountid);
		detail.setTranamt(Double.valueOf(tranamt));
		detail.setStartdate(Utils.str82date(startdate));
		detail.setEnddate(Utils.str82date(enddate));
		detail.setLastmodifybranchid(sysAccount.getBranchid());
		detail.setLastmodifyacctid(sysAccount.getAccountid());
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
		try
		{
			pledgeService.addPersonPledgeDetail(baseAccount, detail, bindArrayList);
			
		}
		catch(Exception e)
		{
			return new Result(false);
		}
		return new Result(true);
	}
	
	@RequestMapping("/personPledge/updatePersonPledgeDetail")
	@ResponseBody
	public Result updatePersonPledgeDetail(String accountid,
									String accountname,
									String tranamt,
									String startdate,
									String enddate,
									String saleid,
									String bindlist,HttpSession session) throws ParseException
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		BaseAccount baseAccount=new BaseAccount();
		baseAccount.setBranchid(sysAccount.getBranchid());
		baseAccount.setAccountid(accountid);
		baseAccount.setAccountname(accountname);
		baseAccount.setAccounttype(AccountType.PERSON_FIXED_ACCOUNT);
		PersonPledgeAccount detail=new PersonPledgeAccount();
		detail.setSaleid(saleid);
		detail.setAccountid(accountid);
		detail.setTranamt(Double.valueOf(tranamt));
		detail.setStartdate(Utils.str82date(startdate));
		detail.setEnddate(Utils.str82date(enddate));
		detail.setLastmodifybranchid(sysAccount.getBranchid());
		detail.setLastmodifyacctid(sysAccount.getAccountid());
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
		try
		{
			pledgeService.updatePersonPledgeDetail(baseAccount, detail, bindArrayList);
		}
		catch(Exception e)
		{
			return new Result(false);
		}
		return new Result(true);
	}
	
	@RequestMapping("/personPledge/deletePersonPledgeDetail")
	@ResponseBody
	public Result deletePersonPledgeDetail(String accountid,String saleid)
	{
		try
		{
			pledgeService.deletePersonPledgeDetail(saleid,accountid);
		}
		catch(Exception e)
		{
			return new Result(false);
		}
		return new Result(true);
	}
}
