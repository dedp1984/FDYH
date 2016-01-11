package com.nantian.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.nantian.custom.AccountType;
import com.nantian.custom.BusiType;
import com.nantian.custom.ExcelType;
import com.nantian.custom.Utils;
import com.nantian.dao.ReportMapper;

@Service
public class QueryService
{
	
	@Resource
	private ReportMapper reportMapper;
	/**
	 * ���ܣ����ݲ�ѯ����+����+ҵ�����Ͳ�ѯ���ݽ�ֹ����
	 * ������   date-��ѯ����
	 * 		branchId-����ID
	 * 		type-ҵ������
	 * **/
	public Date quereLatestImportFileDate(Date queryDate,String branchId,String type)
	{
		return reportMapper.selectCloseDate(queryDate, branchId, type);
	}
	/**
	 * ���ܣ���������+�ͻ�����Ų�ѯ�������˿ͻ�ָ���������վ�
	 * ������managerId�ͻ�������
	 * 	   queryDate��ѯ����
	 * @throws ParseException 
	 * **/
	public HashMap<String,Double> queryPersonAccountYearDayAvgByManagerId(String managerId,Date nearDate,Date realQueryDate) throws ParseException
	{
		HashMap<String,Double> map=new HashMap<String,Double>();
		Double yearDayAvg=0.00;
		/**
		 * �������ڲ�ѯ�ͻ������Ӧ���˶����˻����վ������Ϣ
		 * **/
		List<Map> fixedAccountYearDayAvg=reportMapper.selectAccountBalAndYearAvgByManagerid(null,null,managerId, nearDate,AccountType.PERSON_FIXED_ACCOUNT);
		double avgfixed=0.00;
		for(int i=0;i<fixedAccountYearDayAvg.size();i++)
		{
			HashMap<String,Object> dayAvg=(HashMap<String,Object>)fixedAccountYearDayAvg.get(i);
			double percent=Double.valueOf(dayAvg.get("percent").toString());
			double avg=Double.valueOf(dayAvg.get("yeardayavg").toString());
			Date gendate=(Date)dayAvg.get("gendate");
			String year=Utils.getYear(gendate);
			Date firstDate=Utils.str82date(year+"0101");
			
			//��ȡ��ѯ�յ�1��1��֮������
			int spaceDays=(int) ((realQueryDate.getTime()-firstDate.getTime())/(24*60*60*1000))+1;
			int totalDays=Utils.getYearDays(year);
			avgfixed+=percent*avg*spaceDays/totalDays;
			
		}
		yearDayAvg+=avgfixed;
		/**
		 * �������ڲ�ѯ�ͻ������Ӧ���˻����˻����վ������Ϣ
		 * **/
		List<Map> currentAccountYearDayAvg=reportMapper.selectAccountBalAndYearAvgByManagerid(null,null,managerId, nearDate,AccountType.PERSON_CURRENT_ACCOUNT);
		double avgcurrent=0.00;
		for(int i=0;i<currentAccountYearDayAvg.size();i++)
		{
			HashMap<String,Object> dayAvg=(HashMap<String,Object>)currentAccountYearDayAvg.get(i);
			double percent=Double.valueOf(dayAvg.get("percent").toString());
			double avg=Double.valueOf(dayAvg.get("yeardayavg").toString());
			Date gendate=(Date)dayAvg.get("gendate");
			String year=Utils.getYear(gendate);
			Date firstDate=Utils.str82date(year+"0101");
			
			//��ȡ��ѯ�յ�1��1��֮������
			int spaceDays=(int) ((realQueryDate.getTime()-firstDate.getTime())/(24*60*60*1000))+1;
			int totalDays=Utils.getYearDays(year);
			avgcurrent+=percent*avg*spaceDays/totalDays;
		}
		yearDayAvg+=avgcurrent;
		
		/**
		 * ����queryDate��ѯ�ͻ�����������������վ�������˻���Ϣ
		 * **/

		String year=Utils.getYear(realQueryDate);
		Date firstDate=Utils.str82date(year+"0101");
		Date lastDate=Utils.str82date(year+"1231");
		List<Map> financeAccountList=reportMapper.selectFinanceAcountYearDayAvg(managerId, firstDate,realQueryDate,null);
		double avgfinance=0.00;
		for(int i=0;i<financeAccountList.size();i++)
		{
			HashMap<String,Object> financeAccount=(HashMap<String,Object>)financeAccountList.get(i);
			double percent=Double.valueOf(financeAccount.get("percent").toString());
			double currentBal=Double.valueOf(financeAccount.get("bal").toString());
			double realbal=percent*currentBal;
			
			Date startDate=(Date)financeAccount.get("startdate");
			Date endDate=(Date)financeAccount.get("enddate");
			/*��ȡ������������*/
			Date khStartDate=startDate.before(firstDate)?firstDate:startDate;
			Date khEndDate=endDate.before(realQueryDate)?endDate:realQueryDate;
			
			int spaceDays=(int) ((khEndDate.getTime()-khStartDate.getTime())/(24*60*60*1000))+1;
			int totalDays=Utils.getYearDays(year);
			avgfinance+=realbal*spaceDays/totalDays;
		}
		yearDayAvg+=avgfinance;
		DecimalFormat df=new DecimalFormat(".##"); 
		yearDayAvg=Double.valueOf(df.format(yearDayAvg));
		map.put("avgfixed", avgfixed);
		map.put("avgcurrent", avgcurrent);
		map.put("avgfinance", avgfinance);
		map.put("yeardayavg", yearDayAvg);
		return map;
	}
	
