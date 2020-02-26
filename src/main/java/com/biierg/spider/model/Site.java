/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author lei
 */
public class Site implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name; // 站点名称
	private String contentType; // 内容类型（html/json）
	private String charset; // 内容类型（html/json）
	
	private int maxDepth; // 最大爬取深度

	private int fetchPeriodRandomMin; // 采集周期随机数 最小值
	private int fetchPeriodRandomMax; // 采集周期随机数 最大值
	
	private String prefix; // 子页面的相对根
	private String entrance; // 入口

	private List<Pattern> allowedUrls = new ArrayList<>();
	private List<Pattern> deniedUrls = new ArrayList<>();
	
	private List<NameValuePair> headers = new ArrayList<>();

	private List<String> filters = new ArrayList<>();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @return the maxDepth
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * @param maxDepth
	 *            the maxDepth to set
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
		
	/**
	 * @return the fetchPeriodRandomMin
	 */
	public int getFetchPeriodRandomMin() {
		return fetchPeriodRandomMin;
	}

	/**
	 * @param fetchPeriodRandomMin the fetchPeriodRandomMin to set
	 */
	public void setFetchPeriodRandomMin(int fetchPeriodRandomMin) {
		this.fetchPeriodRandomMin = fetchPeriodRandomMin;
	}

	/**
	 * @return the fetchPeriodRandomMax
	 */
	public int getFetchPeriodRandomMax() {
		return fetchPeriodRandomMax;
	}

	/**
	 * @param fetchPeriodRandomMax the fetchPeriodRandomMax to set
	 */
	public void setFetchPeriodRandomMax(int fetchPeriodRandomMax) {
		this.fetchPeriodRandomMax = fetchPeriodRandomMax;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the entrance
	 */
	public String getEntrance() {
		return entrance;
	}

	/**
	 * @param entrance the entrance to set
	 */
	public void setEntrance(String entrance) {
		this.entrance = entrance;
	}

	public void allowUrl(String regex) {
		allowedUrls.add(Pattern.compile(regex));
	}

	/**
	 * @return the allowedUrls
	 */
	public List<Pattern> getAllowedUrls() {
		return allowedUrls;
	}

	public void denyUrl(String regex) {
		deniedUrls.add(Pattern.compile(regex));
	}

	/**
	 * @return the deniedUrls
	 */
	public List<Pattern> getDeniedUrls() {
		return deniedUrls;
	}
	
	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	/**
	 * @return the headers
	 */
	public List<NameValuePair> getHeaders() {
		return headers;
	}

	public void addFilter(String filter) {
		filters.add(filter);
	}

	/**
	 * @return the filters
	 */
	public List<String> getFilters() {
		return filters;
	}
}
