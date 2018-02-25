/**
 * Copyright 2016 Iflytek, Inc. All rights reserved.
 */
package com.zhixue.monitor.security.processor.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.iflytek.edu.elp.common.exception.ELPBizException;
import com.iflytek.edu.elp.common.exception.ELPSysException;

/**
 * <p>
 * <code>ControllerExceptionResolver</code>处理系统异常
 * </p>
 *
 * @author yhwang7
 * @since 1.0
 * @version 1.0
 */
public class ControllerExceptionResolver implements HandlerExceptionResolver{

	/**   
	 * @see org.springframework.web.servlet.HandlerExceptionResolver#resolveException(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)    
	 */  
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		if(ex instanceof ELPBizException){
			throw (ELPBizException)ex;
		}
		throw new ELPSysException("系统出现未知错误，请与管理员联系。",ex);
	}

}