	/**
	 * ���ܣ���������+�ͻ�����Ų�ѯ�����˻�ָ������ʱ�����
	 * ������managerId�ͻ�������
	 * 	   queryDate��ѯ����
	 * @throws ParseException 
	 * **/
	public Map queryPersonAccountTimeBalByManagerId(String managerId,Date nearDate,Date realQueryDate) throws ParseException
	{
		double managerTimeBal=0.00;
		double managerFinanceBal=0.00;
		double managerFixedBal=0.00;
		double managerCurrentBal=0.00;
				
		/**
		 * ����������ڲ�ѯ�ͻ������Ӧ�ĸ��˶����˻���ϸ
		 * **/
		List<Map> fixedAccountList=reportMapper.selectAccountBalAndYearAvgByManagerid(null,null,managerId, nearDate, AccountType.PERSON_FIXED_ACCOUNT);
		
		for(int i=0;i<fixedAccountList.size();i++)
		{
			HashMap<String,Object> fixedAccount=(HashMap<String,Object>)fixedAccountList.get(i);
			double percent=Double.valueOf(fixedAccount.get("percent").toString());
			double fixedBal=Double.valueOf(fixedAccount.get("bal").toString());
			//����ʱ�����=����ʱ�����*percent
			managerTimeBal+=fixedBal*percent;
			managerFixedBal+=fixedBal*percent;
		}
		
		/**
		 * ����������ڲ�ѯ�ͻ������Ӧ�ĸ��˻����˻���ϸ
		 * **/
		List<Map> currentAccountList=reportMapper.selectAccountBalAndYearAvgByManagerid(null,null,managerId, nearDate, AccountType.PERSON_CURRENT_ACCOUNT);
		for(int i=0;i<currentAccountList.size();i++)
		{
			HashMap<String,Object> currentAccount=(HashMap<String,Object>)currentAccountList.get(i);
			double percent=Double.valueOf(currentAccount.get("percent").toString());
			double currentBal=Double.valueOf(currentAccount.get("bal").toString());
			//����ʱ�����=����ʱ�����*percent
			managerTimeBal+=currentBal*percent;
			managerCurrentBal+=currentBal*percent;
		}
		
		/**
		 * ��ѯ���ʱ�����
		 * **/
		String year=Utils.getYear(realQueryDate);
		Date firstDate=Utils.str82date(year+"0101");
		Date lastDate=Utils.str82date(year+"1231");
		List<Map> financeAccountList=reportMapper.selectFinanceAccountBal(managerId, firstDate,realQueryDate, null);
		for(int i=0;i<financeAccountList.size();i++)
		{
			HashMap<String,Object> financeAccount=(HashMap<String,Object>)financeAccountList.get(i);
			double percent=Double.valueOf(financeAccount.get("percent").toString());
			double financeBal=Double.valueOf(financeAccount.get("bal").toString());
			//���ʱ�����=���ʱ�����*percent
			managerTimeBal+=financeBal*percent;
			managerFinanceBal+=financeBal*percent;
		}
		HashMap<String,Double> timeBalMap=new HashMap<String,Double>();
		timeBalMap.put("managerTimeBal", managerTimeBal);
		timeBalMap.put("managerCurrentBal", managerCurrentBal);
		timeBalMap.put("managerFixedBal", managerFixedBal);
		timeBalMap.put("managerFinanceBal", managerFinanceBal);
		return timeBalMap;
	}
	
