/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.support;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.biierg.spider.model.Site;

/**
 * @author lei
 */
public class SpiderMap implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(SpiderMap.class);

	// 中断解析信号
	private final String InterruptSignal = "Interrupt parsing";

	private List<Site> sites = new ArrayList<>();

	private static SpiderMap __instance = null;

	private SpiderMap() {

		Path mapPath = Paths.get("./etc/map");

		if (Files.exists(mapPath)) {
			try {
				Files.list(mapPath).filter(filepath -> {
					return !Files.isDirectory(filepath) && filepath.toString().endsWith(".xml");
				}).forEach(filepath -> {

					loadParseXml(filepath);
				});
			} catch (Throwable e) {
			}
		}
	}

	private void loadParseXml(Path filepath) {
		try {
			Stack<Object> stack = new Stack<>();

			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(Files.newInputStream(filepath), new DefaultHandler() {
				private boolean urlAllowed = false, urlDenied = false;

				@Override
				public void characters(char[] ch, int start, int length) throws SAXException {
					super.characters(ch, start, length);

					if (length > 0 && !stack.isEmpty()) {
						String content = new String(ch, start, length).trim();

						if (stack.peek() instanceof Site) {
							Site site = (Site) stack.peek();

							if (urlAllowed) {
								site.allowUrl(new String(ch, start, length).trim());
							} else if (urlDenied) {
								site.denyUrl(new String(ch, start, length).trim());
							}
						}

						// 加载脚本
						else if ("script".equals(stack.peek())) {

							try {
								JSEngineUtil.loadScript(content);
							} catch (Throwable e) {
								logger.error(e.getMessage(), e);
							}
						}

						// 入口URL
						else if ("entrance".equals(stack.peek()) && stack.size() > 1
								&& (stack.get(stack.size() - 2) instanceof Site)) {

							try {
								Site site = (Site) stack.get(stack.size() - 2);
								if (site.getEntrance() == null) {
									site.setEntrance(content);
								} else {
									site.setEntrance(site.getEntrance() + content);
								}
							} catch (Throwable e) {
								logger.error(e.getMessage(), e);
							}
						}
					}
				}

				@Override
				public void startDocument() throws SAXException {
					super.startDocument();
					stack.clear();
				}

				@Override
				public void endDocument() throws SAXException {
					super.endDocument();
					stack.clear();
				}

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					super.startElement(uri, localName, qName, attributes);

					if ("map".equalsIgnoreCase(qName)) {

						if ("false".equals(attributes.getValue("enable"))) {
							throw new SAXException(InterruptSignal);
						}
					}

					// 加载脚本
					else if ("script".equalsIgnoreCase(qName)) {

						if (attributes.getValue("file") != null) {
							try {
								JSEngineUtil.loadScript(
										Paths.get(filepath.getParent().toString(), attributes.getValue("file")));
							} catch (Throwable e) {
								logger.error(e.getMessage(), e);
							}
						} else {
							stack.push("script");
						}
					}

					// 新站点
					else if ("site".equalsIgnoreCase(qName)) {
						Site site = new Site();

						site.setName(attributes.getValue("name"));
						site.setContentType(attributes.getValue("content-type"));

						if (attributes.getValue("charset") != null) {
							site.setCharset(attributes.getValue("charset"));
						}

						if (attributes.getValue("prefix") != null) {
							site.setPrefix(attributes.getValue("prefix"));
						}
						if (attributes.getValue("entrance") != null) {
							site.setEntrance(attributes.getValue("entrance"));
						}

						site.setMaxDepth(Integer.parseInt(attributes.getValue("max-depth")));
						site.setFetchPeriodRandomMin(Integer.parseInt(attributes.getValue("min-period")));
						site.setFetchPeriodRandomMax(Integer.parseInt(attributes.getValue("max-period")));

						stack.push(site);
					}

					else if ("entrance".equalsIgnoreCase(qName)) {
						stack.push("entrance");
					}

					else if ("allowed".equalsIgnoreCase(qName)) {
						urlAllowed = true;
					}

					else if ("denied".equalsIgnoreCase(qName)) {
						urlDenied = true;
					}

					else if ("header".equalsIgnoreCase(qName)) {
						((Site) stack.peek()).addHeader(attributes.getValue("name"), attributes.getValue("value"));
					}

					else if ("filter".equalsIgnoreCase(qName)) {
						((Site) stack.peek()).addFilter(attributes.getValue("name"));
					}
				}

				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					super.endElement(uri, localName, qName);

					if ("script".equalsIgnoreCase(qName) && !stack.isEmpty() && "script".equals(stack.peek())) {
						stack.pop();
					}

					else if ("site".equalsIgnoreCase(qName) && !stack.isEmpty() && (stack.peek() instanceof Site)) {
						sites.add((Site) stack.pop());
					}

					else if ("entrance".equalsIgnoreCase(qName) && !stack.isEmpty()
							&& "entrance".equals(stack.peek())) {
						stack.pop();
					}

					else if ("allowed".equalsIgnoreCase(qName)) {
						urlAllowed = false;
					}

					else if ("denied".equalsIgnoreCase(qName)) {
						urlDenied = false;
					}
				}
			});
		} catch (Throwable e) {

			if (InterruptSignal.equals(e.getMessage())) {
				logger.warn(e.getMessage());
			} else {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static SpiderMap getInstance() {

		if (SpiderMap.__instance == null) {
			SpiderMap.__instance = new SpiderMap();
		}

		return SpiderMap.__instance;
	}

	public List<Site> getSites() {
		return sites;
	}
}
