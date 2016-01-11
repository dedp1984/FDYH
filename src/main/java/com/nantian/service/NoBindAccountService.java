package com.nantian.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nantian.custom.Utils;
import com.nantian.dao.BaseAccountMapper;
import com.nantian.dao.BindAccountToManagerMapper;
import com.nantian.dao.NoBindAccountMapper;
import com.nantian.dao.ProcessAccountMapper;
import com.nantian.dao.ProcessBindMapper;
import com.nantian.dao.SysAccountFeatureMapper;
import com.nantian.domain.BaseAccount;
import com.nantian.domain.BindAccountToManager;
import com.nantian.domain.NoBindAccount;
import com.nantian.domain.ProcessAccount;
import com.nantian.domain.ProcessBind;

@Service
@Transactional
public class NoBindAccountService {
	
	@Resource
	private NoBindAccountMapper noBindAccountMapper;
	@Resource
	private ProcessAccountMapper processAccountMapper;
	@Resource
	private ProcessBindMapper processBindMapper;
	@Resource
	private BaseAccountMapper baseAccountMapper;
	@Resource
	private BindAccountToManagerMapper bindAccountMapper;
	@Resource
	private SysAccountFeatureMapper sysAccountFeatureMapper;
	@Resource
	private BindAccountToManagerMapper atmMapper;
	
	public int saveNoBindAccount(NoBindAccount noBindAccount)
	{
		if(noBindAccountMapper.selectByPrimaryKey(noBindAccount.getAccountid())==null)
		{
			return noBindAccountMapper.insert(noBindAccount);
		}
		return 0;
	}

	public List<NoBindAccount> queryNoBindAccountListByBranchid(String branchid,String accountid,String accountname,ArrayList<String> featureTypeList)
	{
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("branchid", branchid);
		map.put("list", featureTypeList);
		map.put("accountid", accountid);
		map.put("accountname", accountname);
		return noBindAccountMapper.selectByBranchId(map);
	}
	
	
	
	/**
	 * 未绑定账户申请绑定
	 * **/
	
	public int submitBindAccountToManager(String branchid,String accountid,String accountname,String type,String submitid,List<ProcessBind> processBindList)
	{
		NoBindAccount nAccount=noBindAccountMapper.selectByPrimaryKey(accountid);
		if(type.equals("4")||type.equals("5")||type.equals("7")||type.equals("8"))
		{
			BaseAccount baseAccount=new BaseAccount();
			baseAccount.setBranchid(nAccount.getBranchid());
			baseAccount.setAccountid(nAccount.getAccountid());
			baseAccount.setAccountname(nAccount.getAccountname());
			baseAccount.setAccounttype(nAccount.getAccounttype());
			baseAccount.setMaccountid(nAccount.getMaccountid());
			baseAccount.setCustomno(nAccount.getCustomno());
			baseAccount.setCurrency(nAccount.getCurrency());
			baseAccount.setSubcode(nAccount.getSubcode());
			baseAccount.setProperty(nAccount.getProperty());
			baseAccount.setAcctstatus(nAccount.getAcctstatus());
			/*检查基础账户表是否已经有对应账户信息, 有则更新对应账户信息及绑定关系，无则创建*/
			if(baseAccountMapper.selectByPrimaryKey(accountid)==null)
			{
				/*将待审批信息转移到有效表中*/
				baseAccountMapper.insert(baseAccount);
				bindAccountMapper.deleteByAccountId(accountid);
				for(int i=0;i<processBindList.size();i++)
				{
					ProcessBind pBind=processBindList.get(i);
					BindAccountToManager bind=new  BindAccountToManager();
					bind.setAccountid(pBind.getAccountid());
					bind.setManagerid(pBind.getManagerid());
					bind.setPercent(pBind.getPercent());
					bind.setType(pBind.getType());
					bindAccountMapper.insert(bind);
				}
			}
			else
			{
				baseAccountMapper.updateByPrimaryKey(baseAccount);
				bindAccountMapper.deleteByAccountId(accountid);
				for(int i=0;i<processBindList.size();i++)
				{
					ProcessBind pBind=processBindList.get(i);
					BindAccountToManager bind=new  BindAccountToManager();
					bind.setAccountid(pBind.getAccountid());
					bind.setManagerid(pBind.getManagerid());
					bind.setPercent(pBind.getPercent());
					bind.setType(pBind.getType());
					bindAccountMapper.insert(bind);
				}
			}
		}
		else
		{
			ProcessAccount pAccount=new ProcessAccount();
			pAccount.setAccountid(accountid);
			pAccount.setBranchid(branchid);
			pAccount.setAccountname(accountname);
			pAccount.setAccounttype(type);
			pAccount.setMaccountid(nAccount.getMaccountid());
			pAccount.setCustomno(nAccount.getCustomno());
			pAccount.setSubcode(nAccount.getSubcode());
			pAccount.setCurrency(nAccount.getCurrency());
			pAccount.setProperty(nAccount.getProperty());
			pAccount.setAcctstatus(nAccount.getAcctstatus());
			pAccount.setStatus("0");
			pAccount.setSubmitid(submitid);
			pAccount.setSubmitdate(new Date());
			processAccountMapper.insert(pAccount);
			for(int i=0;i<processBindList.size();i++)
			{
				ProcessBind processBind=processBindList.get(i);
				processBindMapper.insert(processBind);
			}
		}
		noBindAccountMapper.deleteByPrimaryKey(accountid);		
		return 1;
		
	}
	
