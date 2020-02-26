/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.tags;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lei
 */
public abstract class Tag implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String id;
	protected String name;
	protected String styleName;

	protected String title;
	
	private List<Tag> children = new ArrayList<>();
	
	public String getTagName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the styleName
	 */
	public String getStyleName() {
		return styleName;
	}

	/**
	 * @param styleName the styleName to set
	 */
	public void setStyleName(String styleName) {
		this.styleName = styleName;
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
	 * @return the children
	 */
	public List<Tag> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<Tag> children) {
		this.children = children;
	}
}
