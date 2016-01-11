package com.nantian.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nantian.dao.SysMenuMapper;
import com.nantian.dao.SysRoleMapper;
import com.nantian.dao.SysRoleMenuMapper;
import com.nantian.domain.SysMenu;
import com.nantian.domain.SysRole;
import com.nantian.domain.SysRoleMenuKey;
import com.nantian.tree.CheckBoxTreeNode;

@Service
@Transactional
public class SysRoleService 
{
	@Resource
	private SysMenuMapper sysMenuDao;
	
	@Resource
	private SysRoleMapper sysRoleDao;
	
	@Resource
	private SysRoleMenuMapper sysRoleMenuDao;
	
	public List<SysRole> queryRoleListByRoleName(String rolename)
	{
		SysRole record=new SysRole();
		record.setRolename(rolename);
		return sysRoleDao.selectRoleList(record);
	}
	
	public List<CheckBoxTreeNode> queryMenuTreeByRoleId(String roleid)
	{
		return generateMenuTreeByRoleId(roleid,"0");
	}
	
	public List<CheckBoxTreeNode> generateMenuTreeByRoleId(String roleid,String parentmenuid)
	{
		List<CheckBoxTreeNode> treeNodeList=new ArrayList<CheckBoxTreeNode>();
		
		List<SysMenu> menuList=sysMenuDao.selectSubMenuListByParentMenuId(parentmenuid);
		for(int i=0;i<menuList.size();i++)
		{
			SysMenu menu=menuList.get(i);
			CheckBoxTreeNode treeNode=new CheckBoxTreeNode();
			treeNode.id=menu.getMenuid();
			treeNode.text=menu.getMenuname();
			treeNode.expanded=true;
			//判断是否属于此角色菜单
			SysRoleMenuKey rolemenu=new SysRoleMenuKey();
			rolemenu.setRoleid(roleid);
			rolemenu.setMenuid(menu.getMenuid());
			if(sysRoleMenuDao.selectByPrimaryKey(rolemenu)!=null)
			{
				treeNode.checked=true;
			}
			else
			{
				treeNode.checked=false;
			}
			//判断此节点是否有子节点
			List<CheckBoxTreeNode> subTreeNodeList=generateMenuTreeByRoleId(roleid,treeNode.id);
			if(subTreeNodeList.size()==0)
			{
				treeNode.leaf=true;
			}
			else
			{
				treeNode.leaf=false;
				treeNode.children=subTreeNodeList;
			}
			treeNodeList.add(treeNode);
		}
		return treeNodeList;
		
	}
	
	public int addRole(SysRole role)
	{
		return sysRoleDao.insert(role);
	}
	
	public int modifyRole(SysRole role)
	{
		return sysRoleDao.updateByPrimaryKey(role);
	}
	public int deleteRole(String roleid)
	{
		sysRoleMenuDao.deleteByRoleId(roleid);
		return sysRoleDao.deleteByPrimaryKey(roleid);
	}
	
	public boolean modifyRoleAuthMenu(String roleid,List<SysRoleMenuKey> list)
	{
		sysRoleMenuDao.deleteByRoleId(roleid);
		System.out.println(list.size());
		for(int i=0;i<list.size();i++)
		{
			sysRoleMenuDao.insert(list.get(i));
		}
		return true;
	}
	
}
