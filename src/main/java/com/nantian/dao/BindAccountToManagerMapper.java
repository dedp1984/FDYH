package com.nantian.dao;

import org.apache.ibatis.annotations.Param;

import com.nantian.domain.BindAccountToManager;
import com.nantian.domain.BindAccountToManagerKey;
import com.sun.org.glassfish.gmbal.ParameterNames;

public interface BindAccountToManagerMapper {
    int deleteByPrimaryKey(BindAccountToManagerKey key);

    int insert(BindAccountToManager record);

    int insertSelective(BindAccountToManager record);

    BindAccountToManager selectByPrimaryKey(BindAccountToManagerKey key);

    int updateByPrimaryKeySelective(BindAccountToManager record);

    int updateByPrimaryKey(BindAccountToManager record);
    
    int updateNewManagerByPrimaryKey(@Param("accountid")String accountid,@Param("oldmanagerid")String oldManagerid,@Param("newmanagerid")String newManagerid);
    
    int deleteByAccountId(String accountid);
    
    int selectCntByManagerId(@Param("managerid")String managerid);
}