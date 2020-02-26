/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.mq;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author lei
 */
public class Message extends HashMap<String, Object> implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String KEY = "Message.KEY";
	public static final String VALUE = "Message.VALUE";
	
	public Message(java.lang.String key, Object value) {
		put(KEY, key);
		put(VALUE, value);
	}

	@Override
	public java.lang.String toString() {

		if (get(VALUE) == null) {
			return null;
		}

		return get(VALUE).toString();
	}
}
