package com.nantian.web.controller;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nantian.custom.AccountType;
import com.nantian.custom.BusiType;
import com.nantian.custom.ExcelType;
import com.nantian.custom.Result;
import com.nantian.custom.Utils;
import com.nantian.dao.SysAccountMapper;
import com.nantian.domain.FinanceProductType;
import com.nantian.domain.SysAccount;
import com.nantian.domain.SysAccountFeatureKey;
import com.nantian.domain.SysBranch;
import com.nantian.service.QueryService;
import com.nantian.service.SysAccountService;
import com.nantian.service.SysBranchService;
import com.nantian.tree.FinanceTree;
import com.nantian.tree.PersonTree;
import com.nantian.tree.PublicDepositTree;
import com.nantian.tree.PublicTree;
import com.nantian.tree.TreeNode;

@Controller
public class QueryController
{

	@Resource
	private QueryService queryService;
	@Resource
	private SysBranchService sysBranchService;
	@Resource
	private SysAccountService sysAccountService;
	
	private boolean isPersonManager(List<SysAccountFeatureKey> busiFeatures)
	{
		if(busiFeatures!=null)
		{
			for(int i=0;i<busiFeatures.size();i++)
			{
				SysAccountFeatureKey feature=busiFeatures.get(i);
				if(feature.getValue().equals("2"))
				{
					return true;
				}
			}
		}
		return false;
	}
	private boolean isPublicManager(List<SysAccountFeatureKey> busiFeatures)
	{
		if(busiFeatures!=null)
		{
			for(int i=0;i<busiFeatures.size();i++)
			{
				SysAccountFeatureKey feature=busiFeatures.get(i);
				if(feature.getValue().equals("1"))
				{
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 功能：统计各支行、支行下属客户经理个人业务年日均余额、时点余额信息、理财年日均、理财余额、质押存款年日均、质押存款时点余额
	 * **/
	@RequestMapping("query/QueryPersonBusinessData")
	@ResponseBody
	public List<PersonTree> QueryPersonBusinessData(String enddate,String branchid,String managerid,HttpSession session) throws ParseException
	{
		DecimalFormat df=new DecimalFormat("#,###,###,###,###.##");
		List<PersonTree>  totalList=new ArrayList<PersonTree>();
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==true)
			{
				SysBranch tmp=sysBranchService.querySubBranchList("0").get(0);
				branchid=tmp.getBranchid();
			}
			else
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		List<SysBranch> sysBranchList=sysBranchService.queryAllSubBranchList(branchid,true);
		List<PersonTree> listBranch=new ArrayList<PersonTree>();
		PersonTree rootNode=new PersonTree();
		double totalBal=0.00;
		double totalTimeBal=0.00;
		double totalYearAvg=0.00;
		double totalPledgeYearAvg=0.00;
		double totalFixedYearAvg=0.00;
		double totalCurrentYearAvg=0.00;
		double totalFinanceYearAvg=0.00;
		double totalFinanceBal=0.00;
		double totalPledgeBal=0.00;
		double totalFixedBal=0.00;
		double totalCurrentBal=0.00;
		for(int i=0;i<sysBranchList.size();i++)
		{
			double branchBal=0.00;
			double branchTimeBal=0.00;
			double branchYearAvg=0.00;
			double branchPledgeYearAvg=0.00;
			double branchFixedYearAvg=0.00;
			double branchCurrentYearAvg=0.00;
			double branchFinanceYearAvg=0.00;
			double branchFinanceBal=0.00;
			double branchPledgeBal=0.00;
			double branchFixedBal=0.00;
			double branchCurrentBal=0.00;
			boolean balFlag=true;
			boolean avgFlag=true;
			SysBranch branch=sysBranchList.get(i);
			if(branch.getBranchid().equals("0000"))
				continue;
			ArrayList<String> propertyList=new ArrayList<String>();
			propertyList.add("1");
			propertyList.add("2");
			propertyList.add("3");
			propertyList.add("5");
			propertyList.add("6");
			List<SysAccount> sysAccountList=sysAccountService.querySysAccountList(managerid, "", branch.getBranchid(), propertyList);
			
			PersonTree branchTreeNode=new PersonTree();
			branchTreeNode.name=branch.getBranchname();
			
			Date nearFixedBalDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(),ExcelType.PERSON_FIXED_ACCOUNT);
			Date nearCurrentBalDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(), ExcelType.PERSON_CURRENT_ACCOUNT);
			Date nearBalDate =Utils.str82date(enddate);
			if(nearFixedBalDate==null&&nearCurrentBalDate==null)
			{
				balFlag=false;
			}
			else if(nearFixedBalDate==null||nearCurrentBalDate==null)
			{
				if(nearFixedBalDate==null)
					nearBalDate=nearCurrentBalDate;
				else
					nearBalDate=nearFixedBalDate;
			}
			else
			{
				nearBalDate=nearFixedBalDate.before(nearCurrentBalDate)?nearCurrentBalDate:nearFixedBalDate;
			}

			Date nearFixedAvgDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(),ExcelType.PERSON_FIXED_DAYAVG);
			Date nearCurrentAvgDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(), ExcelType.PERSON_CURRENT_DAYAVG);
			Date nearAvgDate=Utils.str82date(enddate);
			if(nearFixedAvgDate==null&&nearCurrentAvgDate==null)
			{
				avgFlag=false;
			}
			else if(nearFixedAvgDate==null||nearCurrentAvgDate==null)
			{
				if(nearFixedAvgDate==null)
					nearAvgDate=nearCurrentAvgDate;				
				else
					nearAvgDate=nearFixedAvgDate;
			}
			else
			{
				nearAvgDate=nearFixedAvgDate.before(nearCurrentAvgDate)?nearCurrentAvgDate:nearFixedAvgDate;
			}
						
			List<PersonTree> listManager=new ArrayList<PersonTree>();
			for(int j=0;j<sysAccountList.size();j++)
			{
				double managerTimeBal=0.00;
				double managerYearAvg=0.00;
				double managerPledgeYearAvg=0.00;
				double managerFixedYearAvg=0.00;
				double managerCurrentYearAvg=0.00;
				double managerFinanceYearAvg=0.00;
				double managerFixedBal=0.00;
				double managerCurrentBal=0.00;
				double managerFinanceBal=0.00;
				double managerPledgeBal=0.00;
				PersonTree managerTree=new PersonTree();				
				SysAccount sysAcc=sysAccountList.get(j);
				/*
				if(isPersonManager(sysAcc.getBusiFeature())==false)
				{
					continue;
				}*/
				managerTree.name=sysAcc.getAccountname();
				
				//查询客户经理时点余额
				if(balFlag)
				{
					HashMap<String,Double> timeBalMap=(HashMap<String,Double>)queryService.queryPersonAccountTimeBalByManagerId(sysAcc.getAccountid(),nearBalDate, Utils.str82date(enddate));
					managerTimeBal+=timeBalMap.get("managerTimeBal");
					managerFixedBal+=timeBalMap.get("managerFixedBal");
					managerFinanceBal+=timeBalMap.get("managerFinanceBal");
					managerCurrentBal+=timeBalMap.get("managerCurrentBal");
				}
											
				//查询客户经理年日均
				if(avgFlag)
				{
					HashMap<String,Double> m_map=queryService.queryPersonAccountYearDayAvgByManagerId(sysAcc.getAccountid(), nearAvgDate, Utils.str82date(enddate));
					managerYearAvg+=m_map.get("yeardayavg");
					managerFixedYearAvg+=m_map.get("avgfixed");
					managerCurrentYearAvg=m_map.get("avgcurrent");
					managerFinanceYearAvg+=m_map.get("avgfinance");	
				}
									
				//查询个人质押存款年日均
				HashMap<String,Double> pledgeBalMap=(HashMap<String,Double>)queryService.queryPersonAccountPledgeAvgByManagerId(sysAcc.getAccountid(),  Utils.str82date(enddate));
				managerPledgeYearAvg+=pledgeBalMap.get("managerPledgeAvg");
				managerPledgeBal+=pledgeBalMap.get("managerPledgeBal");
				
				branchTimeBal+=managerTimeBal;
				branchCurrentBal+=managerCurrentBal;
				branchFixedBal+=managerFixedBal;
				branchFinanceBal+=managerFinanceBal;
				branchPledgeBal+=managerPledgeBal;
				branchYearAvg+=managerYearAvg;
				branchFixedYearAvg+=managerFixedYearAvg;
				branchCurrentYearAvg+=managerCurrentYearAvg;
				branchFinanceYearAvg+=managerFinanceYearAvg;
				branchPledgeYearAvg+=managerPledgeYearAvg;
				
				managerTree.timebal=df.format(managerTimeBal);
				managerTree.balfixed=df.format(managerFixedBal);
				managerTree.balcurrent=df.format(managerCurrentBal);
				managerTree.balfinance=df.format(managerFinanceBal);
				managerTree.balpledge=df.format(managerPledgeBal);
				managerTree.avg=df.format(managerYearAvg);
				managerTree.avgfixed=df.format(managerFixedYearAvg);
				managerTree.avgcurrent=df.format(managerCurrentYearAvg);
				managerTree.avgfinance=df.format(managerFinanceYearAvg);
				managerTree.pledgeavg=df.format(managerPledgeYearAvg);
				managerTree.pledgeavgenddate=enddate;
				managerTree.leaf=true;
				listManager.add(managerTree);
			}
			totalBal+=branchBal;
			totalTimeBal+=branchTimeBal;
			totalYearAvg+=branchYearAvg;
			totalPledgeYearAvg+=branchPledgeYearAvg;
			totalFixedYearAvg+=branchFixedYearAvg;
			totalCurrentYearAvg+=branchCurrentYearAvg;
			totalFinanceYearAvg+=branchFinanceYearAvg;
			totalFixedBal+=branchFixedBal;
			totalCurrentBal+=branchCurrentBal;
			totalFinanceBal+=branchFinanceBal;
			totalPledgeBal+=branchPledgeBal;
			
			branchTreeNode.bal=df.format(totalBal);
			branchTreeNode.balenddate=Utils.getFormatDate(nearBalDate, "yyyyMMdd");
			branchTreeNode.timebal=df.format(branchTimeBal);
			branchTreeNode.timebalenddate=Utils.getFormatDate(nearBalDate, "yyyyMMdd");
			branchTreeNode.avg=df.format(branchYearAvg);
			branchTreeNode.avgenddate=Utils.getFormatDate(nearAvgDate, "yyyyMMdd");
			branchTreeNode.pledgeavg=df.format(branchPledgeYearAvg);
			branchTreeNode.pledgeavgenddate=enddate;
			branchTreeNode.avgfixed=df.format(branchFixedYearAvg);
			branchTreeNode.avgcurrent=df.format(branchCurrentYearAvg);
			branchTreeNode.avgfinance=df.format(branchFinanceYearAvg);
			branchTreeNode.balfixed=df.format(branchFixedBal);
			branchTreeNode.balcurrent=df.format(branchCurrentBal);
			branchTreeNode.balfinance=df.format(branchFinanceBal);
			branchTreeNode.balpledge=df.format(branchPledgeBal);
			branchTreeNode.children=listManager;
			if(sysAccountList.size()==0)
				branchTreeNode.leaf=true;
			else
				branchTreeNode.expanded=false;
			listBranch.add(branchTreeNode);
		}
		rootNode.bal=df.format(totalBal);
		rootNode.timebal=df.format(totalTimeBal);
		rootNode.avg=df.format(totalYearAvg);
		rootNode.pledgeavg=df.format(totalPledgeYearAvg);
		rootNode.avgfixed=df.format(totalFixedYearAvg);
		rootNode.avgcurrent=df.format(totalCurrentYearAvg);
		rootNode.avgfinance=df.format(totalFinanceYearAvg);
		rootNode.balfixed=df.format(totalFixedBal);
		rootNode.balcurrent=df.format(totalCurrentBal);
		rootNode.balfinance=df.format(totalFinanceBal);
		rootNode.balpledge=df.format(totalPledgeBal);
		rootNode.children=listBranch;
		rootNode.name="合计";
		totalList.add(rootNode);
		return totalList;
	}
	
	
	
	@RequestMapping("query/queryPersonPledgeYearDayAvgDetail")
	@ResponseBody
	public Result queryPersonPledgeYearDayAvgDetail(String branchid,String managerid,String accountid,HttpSession session,
													@RequestParam(value="page")String startPage,
													@RequestParam(value="limit")String pageSize) throws ParseException
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==true)
			{
				SysBranch tmp=sysBranchService.querySubBranchList("0").get(0);
				branchid=tmp.getBranchid();
			}
			else
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		List<Map> list=queryService.queryPersonPledgeYearDayAvgDetail(branchid, managerid, accountid);
		for(int i=0;i<list.size();i++)
		{
			HashMap<String,Object> map=(HashMap<String,Object>)list.get(i);
			double tranamt=(Double)map.get("tranamt");
			Date startDate=(Date)map.get("startdate");
			Date endDate=(Date)map.get("enddate");
			
		}
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("query/queryFinanceDetail")
	@ResponseBody
	public Result QueryFinanceDetail(String branchid,
											String managerid,
											String accounttype,
											String productid,
											String productbatch,
											String startdate,
											String enddate,
											String channel,
											String iszy,
											@RequestParam(value="page")String startPage,
											@RequestParam(value="limit")String pageSize,
											HttpSession session) throws ParseException
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if((branchid==null||branchid.equals(""))&&(managerid==null||managerid.equals("")))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==false)
			{
				branchid=sysAccount.getBranchid();
			}
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		Date dStartDate=startdate.equals("")?null:Utils.str82date(startdate);
		Date dEndDate=enddate.equals("")?null:Utils.str82date(enddate);
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		List<Map> list=queryService.queryFinanceDetail(branchid, managerid, accounttype, productid, productbatch, dStartDate, dEndDate, channel,iszy);
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("query/QueryFinanceCnt")
	@ResponseBody
	public List<FinanceTree> QueryFinanceCnt(String branchid,
											String managerid,
											String accounttype,
											String productid,
											String productbatch,
											String startdate,
											String enddate,
											String channel,
											String iszy,
											String calenddate,
											HttpSession session) throws ParseException
	{
		DecimalFormat df=new DecimalFormat("#,###,###,###,###.##");
		Date dStartDate=startdate.equals("")?null:Utils.str82date(startdate);
		Date dEndDate=enddate.equals("")?null:Utils.str82date(enddate);
		Date avgEndDate=calenddate.equals("")?new Date():Utils.str82date(calenddate);
		Date avgYearStartDay=Utils.str82date(Utils.getYear(avgEndDate)+"0101");
		Date avgYearEndDay=Utils.str82date(Utils.getYear(avgEndDate)+"1231");
		
		
		List<FinanceTree>  totalList=new ArrayList<FinanceTree>();
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==true)
			{
				SysBranch tmp=sysBranchService.querySubBranchList("0").get(0);
				branchid=tmp.getBranchid();
			}
			else
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		List<SysBranch> sysBranchList=sysBranchService.queryAllSubBranchList(branchid,true);
		List<FinanceTree> listBranch=new ArrayList<FinanceTree>();
		FinanceTree rootNode=new FinanceTree();
		double totalBal=0.00;
		int cntBal=0;
		double totalTimeBal=0.00;
		double totalAvg=0.00;
		for(int i=0;i<sysBranchList.size();i++)
		{
			int branchCntBal=0;
			double branchTotalBal=0.00;
			double branchTotalTimeBal=0.00;
			double branchTotalAvg=0.00;
			SysBranch branch=sysBranchList.get(i);
			if(branch.getBranchid().equals("0000"))
				continue;
			ArrayList<String> propertyList=new ArrayList<String>();
			propertyList.add("1");
			propertyList.add("2");
			propertyList.add("3");
			propertyList.add("5");
			propertyList.add("6");
			List<SysAccount> sysAccountList=sysAccountService.querySysAccountList(managerid, "", branch.getBranchid(), propertyList);
			
			FinanceTree branchTreeNode=new FinanceTree();
			branchTreeNode.name=branch.getBranchname();
						
			List<FinanceTree> listManager=new ArrayList<FinanceTree>();
			for(int j=0;j<sysAccountList.size();j++)
			{
				FinanceTree managerTree=new FinanceTree();
				
				SysAccount sysAcc=sysAccountList.get(j);
				managerTree.name=sysAcc.getAccountname();
				
				List<Map> list=queryService.queryFinanceCnt(branch.getBranchid(), sysAcc.getAccountid(), accounttype, productid, productbatch, dStartDate, dEndDate,channel,iszy);
				HashMap<String,Object> map=(HashMap<String,Object>)list.get(0);
				int managerCntBal=Integer.valueOf(map.get("cntamt").toString());
				
				double managerTotalBal=0.00;
				if(map.get("totalamt")!=null)
				{
					managerTotalBal=Double.valueOf(map.get("totalamt").toString());
				}
				//查询理财明细
				List<Map> listDetail=queryService.queryFinanceAvgDetail(branch.getBranchid(), sysAcc.getAccountid(), avgYearStartDay, avgEndDate, accounttype);
				double managerTotalTimeBal=0.00;
				double managerTotalAvg=0.00;
				for(int index=0;index<listDetail.size();index++)
				{
					HashMap<String,Object> detailMap=(HashMap<String,Object>)listDetail.get(index);
					double percent=Double.valueOf(detailMap.get("percent").toString());
					double tranamt=Double.valueOf(detailMap.get("tranamt").toString());
					Date qxr=(Date)detailMap.get("startdate");
					Date dqr=(Date)detailMap.get("enddate");
					Date calBeginDate=avgYearStartDay.before(qxr)?qxr:avgYearStartDay;
					Date calEndDate=avgYearEndDay.before(dqr)?avgYearEndDay:dqr;
					if(avgEndDate.compareTo(dqr)<=0&&avgEndDate.compareTo(qxr)>=0)
					{
						managerTotalTimeBal+=tranamt*percent;
					}
					managerTotalAvg+=tranamt*percent*Utils.getSpaceDay(calBeginDate, calEndDate)/365;
				}
				branchCntBal+=managerCntBal;
				branchTotalBal+=managerTotalBal;
				branchTotalTimeBal+=managerTotalTimeBal;
				branchTotalAvg+=managerTotalAvg;
				managerTree.cntamt=String.valueOf(managerCntBal);
				managerTree.totalamt=df.format(managerTotalBal);
				managerTree.totaltimebal=df.format(managerTotalTimeBal);
				managerTree.totalavg=df.format(managerTotalAvg);
				managerTree.leaf=true;
				managerTree.nodetype="2";
				managerTree.id=sysAcc.getAccountid();
				listManager.add(managerTree);
			}
			totalBal+=branchTotalBal;
			cntBal+=branchCntBal;
			totalTimeBal+=branchTotalTimeBal;
			totalAvg+=branchTotalAvg;
			branchTreeNode.cntamt=String.valueOf(branchCntBal);
			branchTreeNode.totalamt=df.format(branchTotalBal);
			branchTreeNode.totaltimebal=df.format(branchTotalTimeBal);
			branchTreeNode.totalavg=df.format(branchTotalAvg);
			branchTreeNode.avgenddate=Utils.getFormatDate(avgYearStartDay, "yyyyMMdd")+"-"+Utils.getFormatDate(avgEndDate, "yyyyMMdd");
			branchTreeNode.children=listManager;
			if(sysAccountList.size()==0)
				branchTreeNode.leaf=true;
			else
				branchTreeNode.expanded=false;
			branchTreeNode.nodetype="1";
			branchTreeNode.id=branch.getBranchid();
			listBranch.add(branchTreeNode);
		}
		rootNode.cntamt=String.valueOf(cntBal);
		rootNode.totalamt=df.format(totalBal);
		rootNode.totaltimebal=df.format(totalTimeBal);
		rootNode.totalavg=df.format(totalAvg);
		rootNode.children=listBranch;
		rootNode.name="合计";
		totalList.add(rootNode);
		return totalList;
	}
	@RequestMapping("query/queryPledgeDetail")
	@ResponseBody
	public Result queryPledgeDetail(String branchid,
									String managerid,
									String startdate,
									String enddate,
									@RequestParam(value="page")String startPage,
									@RequestParam(value="limit")String pageSize,
									HttpSession session) throws ParseException
	{
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if((branchid==null||branchid.equals(""))&&(managerid==null||managerid.equals("")))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==false)
			{
				branchid=sysAccount.getBranchid();
			}
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		Date dStartDate=startdate.equals("")?null:Utils.str82date(startdate);
		Date dEndDate=enddate.equals("")?null:Utils.str82date(enddate);
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		List<Map> list=queryService.queryPledgeDetail(branchid, managerid, dStartDate, dEndDate);
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("query/QueryPledgeCnt")
	@ResponseBody
	public List<FinanceTree> QueryPledgeCnt(String branchid,
											String managerid,
											String startdate,
											String enddate,
											String calenddate,
											HttpSession session) throws ParseException
	{
		DecimalFormat df=new DecimalFormat("#,###,###,###,###.##");
		Date dStartDate=startdate.equals("")?null:Utils.str82date(startdate);
		Date dEndDate=enddate.equals("")?null:Utils.str82date(enddate);
		Date avgEndDate=calenddate.equals("")?new Date():Utils.str82date(calenddate);
		Date avgYearStartDay=Utils.str82date(Utils.getYear(avgEndDate)+"0101");
		Date avgYearEndDay=Utils.str82date(Utils.getYear(avgEndDate)+"1231");
		
		List<FinanceTree>  totalList=new ArrayList<FinanceTree>();
		List<SysBranch> sysBranchList=new ArrayList<SysBranch>();
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==true)
			{
				//SysBranch tmp=sysBranchService.querySubBranchList("0").get(0);
				//branchid=tmp.getBranchid();
				sysBranchList=sysBranchService.queryAllSubBranchList("0000",false);
			}
			else
			{
				sysBranchList=sysBranchService.queryAllSubBranchList(sysAccount.getBranchid(),true);
			}
		}
		else
		{
			sysBranchList=sysBranchService.queryAllSubBranchList(branchid,true);
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		
		List<FinanceTree> listBranch=new ArrayList<FinanceTree>();
		FinanceTree rootNode=new FinanceTree();
		double totalBal=0.00;
		int cntBal=0;
		double totalTimeBal=0.00;
		double totalAvg=0.00;
		for(int i=0;i<sysBranchList.size();i++)
		{
			int branchCntBal=0;
			double branchTotalBal=0.00;
			double branchTotalTimeBal=0.00;
			double branchTotalAvg=0.00;
			SysBranch branch=sysBranchList.get(i);
			if(branch.getBranchid().equals("0000"))
				continue;
			ArrayList<String> propertyList=new ArrayList<String>();
			propertyList.add("1");
			propertyList.add("2");
			propertyList.add("3");
			propertyList.add("5");
			propertyList.add("6");
			List<SysAccount> sysAccountList=sysAccountService.querySysAccountList(managerid, "", branch.getBranchid(), propertyList);
			
			FinanceTree branchTreeNode=new FinanceTree();
			branchTreeNode.name=branch.getBranchname();
						
			List<FinanceTree> listManager=new ArrayList<FinanceTree>();
			for(int j=0;j<sysAccountList.size();j++)
			{
				FinanceTree managerTree=new FinanceTree();
				
				SysAccount sysAcc=sysAccountList.get(j);
				managerTree.name=sysAcc.getAccountname();
				
				List<Map> list=queryService.selectPledgeCntByManagerId(branch.getBranchid(), sysAcc.getAccountid(),dStartDate, dEndDate);
				HashMap<String,Object> map=(HashMap<String,Object>)list.get(0);
				int managerCntBal=Integer.valueOf(map.get("cntamt").toString());
				
				double managerTotalBal=0.00;
				if(map.get("totalamt")!=null)
				{
					managerTotalBal=Double.valueOf(map.get("totalamt").toString());
				}
				//查询理财明细
				
				double managerTotalTimeBal=0.00;
				double managerTotalAvg=0.00;
				HashMap<String,Double> pledgeBalMap=(HashMap<String,Double> )queryService.queryPersonAccountPledgeAvgByManagerId(sysAcc.getAccountid(),avgEndDate);
				managerTotalAvg+=pledgeBalMap.get("managerPledgeAvg");
				managerTotalTimeBal+=pledgeBalMap.get("managerPledgeBal");
				
				branchCntBal+=managerCntBal;
				branchTotalBal+=managerTotalBal;
				branchTotalTimeBal+=managerTotalTimeBal;
				branchTotalAvg+=managerTotalAvg;
				managerTree.cntamt=String.valueOf(managerCntBal);
				managerTree.totalamt=df.format(managerTotalBal);
				managerTree.totaltimebal=df.format(managerTotalTimeBal);
				managerTree.totalavg=df.format(managerTotalAvg);
				managerTree.leaf=true;
				managerTree.nodetype="2";
				managerTree.id=sysAcc.getAccountid();
				listManager.add(managerTree);
			}
			totalBal+=branchTotalBal;
			cntBal+=branchCntBal;
			totalTimeBal+=branchTotalTimeBal;
			totalAvg+=branchTotalAvg;
			branchTreeNode.cntamt=String.valueOf(branchCntBal);
			branchTreeNode.totalamt=df.format(branchTotalBal);
			branchTreeNode.totaltimebal=df.format(branchTotalTimeBal);
			branchTreeNode.totalavg=df.format(branchTotalAvg);
			branchTreeNode.avgenddate=Utils.getFormatDate(avgYearStartDay, "yyyyMMdd")+"-"+Utils.getFormatDate(avgEndDate, "yyyyMMdd");
			branchTreeNode.children=listManager;
			if(sysAccountList.size()==0)
				branchTreeNode.leaf=true;
			else
				branchTreeNode.expanded=false;
			branchTreeNode.nodetype="1";
			branchTreeNode.id=branch.getBranchid();
			listBranch.add(branchTreeNode);
		}
		rootNode.cntamt=String.valueOf(cntBal);
		rootNode.totalamt=df.format(totalBal);
		rootNode.totaltimebal=df.format(totalTimeBal);
		rootNode.totalavg=df.format(totalAvg);
		rootNode.children=listBranch;
		rootNode.name="合计";
		totalList.add(rootNode);
		return totalList;
	}

	@RequestMapping("query/queryPublicDepositChangeList")
	@ResponseBody
	public Result queryPublicDepositChangeList(String branchid,
											   String accountname,
											   String startdate,
											   String enddate,
											   String minnetamt,
											   @RequestParam(value="page")String startPage,
											   @RequestParam(value="limit")String pageSize,
											   HttpSession session) throws ParseException
	{
		Date dStartDate=startdate.equals("")?null:Utils.str82date(startdate);
		Date dEndDate=enddate.equals("")?null:Utils.str82date(enddate);
		double minNetAmt=minnetamt.equals("")?0.00:Double.valueOf(minnetamt);
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		List<Map> list=queryService.queryPublicDepositList(branchid, accountname, dStartDate, dEndDate, minNetAmt);
		DecimalFormat df=new DecimalFormat("#,###,###,###,##0.00");
		for(int i=0;i<list.size();i++)
		{
			HashMap<String,Object> map=(HashMap<String,Object>)list.get(i);
			map.put("netinout", df.format(Double.valueOf(map.get("netinout").toString())));
			map.put("inamt", df.format(Double.valueOf(map.get("inamt").toString())));
			map.put("outamt", df.format(Double.valueOf(map.get("outamt").toString())));
			map.put("dqye", df.format(Double.valueOf(map.get("dqye").toString())));
			list.set(i, map);
		}
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("query/queryPublicDepositChangeDetail")
	@ResponseBody
	public Result queryPublicDepositChangeDetail(String branchid,
											   String accountname,
											   String startdate,
											   String enddate,
											   String minnetamt,
											   @RequestParam(value="page")String startPage,
											   @RequestParam(value="limit")String pageSize,
											   HttpSession session) throws ParseException
	{
		Date dStartDate=startdate.equals("")?null:Utils.str82date(startdate);
		Date dEndDate=enddate.equals("")?null:Utils.str82date(enddate);
		double minNetAmt=minnetamt.equals("")?0.00:Double.valueOf(minnetamt);
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		List<Map> list=queryService.queryPublicDepositDetail(branchid, accountname, dStartDate, dEndDate, minNetAmt);
		DecimalFormat df=new DecimalFormat("#,###,###,###,##0.00");
		for(int i=0;i<list.size();i++)
		{
			HashMap<String,Object> map=(HashMap<String,Object>)list.get(i);
			map.put("netinout", df.format(Double.valueOf(map.get("netinout").toString())));
			map.put("dqye", df.format(Double.valueOf(map.get("dqye").toString())));
			map.put("tolastday", df.format(Double.valueOf(map.get("tolastday").toString())));
			map.put("tolastyear", df.format(Double.valueOf(map.get("tolastyear").toString())));
			map.put("inamt", df.format(Double.valueOf(map.get("inamt").toString())));
			map.put("outamt", df.format(Double.valueOf(map.get("outamt").toString())));
			list.set(i, map);
		}
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("query/queryPublicDepositBalAndYearDayAvg")
	@ResponseBody 
	public  Result queryPublicDepositBalAndYearDayAvg(
										  String enddate,
										  String branchid,
										  String accounttype,
										  String accountid,
										  String customno,
										  String subcode,
										  String accountname,
										  String property,
										  String status,
										  String managerid,
										  @RequestParam(value="page")String startPage,
										  @RequestParam(value="limit")String pageSize,
										  HttpSession session) throws ParseException, UnsupportedEncodingException
	{
		
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==false)
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		if(enddate.equals(""))
		{
			enddate=Utils.getCurrentTime("yyyyMMdd");
		}
		Date queryEndDate=Utils.str82date(enddate);
		DecimalFormat df=new DecimalFormat("#,###,###,###,##0.00");
		List<Map> list=null;
		
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		if(accounttype.equals("2"))
		{
			list=queryService.queryAccountBalAndYearDayAvg(queryEndDate, branchid, managerid,accountid, accountname, customno, subcode,null, status,AccountType.PUBLIC_FIXED_ACCOUNT);
		}
		else
		{
			list=queryService.queryAccountBalAndYearDayAvg(queryEndDate, branchid, managerid,accountid, accountname, customno, subcode,null, status,AccountType.PUBLIC_CURRENT_ACCOUNT);
		}
		Date beginDate=Utils.str82date(enddate.substring(0,4)+"0101");
		String year=enddate.substring(0, 4);
		int month=Integer.valueOf(enddate.substring(4, 6));
		for(int i=0;i<list.size();i++)
		{
			HashMap<String,Object> map=(HashMap<String,Object>)list.get(i);
			List<Double> monthBal=new ArrayList<Double>();
			double total=0.00;
			map.put("bal", df.format(map.get("bal")));
			double m1=Double.valueOf((map.get("m1")==null)?"0.00":map.get("m1").toString());
			monthBal.add(0, m1);
			double m2=Double.valueOf((map.get("m2")==null)?"0.00":map.get("m2").toString());
			monthBal.add(1, m2);
			double m3=Double.valueOf((map.get("m3")==null)?"0.00":map.get("m3").toString());
			monthBal.add(2, m3);
			double m4=Double.valueOf((map.get("m4")==null)?"0.00":map.get("m4").toString());
			monthBal.add(3, m4);
			double m5=Double.valueOf((map.get("m5")==null)?"0.00":map.get("m5").toString());
			monthBal.add(4, m5);
			double m6=Double.valueOf((map.get("m6")==null)?"0.00":map.get("m6").toString());
			monthBal.add(5, m6);
			double m7=Double.valueOf((map.get("m7")==null)?"0.00":map.get("m7").toString());
			monthBal.add(6, m7);
			double m8=Double.valueOf((map.get("m8")==null)?"0.00":map.get("m8").toString());
			monthBal.add(7, m8);
			double m9=Double.valueOf((map.get("m9")==null)?"0.00":map.get("m9").toString());
			monthBal.add(8, m9);
			double m10=Double.valueOf((map.get("m10")==null)?"0.00":map.get("m10").toString());
			monthBal.add(9, m10);
			double m11=Double.valueOf((map.get("m11")==null)?"0.00":map.get("m11").toString());
			monthBal.add(10, m11);
			double m12=Double.valueOf((map.get("m12")==null)?"0.00":map.get("m12").toString());
			monthBal.add(11, m12);
			int[] monthDay={31,28,31,30,31,30,31,31,30,31,30,31};
			for(int j=1;j<=12;j++)
			{
				if(j==month)
				{
					total+=monthBal.get(j-1)*Utils.getSpaceDay(Utils.str82date(year+enddate.substring(4, 6)+"01"), queryEndDate);
					break;
				}
				total+=monthBal.get(j-1)*monthDay[j-1];
			}
			double yearDayAvg=total/Utils.getSpaceDay(Utils.str82date(year+"0101"), queryEndDate);
			double yearDayAvg1=total/365;
			map.put("yeardayavg", df.format(yearDayAvg));
			map.put("yeardayavg1", df.format(yearDayAvg1));
			list.set(i, map);
		}
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	@RequestMapping("query/queryPublicCreditBalAndYearDayAvg")
	@ResponseBody 
	public  Result queryPublicCreditBalAndYearDayAvg(
										  String enddate,
										  String branchid,
										  String accountid,
										  String customno,
										  String subcode,
										  String accountname,
										  String property,
										  String status,
										  String managerid,
										  @RequestParam(value="page")String startPage,
										  @RequestParam(value="limit")String pageSize,
										  HttpSession session) throws ParseException, UnsupportedEncodingException
	{
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==false)
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		if(enddate.equals(""))
		{
			enddate=Utils.getCurrentTime("yyyyMMdd");
		}
		Date queryEndDate=Utils.str82date(enddate);
		DecimalFormat df=new DecimalFormat("#,###,###,###,##0.00");
		List<Map> list=queryService.queryAccountBalAndYearDayAvg(queryEndDate, branchid,managerid, accountid, accountname, customno, subcode,null, status,AccountType.PUBLIC_CREDIT_ACCOUNT);
		Date beginDate=Utils.str82date(enddate.substring(0,4)+"0101");
		String year=enddate.substring(0, 4);
		int month=Integer.valueOf(enddate.substring(4, 6));
		for(int i=0;i<list.size();i++)
		{
			HashMap<String,Object> map=(HashMap<String,Object>)list.get(i);
			List<Double> monthBal=new ArrayList<Double>();
			double total=0.00;
			map.put("bal", df.format(map.get("bal")));
			double m1=Double.valueOf((map.get("m1")==null)?"0.00":map.get("m1").toString());
			monthBal.add(0, m1);
			double m2=Double.valueOf((map.get("m2")==null)?"0.00":map.get("m2").toString());
			monthBal.add(1, m2);
			double m3=Double.valueOf((map.get("m3")==null)?"0.00":map.get("m3").toString());
			monthBal.add(2, m3);
			double m4=Double.valueOf((map.get("m4")==null)?"0.00":map.get("m4").toString());
			monthBal.add(3, m4);
			double m5=Double.valueOf((map.get("m5")==null)?"0.00":map.get("m5").toString());
			monthBal.add(4, m5);
			double m6=Double.valueOf((map.get("m6")==null)?"0.00":map.get("m6").toString());
			monthBal.add(5, m6);
			double m7=Double.valueOf((map.get("m7")==null)?"0.00":map.get("m7").toString());
			monthBal.add(6, m7);
			double m8=Double.valueOf((map.get("m8")==null)?"0.00":map.get("m8").toString());
			monthBal.add(7, m8);
			double m9=Double.valueOf((map.get("m9")==null)?"0.00":map.get("m9").toString());
			monthBal.add(8, m9);
			double m10=Double.valueOf((map.get("m10")==null)?"0.00":map.get("m10").toString());
			monthBal.add(9, m10);
			double m11=Double.valueOf((map.get("m11")==null)?"0.00":map.get("m11").toString());
			monthBal.add(10, m11);
			double m12=Double.valueOf((map.get("m12")==null)?"0.00":map.get("m12").toString());
			monthBal.add(11, m12);
			int[] monthDay={31,28,31,30,31,30,31,31,30,31,30,31};
			for(int j=1;j<=12;j++)
			{
				if(j==month)
				{
					total+=monthBal.get(j-1)*Utils.getSpaceDay(Utils.str82date(year+enddate.substring(4, 6)+"01"), queryEndDate);
					break;
				}
				total+=monthBal.get(j-1)*monthDay[j-1];
			}
			double yearDayAvg=total/Utils.getSpaceDay(Utils.str82date(year+"0101"), queryEndDate);
			double yearDayAvg1=total/365;
			map.put("yeardayavg", df.format(yearDayAvg));
			map.put("yeardayavg1", df.format(yearDayAvg1));
			list.set(i, map);
		}
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
	
	@RequestMapping("query/queryPublicBusinessData")
	@ResponseBody
	public List<PublicTree> queryPublicBusinessData(String enddate,String branchid,String managerid,HttpSession session) throws ParseException
	{
		DecimalFormat df=new DecimalFormat("#,###,###,###,###.##");
		List<PublicTree>  totalList=new ArrayList<PublicTree>();
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==true)
			{
				SysBranch tmp=sysBranchService.querySubBranchList("0").get(0);
				branchid=tmp.getBranchid();
			}
			else
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		List<SysBranch> sysBranchList=sysBranchService.queryAllSubBranchList(branchid,true);
		List<PublicTree> listBranch=new ArrayList<PublicTree>();
		PublicTree rootNode=new PublicTree();
		double totalDepositBal=0.00;
		double totalDepositTimeBal=0.00;
		double totalDepositYearAvg=0.00;
		double totalCreditTimeBal=0.00;
		double totalCreditYearAvg=0.00;
		for(int i=0;i<sysBranchList.size();i++)
		{
			double branchBal=0.00;
			double branchDepositTimeBal=0.00;
			double branchDepositYearAvg=0.00;
			double branchCreditTimeBal=0.00;
			double branchCreditYearAvg=0.00;
			boolean balFlag=true;
			boolean avgFlag=true;
			boolean creditFlag=true;
			SysBranch branch=sysBranchList.get(i);
			if(branch.getBranchid().equals("0000"))
				continue;
			ArrayList<String> propertyList=new ArrayList<String>();
			propertyList.add("1");
			propertyList.add("2");
			propertyList.add("3");
			propertyList.add("5");
			propertyList.add("6");
			List<SysAccount> sysAccountList=sysAccountService.querySysAccountList(managerid, "", branch.getBranchid(), propertyList);
			
			PublicTree branchTreeNode=new PublicTree();
			branchTreeNode.name=branch.getBranchname();
			
			Date nearFixedBalDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(),ExcelType.PUBLIC_FIXED_ACCOUNT);
			Date nearCurrentBalDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(), ExcelType.PUBLIC_CURRENT_ACCOUNT);
			Date nearBalDate =Utils.str82date(enddate);
			if(nearFixedBalDate==null&&nearCurrentBalDate==null)
			{
				balFlag=false;
			}
			else if(nearFixedBalDate==null||nearCurrentBalDate==null)
			{
				if(nearFixedBalDate==null)
					nearBalDate=nearCurrentBalDate;
				else
					nearBalDate=nearFixedBalDate;
			}
			else
			{
				nearBalDate=nearFixedBalDate.before(nearCurrentBalDate)?nearCurrentBalDate:nearFixedBalDate;
			}

			Date nearFixedAvgDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(),ExcelType.PUBLIC_FIXED_ACCOUNT_DAYAVG);
			Date nearCurrentAvgDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(), ExcelType.PUBLIC_CURRENT_ACCOUNT_DAYAVG);
			Date nearAvgDate=Utils.str82date(enddate);
			if(nearFixedAvgDate==null&&nearCurrentAvgDate==null)
			{
				avgFlag=false;
			}
			else if(nearFixedAvgDate==null||nearCurrentAvgDate==null)
			{
				if(nearFixedAvgDate==null)
					nearAvgDate=nearCurrentAvgDate;				
				else
					nearAvgDate=nearFixedAvgDate;
			}
			else
			{
				nearAvgDate=nearFixedAvgDate.before(nearCurrentAvgDate)?nearCurrentAvgDate:nearFixedAvgDate;
			}
			//对公贷款最近数据导入日期
			Date nearCreditBalDate=queryService.quereLatestImportFileDate(Utils.str82date(enddate),branch.getBranchid(),ExcelType.PUBLIC_CREDIT_ACCOUNT);
			if(nearCreditBalDate==null)
			{
				creditFlag=false;
			}
			
			
			List<PublicTree> listManager=new ArrayList<PublicTree>();
			for(int j=0;j<sysAccountList.size();j++)
			{
				
				PublicTree managerTree=new PublicTree();
				
				SysAccount sysAcc=sysAccountList.get(j);
				/*
				if(isPublicManager(sysAcc.getBusiFeature())==false)
				{
					continue;
				}*/
				managerTree.name=sysAcc.getAccountname();
				
				//查询客户经理对公存款时点余额
				if(balFlag)
				{
					HashMap<String,Double> timeBalMap=(HashMap<String,Double>)queryService.queryPublicDepositTimeBalByManagerId(sysAcc.getAccountid(),nearBalDate, Utils.str82date(enddate));
					double timeBal=timeBalMap.get("timeBal");
					branchDepositTimeBal+=timeBal;
					managerTree.depositbal=df.format(timeBal);
					//managerTree.depositbaldate=Utils.getFormatDate(nearBalDate, "yyyyMMdd");
				}
				
														
				//查询客户经理对公存款年日均
				if(avgFlag)
				{
					double yearavg=queryService.queryPublicDepositYearDayAvgByManagerId(sysAcc.getAccountid(), nearAvgDate, Utils.str82date(enddate));
					branchDepositYearAvg+=yearavg;
					managerTree.depositavg=df.format(yearavg);
					//managerTree.depositavgdate=Utils.getFormatDate(nearAvgDate, "yyyyMMdd");
				}
									
				//查询客户经理对公存贷款时点余额
				if(creditFlag)
				{
					double creditTimeBal=queryService.queryPublicCreditTimeBalByManagerId(sysAcc.getAccountid(), nearCreditBalDate, Utils.str82date(enddate));
					branchCreditTimeBal+=creditTimeBal;
					double creditYearAvg=queryService.queryPublicCreditYearDayAvgByManagerId(sysAcc.getAccountid(), nearCreditBalDate, Utils.str82date(enddate));
					branchCreditYearAvg+=creditYearAvg;
					managerTree.creditbal=df.format(creditTimeBal);
					managerTree.creditavg=df.format(creditYearAvg);
					//managerTree.creditdate=Utils.getFormatDate(nearCreditBalDate, "yyyyMMdd");
				}
				managerTree.leaf=true;				
				listManager.add(managerTree);
			}
			totalDepositTimeBal+=branchDepositTimeBal;
			totalDepositYearAvg+=branchDepositYearAvg;
			totalCreditTimeBal+=branchCreditTimeBal;
			totalCreditYearAvg+=branchCreditYearAvg;
			
			branchTreeNode.depositbal=df.format(branchDepositTimeBal);
			branchTreeNode.depositbaldate=Utils.getFormatDate(nearBalDate, "yyyyMMdd");
			branchTreeNode.depositavg=df.format(branchDepositYearAvg);
			branchTreeNode.depositavgdate=Utils.getFormatDate(nearAvgDate, "yyyyMMdd");
			branchTreeNode.creditbal=df.format(branchCreditTimeBal);
			branchTreeNode.creditavg=df.format(branchCreditYearAvg);
			branchTreeNode.creditdate=nearCreditBalDate==null?"":Utils.getFormatDate(nearCreditBalDate, "yyyyMMdd");
			branchTreeNode.children=listManager;
			if(sysAccountList.size()==0)
				branchTreeNode.leaf=true;
			else
				branchTreeNode.expanded=false;
			listBranch.add(branchTreeNode);
		}
		rootNode.depositbal=df.format(totalDepositTimeBal);
		rootNode.depositavg=df.format(totalDepositYearAvg);
		rootNode.creditbal=df.format(totalCreditTimeBal);
		rootNode.creditavg=df.format(totalCreditYearAvg);
		rootNode.children=listBranch;
		rootNode.name="合计";
		totalList.add(rootNode);
		return totalList;
	}
	@RequestMapping("query/queryPersonDepositBalAndYearDayAvg")
	@ResponseBody 
	public  Result queryPersonDepositBalAndYearDayAvg(
										  String enddate,
										  String branchid,
										  String accounttype,
										  String accountid,
										  String customno,
										  String subcode,
										  String accountname,
										  String property,
										  String status,
										  String managerid,
										  @RequestParam(value="page")String startPage,
										  @RequestParam(value="limit")String pageSize,
										  HttpSession session) throws ParseException, UnsupportedEncodingException
	{
		
		SysAccount sysAccount=(SysAccount)session.getAttribute("user");
		if(branchid==null||branchid.equals(""))
		{
			if(Utils.isAuthorQueryAllBranchData(sysAccount)==false)
			{
				branchid=sysAccount.getBranchid();
			}
		}
		if(managerid==null||managerid.equals(""))
		{
			if(Utils.isAuthorQueryOneSelfData(sysAccount)==true)
			{
				managerid=sysAccount.getAccountid();
			}
		}
		if(enddate.equals(""))
		{
			enddate=Utils.getCurrentTime("yyyyMMdd");
		}
		Date queryEndDate=Utils.str82date(enddate);
		DecimalFormat df=new DecimalFormat("#,###,###,###,##0.00");
		List<Map> list=null;
		PageHelper.startPage(Integer.parseInt(startPage), Integer.parseInt(pageSize),true);
		if(accounttype.equals("2"))
		{
			list=queryService.queryAccountBalAndYearDayAvg(queryEndDate, branchid, managerid,accountid, accountname, customno, subcode,null, status,AccountType.PERSON_FIXED_ACCOUNT);
		}
		else
		{
			list=queryService.queryAccountBalAndYearDayAvg(queryEndDate, branchid,managerid, accountid, accountname, customno, subcode,null, status,AccountType.PERSON_CURRENT_ACCOUNT);
		}
		Date beginDate=Utils.str82date(enddate.substring(0,4)+"0101");
		String year=enddate.substring(0, 4);
		int month=Integer.valueOf(enddate.substring(4, 6));
		for(int i=0;i<list.size();i++)
		{
			HashMap<String,Object> map=(HashMap<String,Object>)list.get(i);
			List<Double> monthBal=new ArrayList<Double>();
			double total=0.00;
			map.put("bal", df.format(map.get("bal")));
//			double m1=Double.valueOf((map.get("m1")==null)?"0.00":map.get("m1").toString());
//			monthBal.add(0, m1);
//			double m2=Double.valueOf((map.get("m2")==null)?"0.00":map.get("m2").toString());
//			monthBal.add(1, m2);
//			double m3=Double.valueOf((map.get("m3")==null)?"0.00":map.get("m3").toString());
//			monthBal.add(2, m3);
//			double m4=Double.valueOf((map.get("m4")==null)?"0.00":map.get("m4").toString());
//			monthBal.add(3, m4);
//			double m5=Double.valueOf((map.get("m5")==null)?"0.00":map.get("m5").toString());
//			monthBal.add(4, m5);
//			double m6=Double.valueOf((map.get("m6")==null)?"0.00":map.get("m6").toString());
//			monthBal.add(5, m6);
//			double m7=Double.valueOf((map.get("m7")==null)?"0.00":map.get("m7").toString());
//			monthBal.add(6, m7);
//			double m8=Double.valueOf((map.get("m8")==null)?"0.00":map.get("m8").toString());
//			monthBal.add(7, m8);
//			double m9=Double.valueOf((map.get("m9")==null)?"0.00":map.get("m9").toString());
//			monthBal.add(8, m9);
//			double m10=Double.valueOf((map.get("m10")==null)?"0.00":map.get("m10").toString());
//			monthBal.add(9, m10);
//			double m11=Double.valueOf((map.get("m11")==null)?"0.00":map.get("m11").toString());
//			monthBal.add(10, m11);
//			double m12=Double.valueOf((map.get("m12")==null)?"0.00":map.get("m12").toString());
//			monthBal.add(11, m12);
//			int[] monthDay={31,28,31,30,31,30,31,31,30,31,30,31};
//			for(int j=1;j<=12;j++)
//			{
//				if(j==month)
//				{
//					total+=monthBal.get(j-1)*Utils.getSpaceDay(Utils.str82date(year+enddate.substring(4, 6)+"01"), queryEndDate);
//					break;
//				}
//				total+=monthBal.get(j-1)*monthDay[j-1];
//			}
//			double yearDayAvg=total/Utils.getSpaceDay(Utils.str82date(year+"0101"), queryEndDate);
			map.put("yeardayavg", df.format(Double.valueOf(map.get("yearavg").toString())));
			list.set(i, map);
		}
		Result result=new Result(true);
		result.setTotalsize( String.valueOf(((Page)list).getTotal()));
		result.setItems(list);
		return result;
	}
}
