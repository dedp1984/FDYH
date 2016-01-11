package com.nantian.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nantian.service.EnumService;

@Controller
public class EnumController
{
	@Resource
	private EnumService enumService;
	
	@RequestMapping("/enum/queryEnumList")
	@ResponseBody
	public List<com.nantian.domain.Enum> queryEnumList(String type)
	{
		return enumService.queryEnumList(type);
	}
}
