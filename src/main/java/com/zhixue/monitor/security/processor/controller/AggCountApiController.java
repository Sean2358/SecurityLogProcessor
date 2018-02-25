package com.zhixue.monitor.security.processor.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.Counter;
import com.zhixue.monitor.security.processor.agg.AccessLogCalculator;
import com.zhixue.monitor.security.processor.agg.AggLogFilter;

@RestController
@RequestMapping(value = "/api/Security")
public class AggCountApiController {
	
	/**
	 * 针对前1分钟日志，提供超标统计
	 * @param appNames
	 * @return
	 * @Date 2017年8月14日 - 上午9:36:07
	 */
	@RequestMapping(value = "/minExceed")
	@ResponseBody
	public Object getMinuteExceedRecords(String appNames){
        JSONObject result = new JSONObject();
		if(null == appNames){
			return result;
		}
		List<String> appNameList = Arrays.asList(appNames.split(","));
		JSONArray data = new JSONArray();
		for(String appName:appNameList){
			SortedMap<String, Counter> counters = AccessLogCalculator.minuteMetric.getCounters(AggLogFilter.lastUnitsFilter(appName,TimeUnit.MINUTES,1,true));
			Iterator<Map.Entry<String,Counter>> it = counters.entrySet().iterator();  
	        while(it.hasNext()){
	        	Map.Entry<String,Counter> entry = it.next();  
	        	data.add(entry.getKey()+"|"+entry.getValue().getCount());
	        }
		}
		result.put("data", data);
		return result;
	}
	
	/**
	 * 针对前5分钟日志，统计3次超过限制的访问
	 * @param appNames
	 * @return
	 * @Date 2017年8月17日 - 下午8:22:37
	 */
	@RequestMapping(value = "/fiveMinExceed")
	@ResponseBody
	public Object get5MinExceeds(String appNames){
		JSONObject result = new JSONObject();
		if(null == appNames){
			return result;
		}
		List<String> appNameList = Arrays.asList(appNames.split(","));
		JSONArray data = new JSONArray();
		for(String appName:appNameList){
			SortedMap<String, Counter> counters = AccessLogCalculator.minuteMetric.getCounters(AggLogFilter.lastUnitsFilter(appName,TimeUnit.MINUTES,5,true));
			if (!counters.isEmpty()) {
				Map<String,List<String>> map = counter2Map(counters);
				Iterator<Map.Entry<String,List<String>>> it = map.entrySet().iterator();
				while(it.hasNext()){
		        	Map.Entry<String,List<String>> entry = it.next(); 
		        	if(map.get(entry.getKey()).size() >= 3){
		        		data.add(entry.getKey());
		        	}
		        }
			}
		}
		result.put("data", data);
		return result;
	}
	
	
	/**
	 * 针对前一个小时访问日志，提供超标统计
	 * @param appNames
	 * @return
	 * @Date 2017年8月14日 - 上午9:35:28
	 */
	@RequestMapping(value = "/hourExceed")
	@ResponseBody
	public Object getHourExceeds(String appNames){
		JSONObject result = new JSONObject();
		if(null == appNames){
			return result;
		}
		List<String> appNameList = Arrays.asList(appNames.split(","));
		JSONArray data = new JSONArray();
		for(String appName:appNameList){
			SortedMap<String, Counter> counters = AccessLogCalculator.hourMetric.getCounters(AggLogFilter.lastUnitsFilter(appName,TimeUnit.HOURS,1,true));
			Iterator<Map.Entry<String,Counter>> it = counters.entrySet().iterator();  
	        while(it.hasNext()){
	        	Map.Entry<String,Counter> entry = it.next();  
	        	data.add(entry.getKey()+"|"+entry.getValue().getCount());
	        }
		}
		result.put("data", data);
		return result;
	}
	
	/**
	 * 之前连续2个小时超标
	 * @param appNames
	 * @return
	 * @Date 2017年8月15日 - 下午6:53:26
	 */
	@RequestMapping(value = "/twoHourExceed")
	@ResponseBody
	public Object getTwoHourExceeds(String appNames){
		JSONObject result = new JSONObject();
		if(null == appNames){
			return result;
		}
		List<String> appNameList = Arrays.asList(appNames.split(","));
		JSONArray data = new JSONArray();
		for(String appName:appNameList){
			SortedMap<String, Counter> counters = AccessLogCalculator.hourMetric.getCounters(AggLogFilter.lastUnitsFilter(appName,TimeUnit.HOURS,2,true));
			if (!counters.isEmpty()) {
				Map<String,List<String>> map = counter2Map(counters);
				Iterator<Map.Entry<String,List<String>>> it = map.entrySet().iterator();
				while(it.hasNext()){
		        	Map.Entry<String,List<String>> entry = it.next(); 
		        	//两个小时都超标
		        	if(map.get(entry.getKey()).size() >= 2){
		        		data.add(entry.getKey());
		        	}
		        }
			}
		}
		result.put("data", data);
		return result;
	}
	
