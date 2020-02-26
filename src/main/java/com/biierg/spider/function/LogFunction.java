/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.function;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 记录日志
 * 
 * @author lei
 */
@JSFunction
@Service("Log")
public class LogFunction implements Consumer<String> {
	private final static Logger logger = LoggerFactory.getLogger(LogFunction.class);

	@Override
	public void accept(String content) {
		
		if (logger.isInfoEnabled()) {
			logger.info(content);
		}
	}
}
