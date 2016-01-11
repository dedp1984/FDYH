package com.nantian.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nantian.dao.BaseAccountMapper;
import com.nantian.dao.BindAccountToManagerMapper;
import com.nantian.dao.PersonPledgeAccountMapper;
import com.nantian.domain.BaseAccount;
import com.nantian.domain.BindAccountToManager;
import com.nantian.domain.PersonPledgeAccount;
@Service
@Transactional
public class PledgeService
{
	@Resource
	private PersonPledgeAccountMapper personPledgeAccountMapper;
	@Resource
	private BaseAccountMapper baseAccountMapper;
	@Resource
	private BindAccountToManagerMapper atmMapper;
	
	public List<PersonPledgeAccount> queryPledgeList(String accountid,String accountname,String toBranchId,String toManagerId,String modifyBranch,String modifyUser)
	{
		return personPledgeAccountMapper.selectList(accountid, accountname, toBranchId, toManagerId, modifyBranch, modifyUser);
	}
	public void addPersonPledgeDetail(BaseAccount baseAccount, PersonPledgeAccount detail,ArrayList<BindAccountToManager> bindList)
	{
		// 有对应账号信息则只需要
		BaseAccount tmpBaseAccount=baseAccountMapper.selectByPrimaryKey(baseAccount.getAccountid());
		if (tmpBaseAccount != null)
		{
			tmpBaseAccount.setSubaccttype("1");
			baseAccountMapper.updateByPrimaryKey(tmpBaseAccount);
			personPledgeAccountMapper.insert(detail);
		}
		else
		{
			baseAccountMapper.insert(baseAccount);
			personPledgeAccountMapper.insert(detail) ;
			for (int i = 0; i < bindList.size(); i++)
			{
				BindAccountToManager bind = bindList.get(i);
				atmMapper.insert(bind);
			}
		}
	}
	
	public void updatePersonPledgeDetail(BaseAccount baseAccount,PersonPledgeAccount detail,List<BindAccountToManager> bindList)
	{
		personPledgeAccountMapper.updateByPrimaryKey(detail);
	}
	
	public void deletePersonPledgeDetail(String saleid,String accountid)
	{
		personPledgeAccountMapper.deleteByPrimaryKey(saleid);
	}
}
