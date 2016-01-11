package com.nantian.dao;

import java.util.List;
import java.util.Map;

import com.nantian.domain.SysRole;

public interface SysRoleMapper {
    int deleteByPrimaryKey(String roleid);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(String roleid);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);
    
    List<SysRole> selectRoleList(SysRole record);
    
    int insertAutoKey(Map<String,String> map);
}