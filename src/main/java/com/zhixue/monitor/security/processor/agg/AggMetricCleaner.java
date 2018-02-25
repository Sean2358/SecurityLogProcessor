package com.zhixue.monitor.security.processor.agg;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;

/**
 * 清除过期的metric数据.
 * <p>
 * <pre>分钟级，每分钟执行，清除5分钟以前</pre>
 * <pre>小时级，每小时执行，清除24小时以前</pre>
 * <pre>天极级，每天执行，清除2天以前</pre>
 * @author gfshen 2017年8月10日
 */
public class AggMetricCleaner {

	private static Logger logger = LoggerFactory.getLogger(AggMetricCleaner.class);
	
	/**
	 * 线程数量
	 */
	private static int poolSize = 3;

	/**
	 * 保留不清除的时间段
	 */
	private static int minBefore = 6;
	private static int hourBefore = 24;
	private static int dayBefore = 2;
	
	static {
		Properties props = new Properties();
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("consumer.properties"));
		} catch (IOException e) {
			throw new RuntimeException("consumer.properties load failed.");
		}
		minBefore = Integer.parseInt(props.getProperty("clean.beforeNow.minute.aggLog", String.valueOf(minBefore)));
		hourBefore = Integer.parseInt(props.getProperty("clean.beforeNow.hour.aggLog", String.valueOf(hourBefore)));
		dayBefore = Integer.parseInt(props.getProperty("clean.beforeNow.day.aggLog", String.valueOf(dayBefore)));
	}
	
	public static void clean() {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize);
		executor.scheduleAtFixedRate(new CleanMinAggLog(), 0, 1, TimeUnit.MINUTES);
		executor.scheduleAtFixedRate(new CleanHourAggLog(), 0, 1, TimeUnit.HOURS);
		executor.scheduleAtFixedRate(new CleanDayAggLog(), 0, 1, TimeUnit.DAYS);
	}
	
	/**
	 * 分钟日志清除
	 * @author gfshen 2017年8月10日
	 */
	private static class CleanMinAggLog implements Runnable {
		@Override
		public void run() {
			try {
				AccessLogCalculator.minuteMetric.removeMatching(new CleanFilter(TimeUnit.MINUTES.toString(),minBefore));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 小时日志清除
	 * @author gfshen 2017年8月10日
	 */
	private static class CleanHourAggLog implements Runnable {
		@Override
		public void run() {
			try {
				AccessLogCalculator.hourMetric.removeMatching(new CleanFilter(TimeUnit.HOURS.toString(),hourBefore));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 天极日志清理
	 * @author gfshen 2017年8月10日
	 */
	private static class CleanDayAggLog implements Runnable {
		@Override
		public void run() {
			try {
				AccessLogCalculator.dayMetric.removeMatching(new CleanFilter(TimeUnit.DAYS.toString(),dayBefore));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 过期日志过滤器.
	 * <p><pre>可按照分、时、天清除</pre>
	 * @author gfshen 2017年8月10日
	 */
	private static class CleanFilter implements MetricFilter {
		
		private String level = "";
		private Integer span = 0;
		
		public CleanFilter(String level,int span){
			this.level = level;
			this.span = span;
		}
		
		@Override
		public boolean matches(String name, Metric metric) {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			SimpleDateFormat sdf = null;
			if(TimeUnit.MINUTES.toString().equalsIgnoreCase(level)){
				c.add(Calendar.MINUTE, -span);
				sdf = new SimpleDateFormat(AccessLogCalculator.minuteDateFormat);
			}else if(TimeUnit.HOURS.toString().equalsIgnoreCase(level)){
				c.add(Calendar.HOUR, -span);
				sdf = new SimpleDateFormat(AccessLogCalculator.hourDateFormat);
			}else if(TimeUnit.DAYS.toString().equalsIgnoreCase(level)){
				c.add(Calendar.DAY_OF_YEAR, -span);
				sdf = new SimpleDateFormat(AccessLogCalculator.dayDateFormat);
			}else{
				return false;
			}
			
			try {
				String[] keys = name.split(AccessLogCalculator.spliter);
				if (sdf.parse(keys[3]).before(c.getTime())) {
					return true;
				}
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
			}
			return false;
		}
	}

	
}