	/**
	 * ���ܣ���������+�ͻ�����Ų�ѯ������Ѻ���ͻ����վ�
	 * ������ managerId:�ͻ�����ID
	 *     queryDate:��ѯ����
	 * @throws ParseException 
	 * **/
	public Map queryPersonAccountPledgeAvgByManagerId(String managerId,Date queryDate) throws ParseException
	{
		
		double pledgeBal=0.00;
		double pledgeAvg=0.00;
		String year=Utils.getYear(queryDate);
		Date firstDate=Utils.str82date(year+"0101");
		Date lastDate=Utils.str82date(year+"1231");
		List<Map> pledgeAccountList=reportMapper.selectPersonAccountPledge(managerId,firstDate,queryDate,AccountType.PERSON_PLEDGE_ACCOUNT);
		for(int i=0;i<pledgeAccountList.size();i++)
		{
			HashMap<String,Object> pledgeAccount=(HashMap<String,Object>)pledgeAccountList.get(i);
			double percent=Double.valueOf(pledgeAccount.get("percent").toString());
			double bal=Double.valueOf(pledgeAccount.get("bal").toString());
			Date startDate=(Date)pledgeAccount.get("startdate");
			Date endDate=(Date)pledgeAccount.get("enddate");
			/*��ȡ������������*/
			Date khStartDate=startDate.before(firstDate)?firstDate:startDate;
			Date khEndDate=endDate.before(queryDate)?endDate:queryDate;
			
			int spaceDays=(int) ((khEndDate.getTime()-khStartDate.getTime())/(24*60*60*1000));
			int totalDays=Utils.getYearDays(year);
			
			pledgeAvg+=percent*bal*spaceDays/totalDays;
			/*ʱ�����ֻͳ�Ʋ�ѯ�����ڿ�ʼ�������������֮������*/
			if(queryDate.compareTo(endDate)<=0&&queryDate.compareTo(startDate)>=0)
			{
				pledgeBal+=percent*bal;
			}
			
		}
		HashMap<String,Double> pledgeBalMap=new HashMap<String,Double>();
		pledgeBalMap.put("managerPledgeBal", pledgeBal);
		pledgeBalMap.put("managerPledgeAvg", pledgeAvg);
		return pledgeBalMap;
	}

	
	public List<Map> queryPersonPledgeYearDayAvgDetail(String branchId,String managerId,String accountId) throws ParseException
	{
		return reportMapper.selectPersonPledgeYearDayAvgDetail(branchId, managerId, accountId);
	}
	
