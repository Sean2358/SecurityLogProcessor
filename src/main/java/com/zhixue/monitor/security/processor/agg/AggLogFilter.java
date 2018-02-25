package com.zhixue.monitor.security.processor.agg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.github.diamond.client.PropertiesConfigurationFactoryBean;

public class AggLogFilter {

	private static Logger logger = LoggerFactory.getLogger(AggLogFilter.class);
	
	/**
	 * 前span个时间段数据过滤器
	 * @param appName	应用名
	 * @param unit		minute | hour
	 * @param span		时间单位数
	 * @return
	 * @Date 2017年8月17日 - 下午5:36:04
	 */
	public static MetricFilter lastUnitsFilter(String appName, TimeUnit unit, int span, Boolean useCount) {
		return new lastUnitsCountFilter(appName,unit,span,useCount) ;
	}
	
	/**
	 * 提取前span时间单位符合条件的记录
	 * @author gfshen 2017年8月17日
	 */
	static class lastUnitsCountFilter implements MetricFilter {
		private String appName = "";
		private TimeUnit unit = null;
		private Integer span = 1;
		private  Boolean useCount = true;
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(AccessLogCalculator.minuteDateFormat);
		
		public lastUnitsCountFilter(String appName,TimeUnit unit,int span,Boolean useCount){
			this.appName = appName;
			this.unit = unit;
			this.span = span;
			this.useCount = useCount;
		};
		@Override
		public boolean matches(String name, Metric metric) {
			String[] keys = name.split(AccessLogCalculator.spliter);
			//不是所选应用的数据，忽略
			if(!keys[0].equals(appName)){
				return false;
			}
			try {
				Calendar temp = Calendar.getInstance();
				temp.setTime(c.getTime());
				if(TimeUnit.MINUTES.equals(unit)){
					temp.add(Calendar.MINUTE, -(span+1));
				}else if(TimeUnit.HOURS.equals(unit)){
					temp.add(Calendar.HOUR, -(span+1));
					sdf = new SimpleDateFormat(AccessLogCalculator.hourDateFormat);
				}
				//是当前单位时间，比如当前分钟，忽略
				if(sdf.format(c.getTime()).equals(keys[3])){
					return false;
				}
				//前span个时间单位以前的数据，忽略
				if (sdf.parse(keys[3]).before(temp.getTime())) {
					return false;
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return false;
			}
            Counter counter = (Counter) metric;
            Integer limitCount = null;
            if(useCount) {
                //使用阀值条件时，如果未达对应阀值，忽略
                limitCount = getUrlLimitCount(keys[0], keys[2], unit.toString());
            }else{
                //不使用阀值条件时，统一使用分钟级的阀值半数作为新阀值。减少误杀
                limitCount = getUrlLimitCount(keys[0], keys[2], TimeUnit.MINUTES.toString()) / 2 + 1;
            }
            if (null == limitCount) {
                return false;
            }
            if (counter.getCount() < limitCount) {
                return false;
            }
			return true;
		}
	}
	
	private static Integer getUrlLimitCount(String appName,String url,String unit){
		String cfgKey = String.format("JCXT.sec.%s.%s",appName, url);
		String cfgValue = PropertiesConfigurationFactoryBean.getPropertiesConfiguration().getString(cfgKey);
		if(null == cfgValue){
			return null;
		}
		String[] thresholds = cfgValue.split(",");
		Integer threshold = null;
		if(unit.equals(TimeUnit.MINUTES.toString())){
			threshold = Integer.parseInt(thresholds[0]);
		}else if(unit.equals(TimeUnit.HOURS.toString())){
			threshold = Integer.parseInt(thresholds[1]);
		}else if(unit.equals(TimeUnit.DAYS.toString())){
			threshold = Integer.parseInt(thresholds[2]);
		}
		return threshold;
	}
	
	/**
	 * 当前自然天数据过滤器
	 * @param appName
	 * @return
	 * @Date 2017年8月17日 - 下午5:39:24
	 */
	public static MetricFilter curDayFilter(String appName) {
		return new curDayCountFilter(appName) ;
	}
	
	/**
	 * 提取当前自然天符合条件的记录
	 * @author gfshen 2017年8月17日
	 */
	static class curDayCountFilter implements MetricFilter {
		private String appName = "";
		public curDayCountFilter(String appName){
			this.appName = appName;
		};
		@Override
		public boolean matches(String name, Metric metric) {
			String[] keys = name.split(AccessLogCalculator.spliter);
			//不是所选应用的数据，忽略
			if(!keys[0].equals(appName)){
				return false;
			}
			//不是当前天的数据，忽略
			String today = new SimpleDateFormat(AccessLogCalculator.dayDateFormat).format(new Date());
			if(!today.equals(keys[3])){
				return false;
			}
			//未达阀值，忽略
			Counter counter = (Counter) metric;
			Integer limitCount = getUrlLimitCount(keys[0],keys[2],TimeUnit.DAYS.toString());
			if(null == limitCount){
				return false;
			}
			if(counter.getCount() < limitCount){
				return false;
			}
			return true;
		}
	}
	
	/**
	 * 统计当前天0-6点的访问记录
	 * @param appName
	 * @return
	 * @Date 2017年8月18日 - 上午10:00:27
	 */
	public static MetricFilter curNightFilter(String appName) {
		return new curNightCountFilter(appName) ;
	}
	
	static class curNightCountFilter implements MetricFilter {
		private String appName = "";
		Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(AccessLogCalculator.hourDateFormat);
		
		public curNightCountFilter(String appName){
			this.appName = appName;
			
			start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            
			end.set(Calendar.HOUR_OF_DAY, 6);
            end.set(Calendar.MINUTE, 0);
            end.set(Calendar.SECOND, 0);
		};
		@Override
		public boolean matches(String name, Metric metric) {
			String[] keys = name.split(AccessLogCalculator.spliter);
			//不是所选应用的数据，忽略
			if(!keys[0].equals(appName)){
				return false;
			}
            try {
            	//不是当天0-6点的数据，忽略
            	Date zero = start.getTime();
            	Date six = end.getTime();
				Date date = sdf.parse(keys[3]);
				if(date.getTime() < zero.getTime() || date.getTime() > six.getTime()){
					return false;
				}
			} catch (ParseException e) {
				logger.error("字符串转时间发生异常", e);
				return false;
			}		
			return true;
		}
	}


}
