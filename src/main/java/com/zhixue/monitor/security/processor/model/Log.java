package com.zhixue.monitor.security.processor.model;

import java.io.Serializable;
import java.util.Date;

import com.zhixue.monitor.model.log.DubboInvokePerfLog;
import com.zhixue.monitor.model.log.PerfLog;
import com.zhixue.monitor.model.log.TomcatAccessLog;




public class Log implements Serializable{
	
	/**
	 */
	private static final long serialVersionUID = -1L;

	/**
	 * 日志类型
	 */
	private String logType;

	/**
	 * 日志产生者所在机器名称
	 */
	private String producerMachineName;
	
	/**
	 * 日志产生者所属应用名称
	 */
	private String producerAppName;
	
	/**
	 * 日志产生者标识。
	 * <p>一般为端口号，用于区分同一机器上同一应用部署多份。
	 */
	private String producerId;
	
	/**
	 * 日志链标识
	 */
	private String traceId;
	
	/**
	 * 操作描述
	 */
	private String opdesc;
	
	/**
	 * 耗时
	 */
	private long duration;
	
	/**
	 * 
	 * 日志创建时间戳
	 */
	private Date createTime;
	
	
	public Log(DubboInvokePerfLog log){
		super();
		this.logType=DubboInvokePerfLog.LOG_TYPE;
		this.producerMachineName =log.getProducerMachineName();
		this.producerAppName = log.getProducerAppName();
		this.producerId =log.getProducerId();
		this.traceId=log.getTraceId();
		this.opdesc = log.getOpdesc();
		this.duration = log.getDuration();
		this.createTime=log.getCreateTime();
		
	}
		public Log(PerfLog log){
		super();
		this.logType=PerfLog.LOG_TYPE;
		this.producerMachineName =log.getProducerMachineName();
		this.producerAppName = log.getProducerAppName();
		this.producerId =log.getProducerId();
		this.traceId=log.getTraceId();
		this.opdesc = log.getOpdesc();
		this.duration = log.getDuration();
		
	}
	
	public Log(TomcatAccessLog log){
		super();
		this.logType=TomcatAccessLog.LOG_TYPE;
		this.producerMachineName =log.getProducerMachineName();
		this.producerAppName = log.getProducerAppName();
		this.producerId =log.getProducerId();
		this.traceId=log.getTraceId();
		this.opdesc = log.getRequestURL();
		this.duration = log.getDuration();
		this.createTime=log.getCreateTime();
	}
	

	/** 
	 * @param @param logType
	 * @param @param producerMachineName
	 * @param @param producerAppName
	 * @param @param producerId
	 * @param @param opdesc
	 * @param @param duration    设定文件 
	 * @throws 
	 */ 
	public Log(String logType, String producerMachineName,
			String producerAppName, String producerId, String opdesc,
			long duration)
	{
		super();
		this.logType = logType;
		this.producerMachineName = producerMachineName;
		this.producerAppName = producerAppName;
		this.producerId = producerId;
		this.opdesc = opdesc;
		this.duration = duration;
	}

	/**
	 * logType.
	 *
	 * @return  the logType
	 */
	public String getLogType() {
		return this.logType;
	}

	/**
	 * logType.
	 *
	 * @param   logType    the logType to set
	 */
	public void setLogType(String logType) {
		this.logType = logType;
	}

	/**
	 * producerMachineName.
	 *
	 * @return  the producerMachineName
	 */
	public String getProducerMachineName() {
		return this.producerMachineName;
	}

	/**
	 * producerMachineName.
	 *
	 * @param   producerMachineName    the producerMachineName to set
	 */
	public void setProducerMachineName(String producerMachineName) {
		this.producerMachineName = producerMachineName;
	}

	/**
	 * producerAppName.
	 *
	 * @return  the producerAppName
	 */
	public String getProducerAppName() {
		return this.producerAppName;
	}

	/**
	 * producerAppName.
	 *
	 * @param   producerAppName    the producerAppName to set
	 */
	public void setProducerAppName(String producerAppName) {
		this.producerAppName = producerAppName;
	}

	/**
	 * producerId.
	 *
	 * @return  the producerId
	 */
	public String getProducerId() {
		return this.producerId;
	}

	/**
	 * producerId.
	 *
	 * @param   producerId    the producerId to set
	 */
	public void setProducerId(String producerId) {
		this.producerId = producerId;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	/**
	 * opdesc.
	 *
	 * @return  the opdesc
	 */
	public String getOpdesc() {
		return this.opdesc;
	}

	/**
	 * opdesc.
	 *
	 * @param   opdesc    the opdesc to set
	 */
	public void setOpdesc(String opdesc) {
		this.opdesc = opdesc;
	}

	/**
	 * duration.
	 *
	 * @return  the duration
	 */
	public long getDuration() {
		return this.duration;
	}

	/**
	 * duration.
	 *
	 * @param   duration    the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}

