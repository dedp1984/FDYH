package com.nantian.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nantian.dao.BaseAccountMapper;
import com.nantian.dao.BindAccountToManagerMapper;
import com.nantian.dao.SysAccountMapper;
import com.nantian.domain.BaseAccount;
import com.nantian.domain.BindAccountToManager;
import com.nantian.domain.BindAccountToManagerKey;
import com.nantian.domain.ProcessAccount;
import com.nantian.domain.ProcessBind;
import com.nantian.domain.SysAccount;

@Service
@Transactional
public class BaseAccountService {

	@Resource
	private BaseAccountMapper baseAccountMapper;
	
	@Resource
	private BindAccountToManagerMapper atmMapper;
	@Resource
	private SysAccountMapper sysAccountMapper;
	
	public List<BaseAccount> queryBaseAccountList(String accountid,String accountname,String accounttype,String branchid,String managerid,ArrayList<String> accountTypeList,String subaccttype,String financeCreateId)
	{
		
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("accountid", accountid);
		map.put("accountname", accountname);
		map.put("accounttype", accounttype);
		map.put("branchid", branchid);
		map.put("managerid", managerid);
		map.put("financecreateid", financeCreateId);
		map.put("list", accountTypeList);
		return baseAccountMapper.selectBaseAccountList(map);
	}
	
	
	/**
	 * 修改账户与客户经理关联关系
	 * **/
	
	public int updateBindAccountToManager(String branchid,String accountid,String accountname,String accounttype,List<BindAccountToManager> bindList)
	{
		BaseAccount baseAccount=new BaseAccount();
		baseAccount.setAccountid(accountid);
		baseAccount.setBranchid(branchid);
		baseAccount.setAccountname(accountname);
		baseAccount.setAccounttype(accounttype);
		if(baseAccountMapper.updateByPrimaryKeySelective(baseAccount)!=0)
		{
			if(atmMapper.deleteByAccountId(accountid)!=0)
			{
				for(int i=0;i<bindList.size();i++)
				{
					BindAccountToManager bind=bindList.get(i);
					if(atmMapper.insert(bind)==0)
					{
						return 0;
					}
				}
			}
			return 1;
		}
		else
		{
			return 0;
		}
		
	}
	
	/**
	 *  增加账户与客户经理关联关系
	 * **/
	
	public int addBindAccountToManager(String branchid,String accountid,String accountname,String accounttype,List<BindAccountToManager> bindList)
	{
		BaseAccount baseAccount=new BaseAccount();
		baseAccount.setAccountid(accountid);
		baseAccount.setBranchid(branchid);
		baseAccount.setAccountname(accountname);
		baseAccount.setAccounttype(accounttype);
		if(baseAccountMapper.insert(baseAccount)!=0)
		{
			for(int i=0;i<bindList.size();i++)
			{
				BindAccountToManager bind=bindList.get(i);
				if(atmMapper.insert(bind)==0)
				{
					return 0;
				}
			}
			return 1;
		}
		else
		{
			return 0;
		}
		
	}
	
	/**
	 * 删除账户与客户经理关联关系
	 * **/
	public int deleteBindAccountToManager(String accountid)
	{
		if(baseAccountMapper.deleteByPrimaryKey(accountid)!=0)
		{
			if(atmMapper.deleteByAccountId(accountid)!=0)
			{
				return 1;
			}
		}
		return 0;
	}
	public BaseAccount queryBaseAccountByAccountId(String accountid)
	{
		return baseAccountMapper.selectByPrimaryKey(accountid);
	}
	
	public int transManagerBinds(String transBinds,String changeBranchId,String managerid)
	{
		if(transBinds!=null&&!transBinds.equals(""))
		{
			String[] newBinds=transBinds.split("\\|");
			for(int i=0;i<newBinds.length;i++)
			{
				String[] record=newBinds[i].split("\\+");
				String accountid=record[0];
				String oldManagerid=record[1];
				String newManagerid=record[2];
				atmMapper.updateNewManagerByPrimaryKey(accountid, oldManagerid, newManagerid);
			}
		}
		if(changeBranchId!=null)
		{
			SysAccount sysAccount=sysAccountMapper.selectByPrimaryKey(managerid);
			if(sysAccount!=null)
			{
				sysAccount.setBranchid(changeBranchId);
				sysAccountMapper.updateByPrimaryKey(sysAccount);
			}
		}
		return 1;
	}
	
}
