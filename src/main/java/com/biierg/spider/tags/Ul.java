/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.tags;

import java.util.List;

/**
 * @author lei
 *
 */
public class Ul extends Tag {
	private static final long serialVersionUID = 1L;

	private List<Li> list;

	/**
	 * @return the list
	 */
	public List<Li> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<Li> list) {
		this.list = list;
	}
}
