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

import com.biierg.common.cache.ICache;

/**
 * 将JSON对象保存至Redis
 * 
 * @author lei
 */
@JSFunction
@Service("RedisPut")
public class RedisPutFunction implements Consumer<Bindings> {
	private final static Logger logger = LoggerFactory.getLogger(RedisPutFunction.class);

	@Resource(name = "redisCache")
	private ICache redisCache;

	@Override
	public void accept(Bindings jsonObj) {

		if (jsonObj != null && jsonObj.containsKey("scope") && jsonObj.containsKey("key")) {
			redisCache.put(jsonObj.get("scope").toString(), jsonObj.get("key").toString(), jsonObj.get("value"));
		} else {
			logger.error("缺少必要的参数");
		}
	}
}
