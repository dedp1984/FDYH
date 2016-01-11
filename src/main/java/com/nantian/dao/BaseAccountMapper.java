package com.nantian.dao;

import java.util.List;
import java.util.Map;

import com.nantian.domain.BaseAccount;

public interface BaseAccountMapper {

	int deleteByPrimaryKey(String accountid);

    int insert(BaseAccount record);

    int insertSelective(BaseAccount record);

    BaseAccount selectByPrimaryKey(String accountid);

    int updateByPrimaryKeySelective(BaseAccount record);

    int updateByPrimaryKey(BaseAccount record);
    
    List<BaseAccount> selectBaseAccountList(Map map);
}