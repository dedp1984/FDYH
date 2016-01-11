package com.nantian.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.nantian.domain.PublicDepositChange;
import com.nantian.domain.PublicDepositChangeKey;

public interface PublicDepositChangeMapper {
    int deleteByPrimaryKey(PublicDepositChangeKey key);

    int insert(PublicDepositChange record);

    int insertSelective(PublicDepositChange record);

    PublicDepositChange selectByPrimaryKey(PublicDepositChangeKey key);

    int updateByPrimaryKeySelective(PublicDepositChange record);

    int updateByPrimaryKey(PublicDepositChange record);
    
    int deleteByGendate(@Param("gendate")Date gendate);
}