	/**
	 * 针对当前自然天访问日志，提供超标统计
	 * @param appNames
	 * @return
	 * @Date 2017年8月14日 - 上午9:35:28
	 */
	@RequestMapping(value = "/dayExceed")
	@ResponseBody
	public Object getDayExceeds(String appNames){
		JSONObject result = new JSONObject();
		if(null == appNames){
			return result;
		}
		List<String> appNameList = Arrays.asList(appNames.split(","));
		JSONArray data = new JSONArray();
		for(String appName:appNameList){
			SortedMap<String, Counter> counters = AccessLogCalculator.dayMetric.getCounters(AggLogFilter.curDayFilter(appName));
			Iterator<Map.Entry<String,Counter>> it = counters.entrySet().iterator();  
	        while(it.hasNext()){
	        	Map.Entry<String,Counter> entry = it.next();  
	        	data.add(entry.getKey()+"|"+entry.getValue().getCount());
	        }
		}
		result.put("data", data);
		return result;
	}
	
	/**
	 * 针对过去24小时，超过8小时访问
	 * @param appNames
	 * @return
	 * @Date 2017年8月14日 - 上午9:33:21
	 */
	@RequestMapping(value = "/dayHourExceed8")
	@ResponseBody
	public Object getDayHourExceed8(String appNames){
		JSONObject result = new JSONObject();
		if(null == appNames){
			return result;
		}
		List<String> appNameList = Arrays.asList(appNames.split(","));
		JSONArray data = new JSONArray();
		for(String appName:appNameList){
			SortedMap<String, Counter> counters = AccessLogCalculator.hourMetric.getCounters(AggLogFilter.lastUnitsFilter(appName,TimeUnit.HOURS,24,false));
			if (!counters.isEmpty()) {
				Map<String,List<String>> map = counter2Map(counters);
				//记录counters中超过8次的appName_user_url
				Iterator<Map.Entry<String,List<String>>> it = map.entrySet().iterator();
				while(it.hasNext()){
		        	Map.Entry<String,List<String>> entry = it.next(); 
		        	if(map.get(entry.getKey()).size() >= 8){
		        		data.add(entry.getKey());
		        	}
		        }
			}
		}
		result.put("data", data);
		return result; 
	}
	
	/**
	 * 针对过去24小时，超过12小时访问
	 * @param appNames
	 * @return
	 * @Date 2017年8月14日 - 上午9:33:21
	 */
	@RequestMapping(value = "/dayHourExceed12")
	@ResponseBody
	public Object getDayHourExceed12(String appNames){
		JSONObject result = new JSONObject();
		if(null == appNames){
			return result;
		}
		List<String> appNameList = Arrays.asList(appNames.split(","));
		JSONArray data = new JSONArray();
		for(String appName:appNameList){
			SortedMap<String, Counter> counters = AccessLogCalculator.hourMetric.getCounters(AggLogFilter.lastUnitsFilter(appName,TimeUnit.HOURS,24,false));
			if (!counters.isEmpty()) {
				Map<String,List<String>> map = counter2Map(counters);
				//记录counters中超过12次的appName_user_url
				Iterator<Map.Entry<String,List<String>>> it = map.entrySet().iterator();
				while(it.hasNext()){
		        	Map.Entry<String,List<String>> entry = it.next(); 
		        	if(map.get(entry.getKey()).size() >= 12){
		        		data.add(entry.getKey());
		        	}
		        }
			}
		}
		result.put("data", data);
		return result; 
	}
	
	/**
	 * 针对凌晨访问日志，提供超标统计
	 * @param appNames
	 * @return
	 * @Date 2017年8月14日 - 上午9:29:33
	 */
	@RequestMapping(value = "/nightExceed")
	@ResponseBody
	public Object getNightHourExceed(String appNames){
		JSONObject result = new JSONObject();
		if(null == appNames){
			return result;
		}
		List<String> appNameList = Arrays.asList(appNames.split(","));
		JSONArray data = new JSONArray();
		for(String appName:appNameList){
			SortedMap<String, Counter> counters = AccessLogCalculator.hourMetric.getCounters(AggLogFilter.curNightFilter(appName));
			if (!counters.isEmpty()) {
				Map<String,List<String>> map = counter2Map(counters);
				//记录counters中0-6点超过3次的formatTime
				Iterator<Map.Entry<String,List<String>>> it = map.entrySet().iterator();
				while(it.hasNext()){
		        	Map.Entry<String,List<String>> entry = it.next(); 
		        	if(map.get(entry.getKey()).size() >= 3){
		        		data.add(entry.getKey());
		        	}
		        }
			}
		}
		result.put("data", data);
		return result; 
	}
	
	/**
	 * Counter转换成Map.
	 * 
	 * @param counters
	 * @return
	 * <pre>key:appName_userId_url_</pre>
	 * <pre>value:"date,count"为内容的List</pre>
	 * @Date 2017年8月18日 - 上午9:25:12
	 */
	private Map<String, List<String>> counter2Map(SortedMap<String, Counter> counters){
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		Iterator<Map.Entry<String,Counter>> it = counters.entrySet().iterator();
        while(it.hasNext()){
        	Map.Entry<String,Counter> entry = it.next();
        	String nameKey = entry.getKey();
        	String key = nameKey.substring(0, nameKey.lastIndexOf(AccessLogCalculator.spliter));
        	String date = nameKey.split(AccessLogCalculator.spliter)[3];
        	if(null != map.get(key)){
        		map.get(key).add(date);
        	}else{
        		List<String> records = new ArrayList<String>();
				records.add(date);
				map.put(key, records);
        	}
        }
		return map;
	}
	
}
