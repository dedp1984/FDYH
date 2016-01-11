package com.nantian.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nantian.service.SysRoleService;
import com.nantian.tree.CheckBoxTreeNode;
import com.nantian.custom.FormPostResult;
import com.nantian.domain.SysRole;
import com.nantian.domain.SysRoleMenuKey;

@Controller
public class RoleController 
{
	@Resource
	private SysRoleService sysRoleService;
	
	@RequestMapping("/role/queryRoleListByRoleName")
	@ResponseBody
	public List<SysRole> queryRoleListByRoleName(String rolename)
	{
		return sysRoleService.queryRoleListByRoleName(rolename);
	}
	@RequestMapping("/role/queryMenuTreeByRoleId" )
	@ResponseBody
	public List<CheckBoxTreeNode> queryMenuTreeByRoleId(@RequestParam(value="id")String roleid)
	{
		System.out.println(roleid);
		return sysRoleService.queryMenuTreeByRoleId(roleid);
	}
	@RequestMapping(value="/role/saveMenuTreeByRoleId" ,method = RequestMethod.POST )
	@ResponseBody
	public FormPostResult saveRoleMenuList(String roleid,String menulist)
	{
		List<SysRoleMenuKey> list=new ArrayList<SysRoleMenuKey>();
		
		String[] arrMenuId=menulist.split(":");
		
		if(menulist.length()>0)
		{
			for(int i=0;i<arrMenuId.length;i++)
			{
				SysRoleMenuKey roleMenuKey=new SysRoleMenuKey();
				roleMenuKey.setMenuid(arrMenuId[i]);
				roleMenuKey.setRoleid(roleid);
				list.add(roleMenuKey);
			}
		}
		
		sysRoleService.modifyRoleAuthMenu(roleid, list);
		return new FormPostResult(true);
	}
	
	@RequestMapping("/role/addRole")
	@ResponseBody
	public FormPostResult addRole(SysRole role)
	{
		if(sysRoleService.addRole(role)!=0)
		{
			FormPostResult result=new FormPostResult(true);
			result.addErros("msg", "添加角色成功");
			return result;
		}
		else
		{
			return new FormPostResult(false);
		}
	}
	@RequestMapping("/role/deleteRole")
	@ResponseBody
	public FormPostResult deleteRole(String roleid)
	{
		if(sysRoleService.deleteRole(roleid)!=0)
		{
			return new FormPostResult(true);
		}
		else
		{
			return new FormPostResult(false);
		}
	}
	@RequestMapping("/role/modifyRole")
	@ResponseBody
	public FormPostResult modifyRole(SysRole role)
	{
		if(sysRoleService.modifyRole(role)!=0)
		{
			FormPostResult result=new FormPostResult(true);
			result.addErros("msg", "修改角色成功");
			return result;
		}
		else
		{
			return new FormPostResult(false);
		}
	}
}

