package com.zhixue.monitor.security.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.github.diamond.client.PropertiesConfiguration;
import com.zhixue.monitor.security.processor.agg.AggMetricCleaner;

@SpringBootApplication
public class MonitorProcessorBootstrap {

	private static Logger logger = LoggerFactory.getLogger(MonitorProcessorBootstrap.class);
	public static ApplicationContext context = null;
	private static Consumer extractor = null;

	/**
	 * main入口.
	 * 
	 * @param args
	 *            请求参数
	 */
	public static void main(String[] args) {
		logger.warn("starting...");
		try {
			if (args.length == 1 && args[0].equals("start")) {
				PropertiesConfiguration configuration = new PropertiesConfiguration();
				configuration.filterConfig("kafka.properties", "application.properties");

				context = SpringApplication.run(MonitorProcessorBootstrap.class, args);
				// 提取kafka数据
				extractor = new Consumer();
				extractor.run();
				AggMetricCleaner.clean();
				logger.warn("start successfully");
			} else {
				logger.warn("请输入启动参数： start");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	static {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (context != null) {
					logger.info("关闭Context,释放资源.");
				}
				if (extractor != null) {
					logger.info("关闭extractor,释放资源.");
					extractor.shutdown();
					extractor = null;
				}
			}
		});
	}

}
