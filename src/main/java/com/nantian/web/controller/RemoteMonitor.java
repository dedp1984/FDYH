package com.nantian.web.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RemoteMonitor
{
	@RequestMapping("/monitor/queryHistoryPath")
	@ResponseBody
	public List<HashMap<String,Double>> queryHistoryPath()
	{
		
		List<HashMap<String,Double>> path=new ArrayList<HashMap<String,Double>>();
		HashMap<String,Double> map1=new HashMap<String,Double>();
		map1.put("longitude", 106.574696);
		map1.put("latitude", 29.515339);
		path.add(map1);
		HashMap<String,Double> map2=new HashMap<String,Double>();
		map2.put("longitude", 106.575226);
		map2.put("latitude",29.51526);
		path.add(map2);
		HashMap<String,Double> map3=new HashMap<String,Double>();
		map3.put("longitude", 106.575522);
		map3.put("latitude", 29.514844);
		path.add(map3);
		HashMap<String,Double> map4=new HashMap<String,Double>();
		map4.put("longitude", 106.575181);
		map4.put("latitude", 29.514482);
		path.add(map4);
		HashMap<String,Double> map5=new HashMap<String,Double>();
		map5.put("longitude", 106.574669);
		map5.put("latitude", 29.514797);
		path.add(map5);
		return path;
	}
}
