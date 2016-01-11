package com.nantian.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nantian.dao.SysBranchMapper;
import com.nantian.domain.SysBranch;
import com.nantian.tree.TreeNode;

@Service
@Transactional
public class SysBranchService 
{
	@Resource 
	SysBranchMapper sysBranchDao;
	public List<SysBranch> queryAllSubBranchList(String parentid,boolean includeSelf)
	{
		List<SysBranch> list=recursiveQueryAllSubBranchList(parentid);
		if(includeSelf)
		{
			list.add(sysBranchDao.selectByPrimaryKey(parentid));
		}
		return list;
	}
	
	private List<SysBranch> recursiveQueryAllSubBranchList(String parentid)
	{
		List<SysBranch> list=querySubBranchList(parentid);
		for(int i=0;i<list.size();i++)
		{
			SysBranch sysBranch=(SysBranch)list.get(i);
			List<SysBranch> tmpList=querySubBranchList(sysBranch.getBranchid());
			if(tmpList.size()>0)
			{
				tmpList=recursiveQueryAllSubBranchList(sysBranch.getBranchid());
				list.addAll(tmpList);
			}
		}
		return list;
	}
	public List<TreeNode> queryAllSubBranchTree(String parentid)
	{
		SysBranch sysBranch=sysBranchDao.selectByPrimaryKey(parentid);
		List<TreeNode> listTreeNode=new ArrayList<TreeNode>();
		List<TreeNode> subListTreeNode=generateBranchTree(parentid,sysBranch.getBranchname());
		TreeNode parentNode=new TreeNode();
		parentNode.id=parentid;
		parentNode.text=sysBranch.getBranchname();
		parentNode.parentid="0";
		if(subListTreeNode.size()==0)
		{
			parentNode.leaf=true;
		}
		else
		{
			parentNode.leaf=false;
			parentNode.children=subListTreeNode;
		}
		listTreeNode.add(parentNode);
		return listTreeNode;
	}
	private List<TreeNode> generateBranchTree(String parentid,String parenttext)
	{
		List<SysBranch> branchList=querySubBranchList(parentid);
		
		List<TreeNode> treeList=new ArrayList<TreeNode>();
		
		for(int i=0;i<branchList.size();i++)
		{
			TreeNode treeNode=new TreeNode();
			SysBranch branch=branchList.get(i);
			
			treeNode.id=branch.getBranchid();
			treeNode.text=branch.getBranchname();
			treeNode.expanded=true;
			treeNode.parentid=parentid;
			treeNode.parentText=parenttext;
			List<TreeNode> subTreeList=generateBranchTree(treeNode.id,treeNode.text);
			if(subTreeList.size()==0)
			{
				treeNode.leaf=true;
			}
			else
			{
				treeNode.leaf=false;
				treeNode.children=subTreeList;
			}
			treeList.add(treeNode);
		}
		return treeList;
		
	}
	public List<SysBranch> querySubBranchList(String parentid)
	{
		return sysBranchDao.selectSubBranchList(parentid);
	}
	
	public int addBranch(SysBranch sysBranch)
	{
		return sysBranchDao.insert(sysBranch);
	}
	public int deleteBranch(String branchid)
	{
		return sysBranchDao.deleteByPrimaryKey(branchid);
	}
	public int modifyBranch(SysBranch sysBranch)
	{
		return sysBranchDao.updateByPrimaryKey(sysBranch);
	}
	
	public SysBranch queryBranchDetail(String branchId)
	{
		return sysBranchDao.selectByPrimaryKey(branchId);
	}
}
