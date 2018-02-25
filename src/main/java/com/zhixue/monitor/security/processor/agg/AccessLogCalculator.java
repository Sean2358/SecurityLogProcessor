package com.zhixue.monitor.security.processor.agg;

import com.codahale.metrics.MetricRegistry;
import com.zhixue.monitor.model.log.TomcatAccessLog;

import java.text.SimpleDateFormat;

/**
 * TomcatAccessLog 统计.
 * <p>
 * 
 * <pre>
 * url按分钟、小时、天级的访问次数统计
 * </pre>
 * 
 * @author gfshen 2017年8月9日
 */
public class AccessLogCalculator {

	public static final String minuteDateFormat = "yyyyMMddHHmm";
	public static final String hourDateFormat = "yyyyMMddHH";
	public static final String dayDateFormat = "yyyyMMdd";

	public static MetricRegistry minuteMetric = new MetricRegistry();
	public static MetricRegistry hourMetric = new MetricRegistry();
	public static MetricRegistry dayMetric = new MetricRegistry();
	
	public static final String spliter = "__";
	
	/**
	 * 对一条Log进行聚合计算.
	 * 
	 * @param log
	 *            Log
	 */
	public static void calc(TomcatAccessLog log) {
		minuteMetric.counter(genCounterName(log,minuteDateFormat)).inc();
		hourMetric.counter(genCounterName(log,hourDateFormat)).inc();
		dayMetric.counter(genCounterName(log,dayDateFormat)).inc();
	}
	
	/**
	 * 生成MetricRegistry.Counter的name.
	 * <p>格式：appName_userId_url_formatTime
	 * @param log
	 * @param dateFormat
	 * @return
	 * @Date 2017年8月9日 - 下午7:16:30
	 */
	private static String genCounterName(TomcatAccessLog log, String dateFormat){
		return log.getProducerAppName()+spliter+log.getUserId()+spliter+log.getRequestURL()+spliter+new SimpleDateFormat(dateFormat).format(log.getCreateTime());
	}
	
}
