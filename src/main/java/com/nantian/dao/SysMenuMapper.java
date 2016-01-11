package com.nantian.dao;

import java.util.List;
import java.util.Map;

import com.nantian.domain.SysMenu;

public interface SysMenuMapper {
    int deleteByPrimaryKey(String menuid);

    int insert(SysMenu record);

    int insertSelective(SysMenu record);

    SysMenu selectByPrimaryKey(String menuid);

    int updateByPrimaryKeySelective(SysMenu record);

    int updateByPrimaryKey(SysMenu record);
    
    List<SysMenu> selectSubMenuListByAccountIdAndParentMenuId(Map<String,String> map);
    
    List<SysMenu> selectSubMenuListByParentMenuId(String parentmenuid);
}