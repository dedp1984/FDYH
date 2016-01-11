package com.nantian.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nantian.dao.SysMenuMapper;
import com.nantian.domain.SysMenu;

@Service
@Transactional
public class SysMenuService 
{
	@Resource
	private SysMenuMapper sysMenuDao;
	
	public List<SysMenu> querySubMenuListByAccountIdAndParentMenuId(String accountid,String parentmenuid) {
		// TODO Auto-generated method stub
		Map<String,String> map=new HashMap<String,String>();
		map.put("accountid", accountid);
		map.put("parentmenuid",parentmenuid);
		return sysMenuDao.selectSubMenuListByAccountIdAndParentMenuId(map);
	}
}
