package com.nantian.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.nantian.domain.AccountBalRecord;
import com.nantian.domain.AccountBalRecordKey;

public interface AccountBalRecordMapper {

	int deleteByPrimaryKey(AccountBalRecordKey key);

    int insert(AccountBalRecord record);

    int insertSelective(AccountBalRecord record);

    AccountBalRecord selectByPrimaryKey(AccountBalRecordKey key);

    int updateByPrimaryKeySelective(AccountBalRecord record);

    int updateByPrimaryKey(AccountBalRecord record);
    
    int deleteImportedData(@Param("gendate")Date importDate,
						 @Param("accounttype")String accountType,
						 @Param("branchid")String branchId); 
}