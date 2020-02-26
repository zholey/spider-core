/**
 * 版权所有@2015 北京京投亿雅捷交通科技有限公司；未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.cache.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.stream.Collectors;

import com.biierg.spider.cache.ICache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 基于Redis的缓存实现（二进制存取方式）
 * 
 * @author lei
 */
public class BinaryCache implements ICache {

	private static JedisPool jedisPool;

	private int maxTotal = 500;
	private int maxIdle = 5;
	private long maxWaitMillis = 1000 * 100;

	/** Redis 主机地址 */
	private String redisHost;

	/** Redis 端口号 */
	private int redisPort;

	/** Key namespace */
	private String namespace;

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}

	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public BinaryCache() {
	}

	public void connect() {

		if (jedisPool == null) {
			JedisPoolConfig config = new JedisPoolConfig();

			config.setMaxTotal(maxTotal);
			config.setMaxIdle(maxIdle);
			config.setMaxWaitMillis(maxWaitMillis);
			config.setTestOnBorrow(true);

			jedisPool = new JedisPool(config, redisHost, redisPort);
		}
	}

	public void disconnect() {
		jedisPool.destroy();
	}
	
	public Jedis getJedis() {
		connect();
		
		if (jedisPool != null) {
			return jedisPool.getResource();
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#containsKey(java.lang.String)
	 */
	@Override
	public boolean containsKey(String scope, String key) {

		Jedis jedis = null;
		try {
			jedis = getJedis();

			return jedis != null && jedis.exists(obtainKey(scope, key));
		} finally {
			
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#get(java.lang.String)
	 */
	@Override
	public String get(String scope, String key) {

		Jedis jedis = null;
		try {
			jedis = getJedis();

			return deserialize(jedis.get(obtainKey(scope, key)), String.class);
		} finally {
			jedis.close();
		}
	}

	public Set<String> getKeys(String scope, String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();

			Set<byte[]> keys = jedis.keys(obtainKey(scope, key));

			if (keys != null && !keys.isEmpty()) {
				return keys.parallelStream().map(String::new).map(k -> {
					return obtainOriginKey(scope, k);
				}).collect(Collectors.toSet());
			}

			return null;
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#getObject(java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	public <T> T getObject(String scope, String key, Class<T> objClass) {
		return getObject(scope, key, objClass, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#getObject(java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	public <T> T getObject(String scope, String key, Class<T> objClass, Class<?> actualTypeClass) {

		Jedis jedis = null;
		try {
			jedis = getJedis();

			if (key != null && objClass != null && jedis.exists(obtainKey(scope, key))) {
				byte[] objBytes = jedis.get(obtainKey(scope, key));

				return deserialize(objBytes, objClass);
			}

			return null;
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void put(String scope, String key) {
		put(scope, key, null, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void put(String scope, String key, int expired) {
		put(scope, key, null, expired);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void put(String scope, String key, Object value) {
		put(scope, key, value, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void put(String scope, String key, Object value, int expired) {

		Jedis jedis = null;
		try {
			jedis = getJedis();

			if (key != null) {

				if (expired > 0) {
					jedis.setex(obtainKey(scope, key), expired, serialize(value));
				} else {
					jedis.set(obtainKey(scope, key), serialize(value));
				}
			}
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#remove(java.lang.String)
	 */
	@Override
	public void remove(String scope, String key) {

		Jedis jedis = null;
		try {
			jedis = getJedis();

			jedis.del(obtainKey(scope, key));
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cocc.common.cache.ICache#setExpired(java.lang.String,
	 * java.lang.String, int)
	 */
	@Override
	public void setExpire(String scope, String key, int expired) {

		Jedis jedis = null;
		try {
			jedis = getJedis();

			jedis.expire(obtainKey(scope, key), expired);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 对象序列化
	 * 
	 * @param obj
	 * @return
	 */
	private byte[] serialize(Object obj) {
		ByteArrayOutputStream byteAryOutStream = new ByteArrayOutputStream();

		if (obj != null) {
			try (ObjectOutputStream objOutStream = new ObjectOutputStream(byteAryOutStream)) {
				objOutStream.writeObject(obj);
			} catch (Throwable e) {
			}
		}

		return byteAryOutStream.toByteArray();
	}

	/**
	 * 对象反序列化
	 * 
	 * @param obj
	 * @return
	 */
	private <T> T deserialize(byte[] objBytes, Class<T> objClass) {

		if (objBytes != null) {
			ByteArrayInputStream byteAryOutStream = new ByteArrayInputStream(objBytes);

			try (ObjectInputStream objInStream = new ObjectInputStream(byteAryOutStream)) {
				return objClass.cast(objInStream.readObject());
			} catch (Throwable e) {
			}
		}

		return null;
	}

	private String obtainOriginKey(String scope, String key) {
		return key.substring((namespace + "." + scope + ".").length());
	}

	/**
	 * 构造缓存Key
	 * 
	 * @param scope
	 * @param key
	 * @return
	 */
	private byte[] obtainKey(String scope, String key) {
		return (namespace + "." + scope + "." + key).getBytes();
	}
}
