/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.function;

import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.script.Bindings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.biierg.spider.json.JacksonHelper;
import com.biierg.spider.mq.IMQProducer;

/**
 * 将JSON对象发送至Kafka
 * 
 * @author lei
 */
@JSFunction
@Service("PostKfk")
public class PostKfkFunction implements Consumer<Bindings> {
	private final static Logger logger = LoggerFactory.getLogger(PostKfkFunction.class);
	
	private JacksonHelper jackson = JacksonHelper.newInstance();
	
	@Resource(name = "kfkProducer")
	private IMQProducer kfkProducer;

	@Override
	public void accept(Bindings jsonObj) {

		if (jsonObj != null) {
			kfkProducer.send(jackson.toJson(jsonObj));
		} else {
			logger.error("缺少必要的参数");
		}
	}
}
