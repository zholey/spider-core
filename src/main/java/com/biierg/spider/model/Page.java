/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面
 * 
 * @author lei
 */
public class Page implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String url; // 页面URL
	
	private String referer; // 引用此页面的URL
	private String refererTitle; // 引用此页面的链接文字
	
	private String title; // 标题
	private String keywords; // 关键字
	private String robots; // 机器人选项
	private String description; // 页面描述
	
	private String pageContent; // 页面内容
	
	// 子页面
	private List<Page> children = new ArrayList<>();

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the referer
	 */
	public String getReferer() {
		return referer;
	}

	/**
	 * @param referer the referer to set
	 */
	public void setReferer(String referer) {
		this.referer = referer;
	}

	/**
	 * @return the refererTitle
	 */
	public String getRefererTitle() {
		return refererTitle;
	}

	/**
	 * @param refererTitle the refererTitle to set
	 */
	public void setRefererTitle(String refererTitle) {
		this.refererTitle = refererTitle;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the robots
	 */
	public String getRobots() {
		return robots;
	}

	/**
	 * @param robots the robots to set
	 */
	public void setRobots(String robots) {
		this.robots = robots;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the pageContent
	 */
	public String getPageContent() {
		return pageContent;
	}

	/**
	 * @param pageContent the pageContent to set
	 */
	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}

	/**
	 * @return the children
	 */
	public List<Page> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void addChild(Page child) {
		this.children.add(child);
	}
}