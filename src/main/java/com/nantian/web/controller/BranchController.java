package com.nantian.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nantian.custom.FormPostResult;
import com.nantian.custom.Utils;
import com.nantian.tree.TreeNode;
import com.nantian.domain.SysAccount;
import com.nantian.domain.SysBranch;
import com.nantian.service.SysBranchService;

@Controller
public class BranchController {
	
	@Resource
	private SysBranchService sysBranchService;
	
	@RequestMapping("/branch/queryBranchTreeByAccountId")
	@ResponseBody
	public List<TreeNode> queryBranchTreeByAccountId(HttpSession session)
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(sysAccount.getProperty().equals("1")||sysAccount.getProperty().equals("6"))
		{
			SysBranch tmp=sysBranchService.querySubBranchList("0").get(0);
			return sysBranchService.queryAllSubBranchTree(tmp.getBranchid());
		}
		else
		{
			return  sysBranchService.queryAllSubBranchTree(sysAccount.getBranchid());
		}
		
	}
	
	@RequestMapping("/branch/queryBranchTree")
	@ResponseBody
	public List<TreeNode> queryBranchTree(HttpSession session)
	{
		SysBranch tmp=sysBranchService.querySubBranchList("0").get(0);
		return sysBranchService.queryAllSubBranchTree(tmp.getBranchid());
		
	}
	@RequestMapping("/branch/addBranch")
	@ResponseBody
	public FormPostResult addBranch(SysBranch sysBranch)
	{
		//sysBranch.setBranchid(Utils.get16UUID());
		if(sysBranchService.addBranch(sysBranch)==0)
		{
			return new FormPostResult(false);
		}
		else
		{
			return new FormPostResult(true);
		}
	}
	
	@RequestMapping("/branch/modifyBranch")
	@ResponseBody
	public FormPostResult modifyBranch(SysBranch sysBranch)
	{

		if(sysBranchService.modifyBranch(sysBranch)==0)
		{
			return new FormPostResult(false);
		}
		else
		{
			return new FormPostResult(true);
		}
	}
	@RequestMapping("/branch/deleteBranch")
	@ResponseBody
	public FormPostResult deleteBranch(@RequestParam(value="id") String branchid)
	{
		if(sysBranchService.deleteBranch(branchid)==0)
		{
			return new FormPostResult(false);
		}
		else
		{
			return new FormPostResult(true);
		}
	}
	
	

}
