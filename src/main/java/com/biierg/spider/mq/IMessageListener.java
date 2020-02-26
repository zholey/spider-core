package com.biierg.spider.mq;

import java.io.Serializable;

/**
 * 消息监听接口
 * 
 * @author lei
 */
public interface IMessageListener {

	/**
	 * 响应收到的消息
	 * 
	 * @param message
	 * @return
	 */
	public boolean onMessage(Serializable message);

	/**
	 * IMessageListener接口的空实现
	 * 
	 * @author lei
	 */
	public static class DefaultAdapter implements IMessageListener {

		public boolean onMessage(Serializable message) {
			return false;
		}
	}
}
