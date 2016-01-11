package com.nantian.tree;

import java.util.ArrayList;
import java.util.List;

public class FinanceTree
{
	public  String name;
	public  String cntamt;
	public  String totalamt;
	public  String totaltimebal;
	public  String totalavg;
	public  String avgenddate;
	public  String id;
	public  List<FinanceTree> children=new ArrayList<FinanceTree>();
	public  boolean leaf;
	public  String nodetype;
	public  boolean expanded=true;
}
