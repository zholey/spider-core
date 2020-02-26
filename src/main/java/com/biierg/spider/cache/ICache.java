/**
 * 版权所有@2015 北京京投亿雅捷交通科技有限公司；未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.cache;

import java.util.Set;

/**
 * 分布式缓存服务接口(Distributed)
 * 
 * @author lei
 */
public interface ICache {

	/**
	 * 测试缓存中是否包含指定的Key
	 * 
	 * @param scope
	 * @param key
	 * @return
	 */
	public boolean containsKey(String scope, String key);

	/**
	 * 从缓存中获取指定Key对应的原始值
	 * 
	 * @param scope
	 * @param key
	 * @return
	 */
	public String get(String scope, String key);
	
	/**
	 * 从缓存中获取指定Key对应的原始值
	 *
	 * @param scope
	 * @param key
	 * @return
	 */
	public Set<String> getKeys(String scope, String key);
	
	/**
	 * 从缓存中获取指定Key对应的对象
	 * 
	 * @param scope
	 * @param key
	 * @param objClass
	 * @return
	 */
	public <T> T getObject(String scope, String key, Class<T> objClass);

	/**
	 * 从缓存中获取指定Key对应的对象
	 * 
	 * @param scope
	 * @param key
	 * @param objClass
	 * @return
	 */
	public <T> T getObject(String scope, String key, Class<T> objClass, Class<?> actualTypeClass);

	/**
	 * 向缓存中设置一个Key用作标记
	 * 
	 * @param scope
	 * @param key
	 */
	public void put(String scope, String key);

	/**
	 * 向缓存中设置一个Key用作标记
	 * 
	 * @param scope
	 * @param key
	 * @param expired
	 *            过期时间(S)
	 */
	public void put(String scope, String key, int expired);

	/**
	 * 向缓存中设置值
	 * 
	 * @param scope
	 * @param key
	 * @param value
	 */
	public void put(String scope, String key, Object value);

	/**
	 * 向缓存中设置值
	 * 
	 * @param scope
	 * @param key
	 * @param value
	 * @param expired
	 *            过期时间(S)
	 */
	public void put(String scope, String key, Object value, int expired);

	/**
	 * 删除指定的缓存
	 * 
	 * @param scope
	 * @param key
	 */
	public void remove(String scope, String key);

	/**
	 * 更新缓存中某个Key的过期时间
	 * 
	 * @param scope
	 * @param key
	 * @param expired
	 *            新的过期时间(S)
	 */
	public void setExpire(String scope, String key, int expired);
}
