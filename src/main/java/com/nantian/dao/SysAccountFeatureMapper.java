package com.nantian.dao;

import java.util.List;

import com.nantian.domain.SysAccountFeatureKey;

public interface SysAccountFeatureMapper {
    int deleteByPrimaryKey(SysAccountFeatureKey key);

    int insert(SysAccountFeatureKey record);

    int insertSelective(SysAccountFeatureKey record);
    
    int deleteBySelective(SysAccountFeatureKey record);
    
    List<SysAccountFeatureKey> selectBySelective(SysAccountFeatureKey record);
}