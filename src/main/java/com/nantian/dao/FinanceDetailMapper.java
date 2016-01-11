package com.nantian.dao;

import com.nantian.domain.FinanceDetail;

public interface FinanceDetailMapper {


	int deleteByPrimaryKey(String saleid);

    int insert(FinanceDetail record);

    int insertSelective(FinanceDetail record);

    FinanceDetail selectByPrimaryKey(String saleid);

    int updateByPrimaryKeySelective(FinanceDetail record);

    int updateByPrimaryKey(FinanceDetail record);
}