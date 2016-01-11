package com.nantian.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nantian.dao.BindAccountToManagerMapper;
import com.nantian.dao.SysAccountFeatureMapper;
import com.nantian.dao.SysAccountMapper;
import com.nantian.dao.SysAccountRoleMapper;
import com.nantian.domain.SysAccount;
import com.nantian.domain.SysAccountFeatureKey;
import com.nantian.domain.SysAccountRoleKey;

@Service
@Transactional(rollbackFor=Exception.class)
public class SysAccountService 
{
	@Resource
	private SysAccountMapper sysAccountDao;
	@Resource
	private SysAccountRoleMapper sysAccountRoleDao;
	@Resource
	private SysAccountFeatureMapper sysAccountFeatureDao;
	@Resource
	private BindAccountToManagerMapper atmDao;
	
	public SysAccount querySysAccountByAccountId(String accountid)
	{
		return sysAccountDao.selectByPrimaryKey(accountid);
	}
	
	public List<SysAccount> querySysAccountList(String accountid,String accountname,String branchid,ArrayList<String> propertyList)
	{
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("accountid", accountid);
		map.put("accountname", accountname);
		map.put("branchid", branchid);
		map.put("propertyList", propertyList);
		List<SysAccount> list= sysAccountDao.selectAccountList(map);
		for(int i=0;i<list.size();i++)
		{
			List<SysAccountRoleKey> list1=list.get(i).getRoles();
			for(int j=0;j<list1.size();j++)
			{
				SysAccountRoleKey accountRole=list1.get(j);
				System.out.println("account roleid="+accountRole.getRoleid());
			}
			
		}
		return list;
	}
	
	public int addSysAccount(SysAccount sysAccount,String rolelist)
	{
		sysAccount.setPassword("111111");
		sysAccountDao.insert(sysAccount);
		List<SysAccountFeatureKey> busiList=sysAccount.busiFeature;
		for(int j=0;j<busiList.size();j++)
		{
			SysAccountFeatureKey feature=busiList.get(j);
			sysAccountFeatureDao.insert(feature);
		}
		if(rolelist.length()!=0)
		{
			String[] arrRole=rolelist.split(",");
			for(int i=0;i<arrRole.length;i++)
			{
				SysAccountRoleKey sysAccountRoleKey=new SysAccountRoleKey();
				sysAccountRoleKey.setAccountid(sysAccount.getAccountid());
				sysAccountRoleKey.setRoleid(arrRole[i]);
				if(sysAccountRoleDao.insert(sysAccountRoleKey)==0)
				{
					return 0;
				}
			}

		}
		return 1;
	}
	
	public int modifySysAccount(SysAccount sysAccount,String rolelist)
	{
		sysAccountRoleDao.deleteByAccountid(sysAccount.getAccountid());
		if(sysAccountDao.updateByPrimaryKey(sysAccount)==0)
		{
			return 0;
		}
		SysAccountFeatureKey key=new SysAccountFeatureKey();
		key.setAccountid(sysAccount.getAccountid());
		key.setType("1");
		sysAccountFeatureDao.deleteBySelective(key);
		List<SysAccountFeatureKey> busiList=sysAccount.busiFeature;
		for(int j=0;j<busiList.size();j++)
		{
			SysAccountFeatureKey feature=busiList.get(j);
			sysAccountFeatureDao.insert(feature);
		}
		if(rolelist.length()!=0)
		{
			String[] arrRole=rolelist.split(",");
			for(int i=0;i<arrRole.length;i++)
			{
				SysAccountRoleKey sysAccountRoleKey=new SysAccountRoleKey();
				sysAccountRoleKey.setAccountid(sysAccount.getAccountid());
				sysAccountRoleKey.setRoleid(arrRole[i]);
				if(sysAccountRoleDao.insert(sysAccountRoleKey)==0)
				{
					return 0;
				}
			}

		}
		return 1;
	}
	
	public int modifySysAccount(SysAccount sysAccount)
	{
		if(sysAccountDao.updateByPrimaryKey(sysAccount)==0)
		{
			return 0;
		}
		return 1;
	}
	
	public void deleteSysAccount(String accountid) throws Exception
	{
		if(atmDao.selectCntByManagerId(accountid)==0)
		{
			sysAccountRoleDao.deleteByAccountid(accountid);
			SysAccountFeatureKey key=new SysAccountFeatureKey();
			key.setAccountid(accountid);
			sysAccountFeatureDao.deleteBySelective(key);
			sysAccountDao.deleteByPrimaryKey(accountid);
		}
		else
		{
			throw new Exception("该账户存在分配的客户关系，无法删除");
		}
		
	}
	
	public List<SysAccount> querySysAccountListByBranchId(String branchid)
	{
		return sysAccountDao.selectAccountListByBranchId(branchid);
	}
	
	public List<SysAccountFeatureKey> querySysAccountFeature(String accountid,String type,String value)
	{
		SysAccountFeatureKey key =new SysAccountFeatureKey();
		key.setAccountid(accountid);
		key.setType(type);
		key.setValue(value);
		return sysAccountFeatureDao.selectBySelective(key);
	}
	
	public void batchModifySysAccountBranch(String oldBranchId,String newBranchId,String modifyUsers)
	{
		String[] users=modifyUsers.split("\\,");
		for(int i=0;i<users.length;i++)
		{
			String accountid=users[i];
			SysAccount sysAccount=sysAccountDao.selectByPrimaryKey(accountid);
			if(sysAccount!=null)
			{
				sysAccount.setBranchid(newBranchId);
				sysAccountDao.updateByPrimaryKey(sysAccount);
			}
		}
	}
}
