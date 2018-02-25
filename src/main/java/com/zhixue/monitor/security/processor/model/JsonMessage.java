package com.zhixue.monitor.security.processor.model;

import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * JsonMessage.
 * <p>用于映射kafka中的消息。
 * @author znyin
 */
public class JsonMessage {
	/**
	 * 头信息。
	 */
	private Map<String,String> headers;
	
	/**
	 * 内容。
	 */
	private String content;
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
}
