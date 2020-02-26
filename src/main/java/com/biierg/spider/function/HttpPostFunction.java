/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.script.Bindings;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 执行Http Post请求的函数
 * 
 * @author lei
 */
@JSFunction
@Service("HttpPost")
public class HttpPostFunction implements Function<Bindings, String> {
	private final static Logger logger = LoggerFactory.getLogger(HttpPostFunction.class);

	@Override
	public String apply(Bindings request) {

		if (request != null && request.containsKey("url")) {
			String url = request.get("url").toString();
			
			try {
				HttpPost httpMethod = new HttpPost(url);
				httpMethod.setHeader("User-Agent", "robot.biierg.com");

				if (request.containsKey("data")) {
					Bindings data = (Bindings) request.get("data");
					
					if (data != null && !data.isEmpty()) {
						List<NameValuePair> params = new ArrayList<>();
						data.keySet().forEach(key -> {
							params.add(new BasicNameValuePair(key, data.get(key).toString()));
						});
						
						httpMethod.setEntity(new UrlEncodedFormEntity(params));
					}
				}

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
