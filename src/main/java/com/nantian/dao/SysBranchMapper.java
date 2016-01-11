package com.nantian.dao;

import java.util.List;

import com.nantian.domain.SysBranch;

public interface SysBranchMapper {
    int deleteByPrimaryKey(String branchid);

    int insert(SysBranch record);

    int insertSelective(SysBranch record);

    SysBranch selectByPrimaryKey(String branchid);

    int updateByPrimaryKeySelective(SysBranch record);

    int updateByPrimaryKey(SysBranch record);
    
    List<SysBranch> selectSubBranchList(String parentid);
}