package com.nantian.dao;

import java.util.HashMap;
import java.util.List;

import com.nantian.domain.FinanceProductType;

public interface FinanceProductTypeMapper {
    int deleteByPrimaryKey(String id);

    int insert(FinanceProductType record);

    int insertSelective(FinanceProductType record);

    FinanceProductType selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FinanceProductType record);

    int updateByPrimaryKey(FinanceProductType record);
    
    List<FinanceProductType> selectListByName(HashMap map);
}