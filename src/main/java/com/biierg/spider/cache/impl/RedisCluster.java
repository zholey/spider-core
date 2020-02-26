/**
 * 版权所有@2015 北京京投亿雅捷交通科技有限公司；未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.cache.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.biierg.spider.cache.ICache;
import com.biierg.spider.support.StringUtil;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 基于Redis集群的缓存实现（二进制存取方式）
 * 
 * @author lei
 */
public class RedisCluster implements ICache {

	private JedisCluster jedisCluster;

	private int timeout = 2000;

	private int maxTotal = 500;
	private int maxIdle = 5;
	private long maxWaitMillis = 1000 * 100;

	/** Redis 集群地址 */
	private String clusterHost;

	/** Key namespace */
	private String namespace;

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public void setClusterHost(String clusterHost) {
		this.clusterHost = clusterHost;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public RedisCluster() {
	}

	public void connect() {

		if (jedisCluster == null) {

			if (StringUtil.isNull(clusterHost)) {
				throw new NullPointerException("clusterHost is null");
			}

			JedisPoolConfig config = new JedisPoolConfig();

			config.setMaxTotal(maxTotal);
			config.setMaxIdle(maxIdle);
			config.setMaxWaitMillis(maxWaitMillis);
			config.setTestOnBorrow(true);

			Set<HostAndPort> clusterNodes = Arrays.stream(clusterHost.split("\\,")).filter(host -> {
				return !StringUtil.isNull(host) && host.contains(":");
			}).map(host -> {
				String[] hostIpAry = host.split("\\:");
				return new HostAndPort(hostIpAry[0], Integer.parseInt(hostIpAry[1]));
			}).collect(Collectors.toSet());

			jedisCluster = new JedisCluster(clusterNodes, timeout, config);
		}
	}

	public void disconnect() {
		try {
			jedisCluster.close();
		} catch (IOException e) {
		}
	}
	
	public JedisCluster getJedisCluster() {
		connect();
		
		return jedisCluster;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#containsKey(java.lang.String)
	 */
	@Override
	public boolean containsKey(String scope, String key) {
		JedisCluster jedisCluster = getJedisCluster();
		return jedisCluster != null && jedisCluster.exists(obtainKey(scope, key));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#get(java.lang.String)
	 */
	@Override
	public String get(String scope, String key) {
		JedisCluster jedisCluster = getJedisCluster();
		return deserialize(jedisCluster.get(obtainKey(scope, key)), String.class);
	}

	public Set<String> getKeys(String scope, String key) {
		JedisCluster jedisCluster = getJedisCluster();

		Set<byte[]> keys = jedisCluster.hkeys(obtainKey(scope, key));

		if (keys != null && !keys.isEmpty()) {
			return keys.parallelStream().map(String::new).map(k -> {
				return obtainOriginKey(scope, k);
			}).collect(Collectors.toSet());
		}

		return null;
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
		JedisCluster jedisCluster = getJedisCluster();

		if (key != null && objClass != null && jedisCluster.exists(obtainKey(scope, key))) {
			byte[] objBytes = jedisCluster.get(obtainKey(scope, key));

			return deserialize(objBytes, objClass);
		}

		return null;
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

		if (key != null) {
			JedisCluster jedisCluster = getJedisCluster();

			if (expired > 0) {
				jedisCluster.setex(obtainKey(scope, key), expired, serialize(value));
			} else {
				jedisCluster.set(obtainKey(scope, key), serialize(value));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#remove(java.lang.String)
	 */
	@Override
	public void remove(String scope, String key) {
		JedisCluster jedisCluster = getJedisCluster();
		jedisCluster.del(obtainKey(scope, key));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cocc.common.cache.ICache#setExpired(java.lang.String,
	 * java.lang.String, int)
	 */
	@Override
	public void setExpire(String scope, String key, int expired) {
		JedisCluster jedisCluster = getJedisCluster();
		jedisCluster.expire(obtainKey(scope, key), expired);
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
