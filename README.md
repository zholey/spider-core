# Spider-core

> 中文名：通用爬虫引擎



本程序是基于Apache HttpClient编写的爬虫引擎，支持自定义爬虫策略。程序支持JS脚本，可以通过JS脚本来对数据进行解析、查找等操作，处理完的数据，可以利用程序提供的JS函数，发送至Redis或Kafka等；



## 目录/文件说明

* etc/profile.properties - 环境配置文件，用本配置Redis数据库及Kafka地址信息
* map/ - 爬早策略文件存储目录；程序自动扫描该目录，查找所有的XML文件并解析，但并不是所有的文件都会启动爬取，程序只会处理 <map enable="true"> 的文件
* map/lib/ - 可以用来存储一些脚本文件，非必须



## 策略文件样例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<map enable="false">

	<!-- 
		// 容器提供的JS函数说明
		// 
		// HttpGet({url:'', charset(optional):''}) : 发送Get请求
		// HttpPost({url:'', data(optional): {}, charset(optional):''}) : 发送Post请求
		// PostKfk({message:'', topic(optional):''}) : 将给定的对象发送至Kafka
		// RedisGet({scope: '', key: ''}) : 从Redis中读取JSON对象
		// RedisPut({scope: '', key: '', value: ''}) : 将指定的对象保存至Redis
		// Log(content) : 记录日志
	-->

	<script file="./lib/utils.js"/>
	<script>
	<![CDATA[
		var deviceListFilter = function(jsonResultStr) {
			print("采集到设备列表数据");
			print(jsonResultStr);
			
			print(HttpGet({url:"http://10.247.53.143:10010/pgiscs-web/iscs/device/listByCondition"}));
			Log(HttpPost({
				url: "http://10.247.53.143:10010/pgiscs-web/iscs/device/listByCondition"
			}));
			
			RedisPut({
				scope : "a",
				key : "b",
				value : "c"
			});
			
			print(RedisGet({
				scope : "a",
				key : "b"
			}));
		};
		
		
		var stockDataFilter = function(jsonResultStr) {
			print("采集到股票数据");
			print(jsonResultStr);
		};
		
		var neteaseFilter = function(doc) {
			print("采集到网易娱乐新闻");
			// print(doc);
			
			if (doc) {
				var elements = doc.select("ul.cm_ul_round li");
				if (elements) {
					for (var i in elements) {
						var a = elements[i].child(0);
						if (a) {
							print(a.html() + "  " + a.attr("href"));
						}
					}
				}
			}
		};
	]]>
	</script>
	
	<site name="京投交通科技股票数据" 
		content-type="json"
		prefix="http://push2.eastmoney.com" 
		max-depth="1" min-period="2" max-period="10">
		
		<entrance>
		<![CDATA[
			http://push2.eastmoney.com/api/qt/stock/get?secid=116.01522&fields=f43,f44,f45,f46,f47,f48,f49,f51,f52,f60,f84,f85,f116,f117,f161,f164,f167,f169,f170&fltt=2&_=${NoCache}
		]]>
		</entrance>
		
		<header name="Referer" value="http://quote.eastmoney.com/"/>
		<header name="User-Agent" value="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36"/>
		
		<filter name="stockDataFilter"/>
	</site>
	
	<site name="网易娱乐新闻" 
		content-type="html" charset="UTF-8"
		prefix="https://www.163.com/"
		entrance="https://www.163.com/"
		max-depth="1" min-period="2" max-period="10">
	
		<filter name="neteaseFilter"/>
	</site>
</map>
```



## 策略文件说明

首先，策略文件是一个XML格式文件，以 `map` 为根节点，其中可以包含若干个 `site` 节点和 `script` 节点。每个 `site` 节点代表一个要抓取的网站。