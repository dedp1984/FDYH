package com.nantian.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sun.org.glassfish.external.probe.provider.annotations.ProbeParam;
import com.sun.org.glassfish.gmbal.ParameterNames;

public interface ReportMapper
{
	/**
	 * ���ܣ����ݲ�ѯ����+����+ҵ�����Ͳ�ѯ���ݽ�ֹ����
	 * ������   date-��ѯ����
	 * 		branchid-����ID
	 * 		type-ҵ������
	 * **/
	
	public Date selectCloseDate(@Param("date")Date date,
								@Param("branchid")String branchId,
								@Param("type")String type);
	
	/**
	 * ���ܣ���ѯ�ͻ��������ҵ���˻�ָ������ʱ�����
	 * ������date-��ѯ����
	 * 	   mangeid-�ͻ�����ID
	 * 	   accounttype-�˻�����
	 * **/
	public List<Map> selectAccountBalAndYearAvgByManagerid( @Param("branchid")String branchId,
															@Param("accountid")String accountId,
															@Param("managerid")String managerId,
															@Param("date")Date date,
															@Param("accounttype")String accounttype);
	/**
	 * ��ѯ��Ʋ�Ʒ�ܱ������ܽ��
	 * **/
	public List<Map>  selectFinanceCnt(@Param("branchid")String branchId,
			   						   @Param("managerid")String managerId,
			   						   @Param("busitype")String busitype,
			   						   @Param("startdate")Date startDate,
			   						   @Param("enddate")Date endDate,
			   						   @Param("productid")String productId ,
			   						   @Param("productbatch")String productBatch,
			   						   @Param("channel")String channel,
			   						   @Param("iszy")String iszy);
	/**
	 * ���ܣ���ѯ�ͻ�������Ʋ�Ʒָ������ʱ�����
	 * ������date-��ѯ����
	 * 	   mangeid-�ͻ�����ID
	 * 	   accounttype-�˻�����
	 * **/
	public List<Map> selectFinanceAccountBal(@Param("managerid")String managerId,
											 @Param("startdate")Date startDate,
											 @Param("enddate")Date endDate,
											 @Param("accounttype")String accounttype);
	

	/**
	 * ���ܣ���ѯ�ͻ�������Ʋ�Ʒ��Ҫ�������վ��˻���Ϣ
	 * ������date-��ѯ����
	 * 	   mangeid-�ͻ�����ID
	 * 	   accounttype-�˻�����
	 * **/
	public List<Map> selectFinanceAcountYearDayAvg(@Param("managerid")String managerId,
												   @Param("startdate")Date startDate,
												   @Param("enddate")Date endDate,
												   @Param("accounttype")String accounttype);
	/**
	 * ��ѯ��Ʋ�Ʒͳ��ʱ��Ӧ��ϸ��¼
	 * **/
	public List<Map>  selectFinanceDetail(@Param("branchid")String branchId,
			  							  @Param("managerid")String managerId,
			  							  @Param("productid")String productId,
			  							  @Param("productbatch")String productBatch,
			  							  @Param("channel")String channel,
			  							  @Param("startdate")Date startDate,
			  							  @Param("enddate")Date endDate,
			  							  @Param("busitype")String busitype,
			  							  @Param("iszy")String iszy);
	/**
	 * ��ѯ��Ʋ�Ʒ�վ���ϸ
	 * **/
	public List<Map>  selectFinanceAvgDetail(@Param("branchid")String branchId,
											 @Param("managerid")String managerId,
											 @Param("startdate")Date startDate,
											 @Param("enddate")Date endDate,
											 @Param("busitype")String busitype);

	/**
	 * ���ܣ���ѯ�ͻ����������Ѻ����˻�
	 * **/
	public List<Map>  selectPersonAccountPledge(@Param("managerid")String managerId,
												@Param("startdate")Date startDate,
												@Param("enddate")Date endDate,
												@Param("accounttype")String accounttype);
	
	public List<Map>  selectPersonPledgeYearDayAvgDetail(@Param("branchid")String branchId,
														 @Param("managerid")String managerId,
														 @Param("accountid")String accountId);
	
	public List<Map>  selectPledgeDetail(@Param("branchid")String branchId,
		     							 @Param("managerid")String managerId,
		     							 @Param("startdate")Date startDate,
		     							 @Param("enddate")Date endDate);	
	
	public List<Map>  selectPledgeCntByManagerId(@Param("branchid")String branchId,
									  @Param("managerid")String managerId,
									  @Param("startdate")Date startDate,
									  @Param("enddate")Date endDate);
	
	public List<Map>  selectPublicDepositChangeList(@Param("branchid")String branchId,
													@Param("accountname")String accountName,
													@Param("startdate")Date startDate,
													@Param("enddate")Date endDate,
													@Param("minnetamt")double minNetAmt);
	
	public List<Map>  selectPublicDepositChangeDetail(@Param("branchid")String branchId,
													  @Param("accountname")String accountName,
													  @Param("startdate")Date startDate,
													  @Param("enddate")Date endDate,
													  @Param("minnetamt")double minNetAmt);

	public List<Map>  selectAccountBalAndYearDayAvg(@Param("gendate")Date date,
				 									@Param("branchid")String branchid,
				 									@Param("managerid")String managerid,
				 									@Param("accountid")String accountId,
				 									@Param("accountname")String accountName,
				 									@Param("customno")String customNo,
				 									@Param("subcode")String subCode,
				 									@Param("property")String property,
				 									@Param("status")String status,
				 									@Param("accounttype")String accounttype);
}
