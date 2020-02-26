package com.biierg.spider.support;

import java.util.UUID;

/**
 * 提供生成UUID的便捷方法
 * 
 * @author lei
 */
public class UUIDUtil {

	public static String generate() {
		return UUID.randomUUID().toString();
	}

	public static String generate32() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
