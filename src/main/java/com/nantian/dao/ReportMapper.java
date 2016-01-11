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
	 * 功能：根据查询日期+机构+业务类型查询数据截止日期
	 * 参数：   date-查询日期
	 * 		branchid-机构ID
	 * 		type-业务类型
	 * **/
	
	public Date selectCloseDate(@Param("date")Date date,
								@Param("branchid")String branchId,
								@Param("type")String type);
	
	/**
	 * 功能：查询客户经理个人业务账户指定日期时点余额
	 * 参数：date-查询日期
	 * 	   mangeid-客户经理ID
	 * 	   accounttype-账户类型
	 * **/
	public List<Map> selectAccountBalAndYearAvgByManagerid( @Param("branchid")String branchId,
															@Param("accountid")String accountId,
															@Param("managerid")String managerId,
															@Param("date")Date date,
															@Param("accounttype")String accounttype);
	/**
	 * 查询理财产品总笔数、总金额
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
	 * 功能：查询客户经理理财产品指定日期时点余额
	 * 参数：date-查询日期
	 * 	   mangeid-客户经理ID
	 * 	   accounttype-账户类型
	 * **/
	public List<Map> selectFinanceAccountBal(@Param("managerid")String managerId,
											 @Param("startdate")Date startDate,
											 @Param("enddate")Date endDate,
											 @Param("accounttype")String accounttype);
	

	/**
	 * 功能：查询客户经理理财产品需要计算年日均账户信息
	 * 参数：date-查询日期
	 * 	   mangeid-客户经理ID
	 * 	   accounttype-账户类型
	 * **/
	public List<Map> selectFinanceAcountYearDayAvg(@Param("managerid")String managerId,
												   @Param("startdate")Date startDate,
												   @Param("enddate")Date endDate,
												   @Param("accounttype")String accounttype);
	/**
	 * 查询理财产品统计时对应明细记录
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
	 * 查询理财产品日均明细
	 * **/
	public List<Map>  selectFinanceAvgDetail(@Param("branchid")String branchId,
											 @Param("managerid")String managerId,
											 @Param("startdate")Date startDate,
											 @Param("enddate")Date endDate,
											 @Param("busitype")String busitype);

	/**
	 * 功能：查询客户经理个人质押存款账户
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
