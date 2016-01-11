package com.nantian.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nantian.tree.TreeNode;
import com.nantian.domain.SysAccount;
import com.nantian.domain.SysMenu;
import com.nantian.service.SysMenuService;

@Controller
public class MenuController 
{
	@Resource
	private SysMenuService sysMenuService;
	
	@RequestMapping("/menu/querySubMenuByAccountId")
	@ResponseBody
	public List<TreeNode> querySubMenuByAccountId(@RequestParam(value="id")String parentid,HttpSession httpSession)
	{
		SysAccount account=(SysAccount)httpSession.getAttribute("user");
		
		return generateMenuTreeByAccountIdAndParentMenuId(account.getAccountid(),parentid,true);
	}
	
	@RequestMapping("/menu/queryTopMenuByAccountId")
	@ResponseBody
	public List<TreeNode> queryTopMenuByAccountId(@RequestParam(value="id")String parentid,HttpSession httpSession)
	{
		SysAccount account=(SysAccount)httpSession.getAttribute("user");
		
		return generateMenuTreeByAccountIdAndParentMenuId(account.getAccountid(),parentid,false);
	}
	
	private List<TreeNode> generateMenuTreeByAccountIdAndParentMenuId(String accountid,String parentmenuid,boolean recursive)
	{
		System.out.println("accountid="+accountid+" "+"parentmenuid="+parentmenuid);
		List<SysMenu> menuList= sysMenuService.querySubMenuListByAccountIdAndParentMenuId(accountid, parentmenuid);
		List<TreeNode> treeNodeList=new ArrayList<TreeNode>();
		for(int i=0;i<menuList.size();i++)
		{
			TreeNode treeNode=new TreeNode();		
			SysMenu menu=menuList.get(i);	
			treeNode.id=menu.getMenuid();
			treeNode.text=menu.getMenuname();	
			if(recursive)
			{
				treeNode.expanded=false;
				treeNode.url=menu.getPageurl();
				List<TreeNode> subTreeNodeList=new ArrayList<TreeNode>();
				subTreeNodeList=generateMenuTreeByAccountIdAndParentMenuId(accountid,treeNode.id,recursive);
				if(subTreeNodeList.size()==0)
				{
					treeNode.leaf=true;
				}
				else
				{
					treeNode.leaf=false;
					treeNode.children=subTreeNodeList;
				}
			}
			else
			{
				treeNode.leaf=false;
			}
			
			treeNodeList.add(treeNode);
		}
		return treeNodeList;
	}

}
