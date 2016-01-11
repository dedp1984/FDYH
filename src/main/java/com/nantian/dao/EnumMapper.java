package com.nantian.dao;

import java.util.List;

import com.nantian.domain.Enum;
import com.nantian.domain.EnumKey;

public interface EnumMapper {
    int deleteByPrimaryKey(EnumKey key);

    int insert(Enum record);

    int insertSelective(Enum record);

    Enum selectByPrimaryKey(EnumKey key);

    int updateByPrimaryKeySelective(Enum record);

    int updateByPrimaryKey(Enum record);
    
    List<Enum> selectListByType(String type);
}