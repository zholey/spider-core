/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.mq.impl;

import java.io.Serializable;

import com.biierg.spider.mq.IMQConsumer;
import com.biierg.spider.mq.IMQProducer;
import com.biierg.spider.mq.IMessageListener;
import com.biierg.spider.mq.Message;
import com.biierg.spider.support.UUIDUtil;

/**
 * @author lei
 */
public class SimpleBridge implements IMQProducer, IMQConsumer {
	
	private static class SingletonHolder {
		private static SimpleBridge instance = new SimpleBridge();
	}
	
	public static SimpleBridge getInstance() {
		return SingletonHolder.instance;
	}

	private IMessageListener listener;
	
	private SimpleBridge() {
	}

	@Override
	public void setMessageListener(IMessageListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean send(Serializable msgObj) {
		return listener.onMessage(new Message(UUIDUtil.generate32(), msgObj));
	}
}