	public List<Map> queryFinanceDetail(String branchId,String managerId,String busitype,String productId,String productBatch,Date startDate,Date endDate,String channel,String iszy)
	{
		return reportMapper.selectFinanceDetail(branchId, managerId, productId, productBatch, channel, startDate, endDate, busitype,iszy);
	}
	public List<Map> queryPledgeDetail(String branchId,String managerId,Date startDate,Date endDate)
	{
		return reportMapper.selectPledgeDetail(branchId, managerId, startDate, endDate);
	}
	public List<Map> queryFinanceAvgDetail(String branchId,String managerId,Date startDate,Date endDate,String busitype)
	{
		return reportMapper.selectFinanceAvgDetail(branchId, managerId, startDate, endDate, busitype);
	}
	public List<Map> queryFinanceCnt(String branchId,String managerId,String busitype,String productId,String productBatch,Date startDate,Date endDate,String channel,String iszy)
	{
		return reportMapper.selectFinanceCnt(branchId, managerId, busitype, startDate, endDate, productId, productBatch,channel,iszy);
	}
	public List<Map> selectPledgeCntByManagerId(String branchId,String managerId,Date startDate,Date endDate)
	{
		return reportMapper.selectPledgeCntByManagerId(branchId, managerId, startDate, endDate);
	}
	//��ѯ�Թ������˻����䶯��ϸ
	public List<Map> queryPublicDepositDetail(String branchId,String accountName,Date startDate,Date endDate,double minNetAmt)
	{
		return reportMapper.selectPublicDepositChangeDetail(branchId, accountName, startDate, endDate, minNetAmt);
	}
	//��ѯ�Թ����䶯�б�
	public List<Map> queryPublicDepositList(String branchId,String accountName,Date startDate,Date endDate,double minNetAmt)
	{
		return reportMapper.selectPublicDepositChangeList(branchId, accountName, startDate, endDate, minNetAmt);
	}
	
