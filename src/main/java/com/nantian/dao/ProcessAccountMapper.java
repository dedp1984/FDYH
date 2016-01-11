package com.nantian.dao;

import java.util.List;
import java.util.Map;

import com.nantian.domain.ProcessAccount;

public interface ProcessAccountMapper {
    int deleteByPrimaryKey(String accountid);

    int insert(ProcessAccount record);

    int insertSelective(ProcessAccount record);

    ProcessAccount selectByPrimaryKey(String accountid);

    int updateByPrimaryKeySelective(ProcessAccount record);

    int updateByPrimaryKey(ProcessAccount record);
    
    List<ProcessAccount> selectUnCheckAccountList(Map map);
}