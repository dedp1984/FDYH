package com.nantian.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.nantian.dao.EnumMapper;

import com.nantian.domain.Enum;
@Service
public class EnumService
{
	@Resource
	private EnumMapper enumMapper;
	
	public List<Enum> queryEnumList(String type)
	{
		return enumMapper.selectListByType(type);
	}
}
