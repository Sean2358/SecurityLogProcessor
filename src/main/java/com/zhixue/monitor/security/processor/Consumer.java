package com.zhixue.monitor.security.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.zhixue.monitor.security.processor.model.JsonMessage;
import com.zhixue.monitor.security.processor.process.IProcessor;
import com.zhixue.monitor.security.processor.process.TomcatAccessLogProcessor;

/**
 * 消费kafka消息。
 * 
 * @author znyin，zhangkaixuan
 */
public class Consumer {

	private static Logger logger = LoggerFactory.getLogger(Consumer.class);
	private ConsumerConnector consumer;
	private List<ExecutorService> executors = new ArrayList<ExecutorService>();

	/**
	 * 构造函数
	 */
	public Consumer() {
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig());
	}

	/**
	 * 启动运行。
	 */
	public void run() {
		Map<String, Integer> topicCountMap = createTopicCountMap();
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		for (Entry<String, Integer> topicCount : topicCountMap.entrySet()) {
			String topic = topicCount.getKey();
			logger.info("this consumer will deal kafka topic message：" + topic);
			Integer numThreads = topicCount.getValue();
			List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
			IProcessor processor = createProcessor4Topic(topic);
			ExecutorService executor = Executors.newFixedThreadPool(numThreads);
			for (final KafkaStream<byte[], byte[]> stream : streams) {
				executor.submit(new KafkaConsumer(stream, processor));
			}
			executors.add(executor);
		}
	}

	/**
	 * 关闭并释放资源。
	 */
	public void shutdown() {
		if (consumer != null) {
			consumer.shutdown();
		}
		for (ExecutorService executor : executors) {
			if (executor != null) {
				executor.shutdown();
			}
		}
	}

	/**
	 * 从配置文件中取Topic及其消费线程数的配置。
	 * 
	 * @return Topic及其消费线程数的配置。key为Topic，value为消费线程数。
	 */
	private Map<String, Integer> createTopicCountMap() {
		Properties props = new Properties();
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("consumer.properties"));
		} catch (IOException e) {
			throw new RuntimeException("consumer.properties load failed.");
		}
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		for (Object keyObj : props.keySet()) {
			String key = (String) keyObj;
			if (key.startsWith("topic.")) {
				String[] strs = key.split("\\.");
				String value = props.getProperty(key);
				topicCountMap.put(strs[1], Integer.parseInt(value));
			}
		}
		return topicCountMap;
	}

	/**
	 * 为指定的Topic创建消息处理程序。
	 * 
	 * @param topic
	 *            Topic
	 * @return 消息处理程序
	 */
	private IProcessor createProcessor4Topic(String topic) {
		return new TomcatAccessLogProcessor();
	}

	/**
	 * 从配置文件中得到ConsumerConfig
	 * 
	 * @return ConsumerConfig
	 */
	private ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("kafka.properties"));
		} catch (IOException e) {
			throw new RuntimeException("kafka.properties load failed.");
		}

		return new ConsumerConfig(props);
	}

	/**
	 * kafka消息处理线程。
	 * 
	 * @author znyin
	 *
	 */
	class KafkaConsumer implements Runnable {
		private KafkaStream<byte[], byte[]> stream;
		private IProcessor processor;

		/**
		 * 构造函数。
		 * 
		 * @param stream
		 *            KafkaStream
		 * @param processor
		 *            IProcessor
		 */
		public KafkaConsumer(KafkaStream<byte[], byte[]> stream, IProcessor processor) {
			this.stream = stream;
			this.processor = processor;
		}

		@Override
		public void run() {
			try {
				ConsumerIterator<byte[], byte[]> it = stream.iterator();
				while (it.hasNext()) {
					try {
						String msg = new String(it.next().message());
						JsonMessage jm = JSON.parseObject(msg, JsonMessage.class);
						processor.process(jm.getContent());
					} catch (Throwable e) {
						logger.error(e.getMessage(), e);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
