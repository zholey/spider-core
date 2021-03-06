/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.function;

import java.util.function.Function;

import javax.annotation.Resource;
import javax.script.Bindings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.biierg.spider.cache.ICache;
import com.biierg.spider.json.JacksonHelper;

/**
 * 从Redis中读取JSON对象
 * 
 * @author lei
 */
@JSFunction
@Service("RedisGet")
public class RedisGetFunction implements Function<Bindings, Object> {
	private final static Logger logger = LoggerFactory.getLogger(RedisGetFunction.class);
	
	@Resource(name = "redisCache")
	private ICache redisCache;

	@Override
	public Object apply(Bindings jsonObj) {

		if (jsonObj != null && jsonObj.containsKey("scope") && jsonObj.containsKey("key")) {
			
			String scope = jsonObj.get("scope").toString();
			String key = jsonObj.get("key").toString();
			
			logger.info("RedisGet({}, {})", scope, key);
			
			return redisCache.getObject(scope, key, Object.class);
		} else {
			logger.error("缺少必要的参数");
		}
		
		return null;
	}
}
