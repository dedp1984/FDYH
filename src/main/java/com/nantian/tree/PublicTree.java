package com.nantian.tree;

import java.util.ArrayList;
import java.util.List;

public class PublicTree
{
	public  String name;
	public  String depositbal;
	public  String depositbaldate;
	public  String depositavg;
	public  String depositavgdate;
	public  String creditbal;
	public  String creditdate;
	public  String creditavg;
	public  List<PublicTree> children=new ArrayList<PublicTree>();
	public  boolean leaf;
	public  boolean expanded=true;
}