	public void submitBindAccountToBranch(String branchid,String accountid,String accountname,String accounttype)
	{
		NoBindAccount nAccount=noBindAccountMapper.selectByPrimaryKey(accountid);
		nAccount.setBranchid(branchid);
		nAccount.setAccountname(accountname);
		nAccount.setAccounttype(accounttype);
		noBindAccountMapper.updateByPrimaryKey(nAccount);
	}
	
	/**
	 * 查询支行提交的账号绑定关系
	 * **/
	public List<ProcessAccount> queryUnCheckBindAccount(String accountid,String accountname,ArrayList<String> featureTypeList)
	{
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("accountid", accountid);
		map.put("accountname", accountname);
		map.put("status","0");
		map.put("list", featureTypeList);
		return processAccountMapper.selectUnCheckAccountList(map);
	}
	/**
	 * 查询用户提交的账户绑定关系
	 * **/
	public List<ProcessAccount> queryCheckBindAccountByUserid(String accountid,String accountname,String userid,String status,String accounttype)
	{
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("submitid", userid);
		map.put("accountid", accountid);
		map.put("accountname", accountname);
		map.put("status", status);
		map.put("accounttype", accounttype);
		return processAccountMapper.selectUnCheckAccountList(map);
	}
	
	/**
	 * 对提交申请的账户绑定关系审批
	 * 审批通过则将对应信息保存到T_ACCOUNT_BASE和T_MANAGER_ACCOUNT中
	 * 审批拒绝则修改T_PROCESS_ACCOUNT状态为9审批拒绝
	 * **/
	public int checkBindAccount(String accountid,boolean passed,String checkid)
	{
		ProcessAccount pAccount=processAccountMapper.selectByPrimaryKey(accountid);
		if(passed)
		{
			BaseAccount baseAccount=new BaseAccount();
			baseAccount.setBranchid(pAccount.getBranchid());
			baseAccount.setAccountid(pAccount.getAccountid());
			baseAccount.setAccountname(pAccount.getAccountname());
			baseAccount.setAccounttype(pAccount.getAccounttype());
			baseAccount.setMaccountid(pAccount.getMaccountid());
			baseAccount.setCustomno(pAccount.getCustomno());
			baseAccount.setCurrency(pAccount.getCurrency());
			baseAccount.setSubcode(pAccount.getSubcode());
			baseAccount.setProperty(pAccount.getProperty());
			baseAccount.setAcctstatus(pAccount.getAcctstatus());
			/*检查基础账户表是否已经有对应账户信息, 有则更新对应账户信息及绑定关系，无则创建*/
			if(baseAccountMapper.selectByPrimaryKey(accountid)==null)
			{
				/*将待审批信息转移到有效表中*/
				if(baseAccountMapper.insert(baseAccount)!=0)
				{
					ArrayList<ProcessBind> binds=(ArrayList<ProcessBind>) pAccount.getBinds();
					for(int i=0;i<binds.size();i++)
					{
						ProcessBind pBind=binds.get(i);
						BindAccountToManager bind=new  BindAccountToManager();
						bind.setAccountid(pBind.getAccountid());
						bind.setManagerid(pBind.getManagerid());
						bind.setPercent(pBind.getPercent());
						bind.setType(pBind.getType());
						if(bindAccountMapper.insert(bind)==0)
						{
							return 0;
						}
					}
				}
			}
			else
			{
				if(baseAccountMapper.updateByPrimaryKey(baseAccount)!=0)
				{
					bindAccountMapper.deleteByAccountId(accountid);
					ArrayList<ProcessBind> binds=(ArrayList<ProcessBind>) pAccount.getBinds();
					for(int i=0;i<binds.size();i++)
					{
						ProcessBind pBind=binds.get(i);
						BindAccountToManager bind=new  BindAccountToManager();
						bind.setAccountid(pBind.getAccountid());
						bind.setManagerid(pBind.getManagerid());
						bind.setPercent(pBind.getPercent());
						bind.setType(pBind.getType());
						if(bindAccountMapper.insert(bind)==0)
						{
							return 0;
						}
					}
				}
			}
			
			/*修改当前审批记录*/
			pAccount.setCheckdate(new Date());
			pAccount.setCheckid(checkid);
			pAccount.setStatus("1");
		}
		else
		{
			pAccount.setStatus("9");
			pAccount.setCheckdate(new Date());
		}
		return processAccountMapper.updateByPrimaryKey(pAccount);
		
	}
	
	/**
	 * 重新提交绑定申请
	 * **/
	
	public int reSubmitBindAccountToManager(String branchid,String accountid,String accountname,String type,String submitid,List<ProcessBind> processBindList)
	{
		ProcessAccount pAccount=this.processAccountMapper.selectByPrimaryKey(accountid);
		pAccount.setAccountid(accountid);
		pAccount.setBranchid(branchid);
		pAccount.setAccountname(accountname);
		pAccount.setAccounttype(type);
		pAccount.setStatus("0");
		pAccount.setSubmitid(submitid);
		pAccount.setSubmitdate(new Date());
		processAccountMapper.updateByPrimaryKey(pAccount);
		processBindMapper.deleteByAccountId(accountid);
		for(int i=0;i<processBindList.size();i++)
		{
			ProcessBind bind=processBindList.get(i);
			if(processBindMapper.insert(bind)==0)
			{
				return 0;
			}
		}
		return 1;
	}
}
