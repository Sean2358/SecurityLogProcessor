package com.zhixue.monitor.security.processor.process;

import com.github.diamond.client.PropertiesConfigurationFactoryBean;
import com.zhixue.monitor.model.log.TomcatAccessLog;
import com.zhixue.monitor.security.processor.agg.AccessLogCalculator;
import com.zhixue.monitor.security.processor.config.ProcessorConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Tomcat访问日志的日志消息处理器。
 * <p>
 * 
 * <pre>
 * 消费的 topic为zx_TomcatAccessLog.
 * 能处理Tomcat访问日志。
 * </pre>
 * 
 * @author znyin,zhangkaixuan
 *
 */
public class TomcatAccessLogProcessor implements IProcessor {

	@Override
	public void process(String msg) {
		try {
			TomcatAccessLog accessLog = new TomcatAccessLog(msg);
			String url = accessLog.getRequestURL();
			if (url != null && url.lastIndexOf(".") != -1) {
				String ext = url.substring(url.lastIndexOf("."));
				if (ProcessorConfig.tomcatRequestFilter.containsKey(ext)) {
					return ;
				}
			}
            if(isWhiteListUser(accessLog.getUserId())){
                return;
            }
            String ip = accessLog.getRemoteIP();
            if(StringUtils.isNotEmpty(ip)){
                if(isWhiteListIp(ip.split(",")[0])){
                    return;
                }
            }
            String cfgKey = String.format("JCXT.sec.%s.%s",accessLog.getProducerAppName(),url);
            String cfgValue = PropertiesConfigurationFactoryBean.getPropertiesConfiguration().getString(cfgKey);
            if(null != cfgValue && 403 != accessLog.getStatus()){
                AccessLogCalculator.calc(accessLog);
            }
		} catch (Exception e) {
			throw new RuntimeException("msg process failed.msg:" + msg, e);
		}
	}

    /**
     * 判断用户是否在白名单
     * @param userId 用户id
     * @return true/false
     */
    private boolean isWhiteListUser(String userId){
        String whiteList = PropertiesConfigurationFactoryBean.getPropertiesConfiguration().getString("JCXT.sec.processor.whiteList");
        List<String> whiteListUsers = Arrays.asList(whiteList.split(","));
        return whiteListUsers.contains(userId);
    }

    /**
     * 判断ip是否在白名单
     * @param ip 用户ip
     * @return true/false
     */
    private boolean isWhiteListIp(String ip){
        String whiteList = PropertiesConfigurationFactoryBean.getPropertiesConfiguration().getString("JCXT.sec.processor.whiteList.ip");
        List<String> whiteListIPs = Arrays.asList(whiteList.split(","));
        return whiteListIPs.contains(ip);
    }

}