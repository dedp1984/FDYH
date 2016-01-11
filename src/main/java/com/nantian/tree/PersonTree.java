package com.nantian.tree;

import java.util.ArrayList;
import java.util.List;

public class PersonTree
{
	public  String name;
	public  String bal;
	public  String balenddate;
	public  String avg;
	public  String avgenddate;
	public  String timebal;
	public  String timebalenddate;
	public  String pledgeavg;
	public  String pledgeavgenddate;
	public  String avgfixed;
	public  String avgcurrent;
	public  String avgfinance;
	public  String balfinance;
	public  String balpledge;
	public  String balfixed;
	public  String balcurrent;
	public  List<PersonTree> children=new ArrayList<PersonTree>();
	public  boolean leaf;
	public  boolean expanded=true;
}
