package com.nantian.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nantian.custom.FormPostResult;
import com.nantian.custom.Result;
import com.nantian.domain.SysAccount;
import com.nantian.domain.SysAccountFeatureKey;
import com.nantian.service.SysAccountService;

@Controller
public class AccountController {
	
	@Resource
	private SysAccountService sysAccountService;
	@RequestMapping("/login")
	@ResponseBody
	public FormPostResult  login(@RequestParam(value="accountid") String accountid,String password,HttpSession httpSession){
		System.out.println("验证登录 accountid="+accountid);
		SysAccount sysAccount=sysAccountService.querySysAccountByAccountId(accountid);
		if(sysAccount!=null)
		{
			System.out.println(password+" "+sysAccount.getPassword());
			if(sysAccount.getPassword().equals(password)==false)
			{
				FormPostResult result=new FormPostResult(false);
				result.addErros("password", "密码错误");
				return result;
			}
			System.out.println("用户验证成功");
			httpSession.setAttribute("user", sysAccount);
			return new FormPostResult(true);
		}
		else
		{
			FormPostResult result=new FormPostResult(false);
			result.addErros("accountid", "用户不存在");
			return result;
		}
		
	}
	@RequestMapping("/logout")
	@ResponseBody
	public Result  logout(HttpSession httpSession){
		httpSession.removeAttribute("user");
		return new Result(true);
		
	}
	@RequestMapping("/account/queryAccount")
	@ResponseBody
	public Result  queryAccount(HttpSession httpSession)
	{
		SysAccount sysAccount=(SysAccount)httpSession.getAttribute("user");
		Result result=new Result(true);
		result.setItems(sysAccount);
		return result;
		
	}
	@RequestMapping("/account/queryAccountList")
	@ResponseBody
	public Result queryAccountList(String accountid,String accountname,String branchid,String propertys,HttpServletRequest request,
			@RequestParam(value="page")String startPage,
			@RequestParam(value="limit")String pageSize,
			HttpSession session) throws UnsupportedEncodingException
	{
		ArrayList<String> propertyList=new ArrayList<String>();
		if(propertys!=null&&!propertys.equals(""))
		{
			String[] arrProperty=propertys.split(",");
			
			for(int i=0;i<arrProperty.length;i++)
			{
				propertyList.add(arrProperty[i]);
			}
		}
		//如果用户性质为客户经理则只能查询本人账户信息
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(sysAccount.getProperty().equals("3"))
		{
			accountid=sysAccount.getAccountid();
		}
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		List<SysAccount> list=sysAccountService.querySysAccountList(accountid, accountname,branchid,propertyList);
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("/account/addAccount")
	@ResponseBody
	public FormPostResult addAccount(SysAccount sysAccount,String[] busifeature,String rolelist)
	{
		if(busifeature!=null)
		{
			for(int i=0;i<busifeature.length;i++)
			{
				SysAccountFeatureKey feature=new SysAccountFeatureKey();
				feature.setAccountid(sysAccount.getAccountid());
				feature.setType("1");
				feature.setValue(busifeature[i]);
				sysAccount.getBusiFeature().add(feature);
			}
		}
		if(sysAccountService.addSysAccount(sysAccount, rolelist)!=0)
		{
			return new FormPostResult(true);
		}
		else
		{
			return new FormPostResult(false);
		}
	}
	@RequestMapping("/account/deleteAccount")
	@ResponseBody
	public Result deleteAccount(String accountid)
	{
		try
		{
			sysAccountService.deleteSysAccount(accountid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Result result=new Result(false);
			result.addErros("errmsg",e.getMessage());
			return result;
		}
		return new Result(true);
	}
	@RequestMapping("/account/modifyAccount")
	@ResponseBody
	public Result modifyAccount(SysAccount sysAccount,String[] busifeature,String rolelist)
	{
		if(busifeature!=null)
		{
			for(int i=0;i<busifeature.length;i++)
			{
				SysAccountFeatureKey feature=new SysAccountFeatureKey();
				feature.setAccountid(sysAccount.getAccountid());
				feature.setType("1");
				feature.setValue(busifeature[i]);
				sysAccount.getBusiFeature().add(feature);
			}
		}
		
		SysAccount tmpSysAccount=sysAccountService.querySysAccountByAccountId(sysAccount.getAccountid());
		if(tmpSysAccount==null)
		{
			return new Result(false,"用户不存在");
		}
		sysAccount.setPassword(tmpSysAccount.getPassword());
		if(sysAccountService.modifySysAccount(sysAccount, rolelist)!=0)
		{
			return new Result(true);
		}
		else
		{
			return new Result(false);
		}
	}
	
	@RequestMapping("account/modifyAccountPassword")
	@ResponseBody
	public Result modifyAccountPassowrd(String accountid,String oldpassword,String newpassword,HttpSession httpSession)
	{
		SysAccount sysAccount=sysAccountService.querySysAccountByAccountId(accountid);
		if(sysAccount!=null)
		{
			if(sysAccount.getPassword().equals(oldpassword)==false)
			{
				Result result=new Result(false);
				result.addErros("errmsg", "密码错误");
				return result;
			}
			else
			{
				sysAccount.setPassword(newpassword);
				if(sysAccountService.modifySysAccount(sysAccount)==0)
				{
					return new Result(false);
				}
			}
		}
		else
		{
			Result result=new Result(false);
			result.addErros("accountid", "用户不存在");
			return result;
		}
		httpSession.removeAttribute("user");
		return new Result(true);
	}
	@RequestMapping("account/querySysAccountListByBranchId")
	@ResponseBody
	public Result querySysAccountListByBranchId(String branchid)
	{
		List<SysAccount> list=sysAccountService.querySysAccountListByBranchId(branchid);
		Result result=new Result(true);
		result.setItems(list);
		return result;
	}
	@RequestMapping("account/batchModifySysAccountBranch")
	@ResponseBody
	public Result batchModifySysAccountBranch(String srcbranchid,String destbranchid,String list)
	{
		try
		{
			sysAccountService.batchModifySysAccountBranch(srcbranchid, destbranchid, list);
		}
		catch(Exception e)
		{
			return new Result(false);
		}
		return new Result(true);
	}
}

