/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.json;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;

/**
 * 实现对 java.time.LocalDateTime 类的反序列化
 * 
 * @author lei
 */
public class LocalDateTimeDeserializers extends SimpleDeserializers {
	private static final long serialVersionUID = 1L;

	@Override
	public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
			BeanDescription beanDesc) throws JsonMappingException {

		if (type.getRawClass().equals(LocalDateTime.class)) {
			return new JsonDeserializer<LocalDateTime>() {

				@Override
				public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
						throws IOException, JsonProcessingException {

					Map<String, Object> dtmap = new HashMap<>();
					Stack<String> fldNameStack = new Stack<>();

					JsonToken token = null;
					while ((token = p.nextToken()) != null) {

						if (JsonToken.FIELD_NAME.equals(token)) {
							fldNameStack.push(ctxt.readValue(p, String.class));
						} else if (JsonToken.VALUE_STRING.equals(token)) {
							dtmap.put(fldNameStack.pop(), ctxt.readValue(p, String.class));
						} else if (JsonToken.VALUE_NUMBER_INT.equals(token)) {
							dtmap.put(fldNameStack.pop(), ctxt.readValue(p, Integer.class));
						}
					}

					return LocalDateTime.of(getIntValue(dtmap, "year"), getIntValue(dtmap, "monthValue"),
							getIntValue(dtmap, "dayOfMonth"), getIntValue(dtmap, "hour"), getIntValue(dtmap, "minute"),
							getIntValue(dtmap, "second"), getIntValue(dtmap, "nano"));
				}

				private Integer getIntValue(Map<String, Object> map, String keyName) {
					return map == null || !map.containsKey(keyName) ? 0 : Integer.parseInt(map.get(keyName).toString());
				}
			};
		}

		return null;
	}
}
