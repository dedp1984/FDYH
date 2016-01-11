package com.nantian.tree;

import java.util.ArrayList;
import java.util.List;

public class AreaTreeNode extends TreeNode {
	
	public String areaId;
	public String areaName;
	public String entryId;
	public String entryName;
	public String entryType;
	public String devId;
	public String devName;
	public String nodeType;
	public String floorCnt;
	public String floorRoomCnt;
	public String adrZoneId;
	public String iosZoneId;
	public String devAddress;
	public String icon;
	public  List<AreaTreeNode> children=new ArrayList<AreaTreeNode>();

}
