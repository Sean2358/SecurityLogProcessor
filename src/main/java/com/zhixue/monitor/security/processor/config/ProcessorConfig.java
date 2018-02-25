package com.zhixue.monitor.security.processor.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProcessorConfig {

    public static Map<String, String> tomcatRequestFilter = new HashMap<String, String>();

    static {
        Properties props = new Properties();
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("consumer.properties"));
        } catch (IOException e) {
            throw new RuntimeException("consumer.properties load failed.");
        }
        String filterString = props.getProperty("tomcat.request.filter", "");
        String[] filters = filterString.split("\\|");
        for (String filter : filters) {
            if (filter.trim().isEmpty()) {
                continue;
            }
            tomcatRequestFilter.put(filter.trim(), filter.trim());
        }

    }


}
