package com.nantian.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode 
{
	public  String id;
	public  String text;
	public  boolean leaf;
	public  String url;
	public  boolean expanded=true;
	public  String  parentid;
	public  String  parentText;
	public  List<TreeNode> children=new ArrayList<TreeNode>();
}
