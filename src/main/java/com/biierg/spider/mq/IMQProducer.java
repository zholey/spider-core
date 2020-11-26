package com.biierg.spider.mq;

import java.io.Serializable;

/**
 * 消息生产者接口
 * 
 * @author lei
 */
public interface IMQProducer {

	/**
	 * 发送对象消息至消息队列
	 * 
	 * @param msgObj 消息对象
	 * @return
	 */
	public boolean send(Serializable msgObj);

	/**
	 * 发送对象消息至消息队列
	 * 
	 * @param topic  消息主题
	 * @param msgObj 消息对象
	 * @return
	 */
	public boolean send(String topic, Serializable msgObj);
}
