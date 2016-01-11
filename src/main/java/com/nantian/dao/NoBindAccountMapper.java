package com.nantian.dao;

import java.util.List;
import java.util.Map;

import com.nantian.domain.NoBindAccount;

public interface NoBindAccountMapper {
    int deleteByPrimaryKey(String accountid);

    int insert(NoBindAccount record);

    int insertSelective(NoBindAccount record);

    NoBindAccount selectByPrimaryKey(String accountid);

    int updateByPrimaryKeySelective(NoBindAccount record);

    int updateByPrimaryKey(NoBindAccount record);
    
    List<NoBindAccount> selectByBranchId(Map map);
}