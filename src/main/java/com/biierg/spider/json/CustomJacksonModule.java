/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.json;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * 自定义Jackson功能模块
 * 
 * @author lei
 */
public class CustomJacksonModule extends SimpleModule {
	private static final long serialVersionUID = 1L;

	@Override
	public void setupModule(SetupContext context) {
		context.addDeserializers(new LocalDateTimeDeserializers());
	}
}
