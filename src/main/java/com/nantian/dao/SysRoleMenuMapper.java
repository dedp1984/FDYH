package com.nantian.dao;

import com.nantian.domain.SysRoleMenuKey;

public interface SysRoleMenuMapper {
    int deleteByPrimaryKey(SysRoleMenuKey key);

    int insert(SysRoleMenuKey record);

    int insertSelective(SysRoleMenuKey record);
    
    SysRoleMenuKey selectByPrimaryKey(SysRoleMenuKey key);
    
    int deleteByRoleId(String roleid);
}