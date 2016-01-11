package com.nantian.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.ss.formula.functions.Finance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nantian.custom.Utils;
import com.nantian.dao.BaseAccountMapper;
import com.nantian.dao.BindAccountToManagerMapper;
import com.nantian.dao.FinanceDetailMapper;
import com.nantian.dao.FinanceProductTypeMapper;
import com.nantian.domain.BaseAccount;
import com.nantian.domain.BindAccountToManager;
import com.nantian.domain.FinanceDetail;
import com.nantian.domain.FinanceProductType;

@Service
@Transactional
public class FinanceProductService
{
	@Resource
	private FinanceProductTypeMapper financeProductTypeMapper;
	@Resource
	private BaseAccountMapper baseAccountMapper;
	@Resource
	private BindAccountToManagerMapper atmMapper;
	@Resource
	private FinanceDetailMapper financeDetailMapper;
	
	public List<FinanceProductType>  queryFinanceProductTypeListByName(String name,String type)
	{
		HashMap<String,String> map=new HashMap<String,String>();
		map.put("name", name);
		map.put("type", type);
		List<FinanceProductType> tmp=financeProductTypeMapper.selectListByName(map);
		for(int i=0;i<tmp.size();i++)
		{
			FinanceProductType fpt=tmp.get(i);
			if(fpt.getType().equals("2"))
			{
				fpt.setComment("个人-"+fpt.getName());
			}
			else
			{
				fpt.setComment("对公-"+fpt.getName());
			}
			tmp.set(i, fpt);
		}
		return  tmp;
	}
	
	public int addFinanceProductType(String type,String name ,Boolean isjsrj,Boolean isjsckye)
	{
		FinanceProductType record=new FinanceProductType();
		record.setType(type);
		record.setName(name);
		record.setIsjsrj(isjsrj);
		record.setIsjsckye(isjsckye);
		record.setId(Utils.get16UUID());
		return financeProductTypeMapper.insert(record);
	}
	
	public int delFinanceProductType(String id)
	{
		return financeProductTypeMapper.deleteByPrimaryKey(id);
	}
	
	public int updateFinanceProductType(String id,String type,String name,Boolean isjsrj,Boolean isjsckye)
	{
		FinanceProductType record=new FinanceProductType();
		record.setType(type);
		record.setName(name);
		record.setIsjsrj(isjsrj);
		record.setIsjsckye(isjsckye);
		record.setId(id);
		return financeProductTypeMapper.updateByPrimaryKey(record);
	}
	
	public int addFinanceDetail(BaseAccount baseAccount,FinanceDetail detail,List<BindAccountToManager> bindList)
	{
		//有对应账号信息则只需要
		if(baseAccountMapper.selectByPrimaryKey(baseAccount.getAccountid())!=null)
		{
			if (financeDetailMapper.insert(detail) != 0)
			{
				return 1;
			}
		}
		else
		{
			if (baseAccountMapper.insert(baseAccount) != 0)
			{
				if (financeDetailMapper.insert(detail) != 0)
				{
					for (int i = 0; i < bindList.size(); i++)
					{
						BindAccountToManager bind = bindList.get(i);
						if (atmMapper.insert(bind) == 0)
						{
							return 0;
						}
					}
					return 1;
				}
			}
		}
		return 0;
	}
	
	public int updateFinanceDetail(BaseAccount baseAccount,FinanceDetail detail,List<BindAccountToManager> bindList)
	{
		String accountid=baseAccount.getAccountid();
		if(baseAccountMapper.updateByPrimaryKey(baseAccount)!=0)
		{
			if(financeDetailMapper.updateByPrimaryKey(detail)!=0)
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
					return 1;
				}
			}
		}
		return 0;
	}
	
	public int deleteFinanceDetail(String saleid,String accountid)
	{
		return financeDetailMapper.deleteByPrimaryKey(saleid);
	}
}
