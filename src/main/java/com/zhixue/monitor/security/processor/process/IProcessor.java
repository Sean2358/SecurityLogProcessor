package com.zhixue.monitor.security.processor.process;

/**
 * kafka日志消息处理器接口。
 * <p>用于处理收集到kafka中的每一条日志消息。
 * @author znyin
 *
 */
public interface IProcessor {
	
	/**
	 * 处理日志消息。
	 * @param msg 日志消息
	 */
	void process(String msg);

}
