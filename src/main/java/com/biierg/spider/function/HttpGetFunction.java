/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.function;

import java.util.function.Function;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.biierg.common.util.StringUtil;

/**
 * 执行Http Get请求的函数
 * 
 * @author lei
 */
@JSFunction
@Service("HttpGet")
public class HttpGetFunction implements Function<String, String> {
	private final static Logger logger = LoggerFactory.getLogger(HttpGetFunction.class);

	@Override
	public String apply(String url) {

		if (!StringUtil.isNull(url)) {

			try {
				HttpGet httpMethod = new HttpGet(url);
				httpMethod.setHeader("User-Agent", "robot.biierg.com");

				RequestConfig.Builder configBuilder = null;

				if (httpMethod.getConfig() != null) {
					configBuilder = RequestConfig.copy(httpMethod.getConfig());
				} else {
					configBuilder = RequestConfig.custom();
				}

				configBuilder.setSocketTimeout(30000);
				configBuilder.setConnectTimeout(30000);

				httpMethod.setConfig(configBuilder.build());

				try (CloseableHttpClient httpclient = HttpClients.createDefault();
						CloseableHttpResponse response = httpclient.execute(httpMethod)) {

					return EntityUtils.toString(response.getEntity());
				} catch (Throwable e) {
					logger.error("在抓取网页 {} 时出现异常 [{}]", url, e.getMessage(), e);
				}

			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			logger.error("缺少必要的参数");
		}

		return null;
	}
}
