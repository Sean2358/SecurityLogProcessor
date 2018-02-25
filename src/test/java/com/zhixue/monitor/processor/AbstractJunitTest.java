/**
 * Copyright 2016 Iflytek, Inc. All rights reserved.
 */
package com.zhixue.monitor.processor;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.diamond.client.PropertiesConfiguration;

/**
 * <p>
 * <code>AbstractJunitTest</code>
 * </p>
 *
 * @author yhwang7
 * @since 1.0
 * @version 1.0
 */
@ContextConfiguration(locations = { "classpath:config/applicationContext_*.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractJunitTest extends
		AbstractJUnit4SpringContextTests {
	@BeforeClass
	public static void setup() {
		PropertiesConfiguration configuration = new PropertiesConfiguration();
		// 加载配置文件
		configuration.filterConfig("logback.xml",
				"consumer.properties", "kafka.properties",
				"application.properties",
				"config/applicationContext_service.xml");
		PropertiesConfiguration.loadLogback("logback.xml");
	}
}