	public List<Map> queryAccountBalAndYearDayAvg(Date date,String branchid,String managerid, String accountId,String accountName,String customNo,String subCode,String property,String status,String accounttype)
	{
		return reportMapper.selectAccountBalAndYearDayAvg(date, branchid, managerid,accountId, accountName, customNo, subCode, property, status, accounttype);
	}
	/**
	 * ���ܣ���������+�ͻ�����Ų�ѯ�����Թ��˻�ָ������ʱ�����
	 * ������managerId�ͻ�������
	 * 	   queryDate��ѯ����
	 * **/
	public Map queryPublicDepositTimeBalByManagerId(String managerId,Date nearDate,Date realQueryDate)
	{
		Double bal=0.00;
		Double timeBal=0.00;
		
				
		/**
		 * ����������ڲ�ѯ�ͻ������Ӧ�ĸ��˶����˻���ϸ
		 * **/
		List<Map> fixedAccountList=reportMapper.selectAccountBalAndYearAvgByManagerid(null, null, managerId, nearDate, AccountType.PUBLIC_FIXED_ACCOUNT);
		
		for(int i=0;i<fixedAccountList.size();i++)
		{
			HashMap<String,Object> fixedAccount=(HashMap<String,Object>)fixedAccountList.get(i);
			double percent=Double.valueOf(fixedAccount.get("percent").toString());
			double fixedBal=Double.valueOf(fixedAccount.get("bal").toString());
			//���
			bal+=fixedBal*percent;
			//����ʱ�����=����ʱ�����*percent
			timeBal+=fixedBal*percent;
		}
		System.out.println(managerId);
		/**
		 * ����������ڲ�ѯ�ͻ������Ӧ�ĸ��˻����˻���ϸ
		 * **/
		List<Map> currentAccountList=reportMapper.selectAccountBalAndYearAvgByManagerid(null, null, managerId, nearDate, AccountType.PUBLIC_CURRENT_ACCOUNT);
		for(int i=0;i<currentAccountList.size();i++)
		{
			HashMap<String,Object> currentAccount=(HashMap<String,Object>)currentAccountList.get(i);
			double percent=Double.valueOf(currentAccount.get("percent").toString());
			double currentBal=Double.valueOf(currentAccount.get("bal").toString());
			//���
			bal+=currentBal;
			//����ʱ�����=����ʱ�����*percent
			timeBal+=currentBal*percent;
		}
		
		HashMap<String,Double> timeBalMap=new HashMap<String,Double>();
		timeBalMap.put("timeBal",timeBal);
		return timeBalMap;
	}
	public Double calYearDayAvg(Date queryDate,HashMap<String,Object> map) throws ParseException
	{
		String year=Utils.getYear(queryDate);
		String month=Utils.getMonth(queryDate);
		List<Double> monthBal=new ArrayList<Double>();
		double total=0.00;
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
			if(j==Integer.valueOf(month))
			{
				total+=monthBal.get(j-1)*Utils.getSpaceDay(Utils.str82date(year+month+"01"), queryDate);
				break;
			}
			total+=monthBal.get(j-1)*monthDay[j-1];
		}
		double percent=Double.valueOf(map.get("percent").toString());
		double yearDayAvg=total/Utils.getSpaceDay(Utils.str82date(year+"0101"), queryDate)*percent;
		return yearDayAvg;
		
	}
	/**
	 * ���ܣ���������+�ͻ�����Ų�ѯ�����Թ��ͻ�ָ���������վ�
	 * ������managerId�ͻ�������
	 * 	   queryDate��ѯ����
	 * @throws ParseException 
	 * **/
	public Double queryPublicDepositYearDayAvgByManagerId(String managerId,Date nearDate,Date realQueryDate) throws ParseException
	{
		Double yearDayAvg=0.00;
		/**
		 * �������ڲ�ѯ�ͻ������Ӧ���˶����˻����վ������Ϣ
		 * **/
		List<Map> fixedAccountYearDayAvg=reportMapper.selectAccountBalAndYearAvgByManagerid(null, null, managerId, nearDate, AccountType.PUBLIC_FIXED_ACCOUNT);
		for(int i=0;i<fixedAccountYearDayAvg.size();i++)
		{
			HashMap<String,Object> dayAvg=(HashMap<String,Object>)fixedAccountYearDayAvg.get(i);
			yearDayAvg+=calYearDayAvg(realQueryDate,dayAvg);
		}
		/**
		 * �������ڲ�ѯ�ͻ������Ӧ���˻����˻����վ������Ϣ
		 * **/
		List<Map> currentAccountYearDayAvg=reportMapper.selectAccountBalAndYearAvgByManagerid(null, null, managerId, nearDate, AccountType.PUBLIC_CURRENT_ACCOUNT);
		for(int i=0;i<currentAccountYearDayAvg.size();i++)
		{
			HashMap<String,Object> dayAvg=(HashMap<String,Object>)currentAccountYearDayAvg.get(i);
			yearDayAvg+=calYearDayAvg(realQueryDate,dayAvg);
		}
		return yearDayAvg;
	}
	public Double queryPublicCreditTimeBalByManagerId(String managerId,Date nearDate,Date realQueryDate)
	{
		Double timeBal=0.00;			
		/**
		 * ����������ڲ�ѯ�ͻ������Ӧ�ĸ��˴����˻���ϸ
		 * **/
		List<Map> creditAccountList=reportMapper.selectAccountBalAndYearAvgByManagerid(null,null,managerId, nearDate, AccountType.PUBLIC_CREDIT_ACCOUNT);
		
		for(int i=0;i<creditAccountList.size();i++)
		{
			HashMap<String,Object> fixedAccount=(HashMap<String,Object>)creditAccountList.get(i);
			double percent=Double.valueOf(fixedAccount.get("percent").toString());
			double fixedBal=Double.valueOf(fixedAccount.get("bal").toString());
			//����ʱ�����=����ʱ�����*percent
			timeBal+=fixedBal*percent;
		}
		return timeBal;
	}
	/**
	 * ���ܣ���������+�ͻ�����Ų�ѯ�����Թ��ͻ�ָ���������վ�
	 * ������managerId�ͻ�������
	 * 	   queryDate��ѯ����
	 * @throws ParseException 
	 * **/
	public Double queryPublicCreditYearDayAvgByManagerId(String managerId,Date nearDate,Date realQueryDate) throws ParseException
	{
		Double yearDayAvg=0.00;
		/**
		 * �������ڲ�ѯ�ͻ������Ӧ���˶����˻����վ������Ϣ
		 * **/
		List<Map> creditAccountYearDayAvg=reportMapper.selectAccountBalAndYearAvgByManagerid(null, null, managerId, nearDate, AccountType.PUBLIC_CREDIT_ACCOUNT);
		for(int i=0;i<creditAccountYearDayAvg.size();i++)
		{
			HashMap<String,Object> dayAvg=(HashMap<String,Object>)creditAccountYearDayAvg.get(i);
			yearDayAvg+=calYearDayAvg(realQueryDate,dayAvg);
		}
		return yearDayAvg;
	}
	
}
