package com.nantian.tree;

import java.util.ArrayList;
import java.util.List;

public class PublicDepositTree
{
	public  String name;
	public  String dqye;
	public  String nioje;
	public  String enddate;
	public  List<PublicDepositTree> children=new ArrayList<PublicDepositTree>();
	public  boolean leaf=false;
	public  boolean expanded=true;
}
