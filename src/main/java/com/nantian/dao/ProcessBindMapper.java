package com.nantian.dao;

import org.apache.ibatis.annotations.Param;

import com.nantian.domain.ProcessBind;
import com.nantian.domain.ProcessBindKey;

public interface ProcessBindMapper {
    int deleteByPrimaryKey(ProcessBindKey key);

    int insert(ProcessBind record);

    int insertSelective(ProcessBind record);

    ProcessBind selectByPrimaryKey(ProcessBindKey key);

    int updateByPrimaryKeySelective(ProcessBind record);

    int updateByPrimaryKey(ProcessBind record);
    
    int deleteByAccountId(@Param("accountid")String accountid);
}