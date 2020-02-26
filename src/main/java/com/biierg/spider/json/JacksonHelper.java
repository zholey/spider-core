/**
 * 版权所有@2015 北京京投亿雅捷交通科技有限公司；未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.json;

import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json工具集(By jackson)
 * 
 * @author lei
 */
public class JacksonHelper {

	private ObjectMapper objectMapper;

	private JacksonHelper() {
		objectMapper = new ObjectMapper();

		// 注册自定义的解析模块
		objectMapper.registerModule(new CustomJacksonModule());
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static JacksonHelper newInstance() {
		return new JacksonHelper();
	}

	/**
	 * 将JavaBean转换成JSON字符串
	 * 
	 * @param obj
	 * @return
	 */
	public String toJson(Object obj) {

		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * （常用）从给定的JSON字符串中读取出指定类型的JavaBean；
	 * 
	 * @param content
	 * @param typeClass
	 * @return
	 */
	public <T> T getObject(String content, Class<T> typeClass) {

		try {
			return objectMapper.readValue(content, typeClass);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * （常用）从给定的JSON字符串中读取出指定类型的JavaBean；
	 * 
	 * @param content
	 * @param typeClass
	 * @return
	 */
	public <T> List<T> getList(String content, Class<T> elementType) {

		try {
			return objectMapper.readValue(content,
					objectMapper.getTypeFactory().constructParametricType(List.class, elementType));
		} catch (Throwable e) {
		}

		return null;
	}

	/**
	 * 从给定的JSON字符串中读取出指定类型的JavaBean
	 * 
	 * @param content
	 * @param typeReference
	 * @return
	 */
	public <T> T getObject(String content, TypeReference<T> typeReference) {

		try {
			return objectMapper.readValue(content, typeReference);
		} catch (Throwable e) {
		}

		return null;
	}

	/**
	 * 从给定的JSON字符串中读取出指定类型的JavaBean
	 * 
	 * @param content
	 * @param type
	 * @return
	 */
	public <T> T getObject(String content, Class<?> parametrized, Class<?>... parameterClasses) {

		try {
			return objectMapper.readValue(content,
					objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses));
		} catch (Throwable e) {
		}

		return null;
	}
}
