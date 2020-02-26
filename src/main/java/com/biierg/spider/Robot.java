/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biierg.common.util.StringUtil;
import com.biierg.spider.model.Page;
import com.biierg.spider.model.Site;
import com.biierg.spider.support.JSEngineUtil;

/**
 * 网页爬虫
 * 
 * @author lei
 */
@SuppressWarnings("unused")
public class Robot implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(Robot.class);

	// 用于检查URL是否完整的正则表达式
	private final static Pattern UrlIntegrityExp = Pattern.compile("(?i)^https?\\:\\/\\/");

	// 用于解析网页中 超链接 的正则表达式
	private final static Pattern HyperLinkExp = Pattern
			.compile("(?i)\\<a\\s+href=[\'\"]([^\"\\<\\>]+)[\'\"]>([^\\<\\>]+)</a>");

	// 用于解析网页中 页面标题 的正则表达式
	private final static Pattern TitleExp = Pattern.compile("(?i)\\<title\\s*>([^\\<\\>]+)</title\\s*>");
	// 用于解析网页中 关键字 的正则表达式
	private final static Pattern KeywordsExp = Pattern
			.compile("(?i)\\<meta\\s+name=[\'\"]keywords[\'\"]\\s+content=[\'\"]([^\'\"\\<\\>]+)[\'\"]>");
	// 用于解析网页中 机器人选项 的正则表达式
	private final static Pattern RobotsExp = Pattern
			.compile("(?i)\\<meta\\s+name=[\'\"]robots[\'\"]\\s+content=[\'\"]([^\'\"\\<\\>]+)[\'\"]>");
	// 用于解析网页中 页面描述 的正则表达式
	private final static Pattern DescriptionExp = Pattern
			.compile("(?i)\\<meta\\s+name=[\'\"]description[\'\"]\\s+content=[\'\"]([^\'\"\\<\\>]+)[\'\"]>");

	// 用于解析URL中的正则表达式
	private final static Pattern NoCacheExp = Pattern.compile("(?i)\\$\\{NoCache\\}");

	private CloseableHttpClient httpclient = HttpClients.createDefault();

	private ScheduledExecutorService executeService;
	private Site site;

	public Robot(ScheduledExecutorService executeService, Site site) {
		this.executeService = executeService;
		this.site = site;
	}

	public void startup() {

		final Random random = new Random();

		int delay = 0;
		while (delay < site.getFetchPeriodRandomMin()) {
			delay = random.nextInt(site.getFetchPeriodRandomMax() + 1);
		}

		if (logger.isInfoEnabled()) {
			logger.info("{} 秒后，启动【{}】抓取任务", delay, site.getName());
		}

		executeService.schedule(this, delay, TimeUnit.SECONDS);
	}

	public void run() {

		try {
			if (logger.isInfoEnabled()) {
				logger.info("【{}】抓取任务开始 ...", site.getName());
			}
			fetchNow(site.getEntrance(), null, null, 1);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		} finally {
			startup();
		}
	}

	private Page fetchNow(String url, String referer, String refererTitle, int depth) {

		// 检查当前抓取深度是否达到最大允许深度
		if (site != null && url != null && depth <= site.getMaxDepth()) {

			Matcher nocacheMatch = NoCacheExp.matcher(url);
			if (nocacheMatch.find()) {
				url = nocacheMatch.replaceAll(String.valueOf(System.currentTimeMillis()));
			}

			if (logger.isDebugEnabled()) {
				logger.debug("准备抓取网页 {}", url);
			}

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent", "robot.biierg.com");

			site.getHeaders().forEach(nvpair -> {
				httpGet.setHeader(nvpair.getName(), nvpair.getValue());
			});

			RequestConfig.Builder configBuilder = null;

			if (httpGet.getConfig() != null) {
				configBuilder = RequestConfig.copy(httpGet.getConfig());
			} else {
				configBuilder = RequestConfig.custom();
			}

			configBuilder.setSocketTimeout(30000);
			configBuilder.setConnectTimeout(30000);

			httpGet.setConfig(configBuilder.build());

			try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
				Charset charset = null;
				if (site.getCharset() != null) {
					charset = Charset.forName(site.getCharset());
				} else if (ContentType.get(response.getEntity()).getCharset() != null) {
					charset = ContentType.get(response.getEntity()).getCharset();
				}

				if (charset == null) {
					charset = Charset.forName("UTF-8");
				}
				String pageContent = new String(EntityUtils.toString(response.getEntity()).getBytes(charset),
						Charset.forName("UTF-8"));

				return buildPageNow(url, referer, refererTitle, pageContent, depth);
			} catch (Throwable e) {
				logger.error("在抓取网页 {} 时出现异常 [{}]", url, e.getMessage(), e);
			}

		} else {

			if (logger.isDebugEnabled()) {
				logger.debug("忽略网页 [{}] [达到最大抓取深度]", url);
			}
		}

		return null;
	}

	private Page buildPageNow(String url, String referer, String refererTitle, String pageContent, int depth) {
		Page page = new Page();

		page.setUrl(url);
		page.setReferer(referer);
		page.setRefererTitle(refererTitle);
		page.setPageContent(pageContent);

		// child page
		Map<String, String> childMap = obtainChildUrl(pageContent);
		if (childMap != null && !childMap.isEmpty()) {

			childMap.entrySet().forEach(entry -> {
				String childUrl = entry.getKey();

				// URL 自动补全
				if (!UrlIntegrityExp.matcher(childUrl).find()) {
					childUrl = site.getPrefix() + childUrl;
				}

				// 抓取子页面
				Page child = (fetchNow(childUrl, page.getUrl(), entry.getValue(), depth + 1));
				if (child != null) {
					page.addChild(child);
				}
			});
		}

		// 执行过滤器
		site.getFilters().forEach(filterFuncName -> {

			try {
				if ("html".equalsIgnoreCase(site.getContentType())) {
					JSEngineUtil.invoke(filterFuncName, Jsoup.parse(page.getPageContent()), depth);
				} else if ("json".equalsIgnoreCase(site.getContentType())) {
					JSEngineUtil.invoke(filterFuncName, page.getPageContent(), depth);
				}
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
		});

		return page;
	}

	private Map<String, String> obtainChildUrl(String pageContent) {
		Map<String, String> urls = new HashMap<>();

		Matcher urlMatch = HyperLinkExp.matcher(pageContent);
		while (urlMatch != null && urlMatch.find()) {
			String url = urlMatch.group(1);
			String title = urlMatch.group(2);

			// 检查该Url是否不在允许列表内
			if (!site.getAllowedUrls().isEmpty() && !site.getAllowedUrls().stream().anyMatch(regex -> {
				return regex.matcher(url).find();
			})) {
				continue;
			}

			// 检查该Url是否在拒绝列表内
			if (!site.getDeniedUrls().isEmpty() && site.getDeniedUrls().stream().anyMatch(regex -> {
				return regex.matcher(url).find();
			})) {
				continue;
			}

			urls.put(url, title);
		}

		return urls;
	}
}
