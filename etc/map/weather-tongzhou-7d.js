/**
 * 天气数据处理
 */
var tzWeatherFilter = function(doc) {
//	Log(doc);
	
	var weatherInfo = [];
	
	var elements = doc.select("div#7d > ul > li.sky");
	if (elements) {
		
		try {
			for (var i in elements) {
				var weatherDCode = elements[i].child(1).attributes().get("class");
				var weatherNCode = elements[i].child(2).attributes().get("class");
				
				var temperature = elements[i].child(4).select("span").text() + "/" 
							+ elements[i].child(4).select("i").text();
				
				var wind = "";
				var wary = elements[i].child(5).select("em > span");
				if (wary && wary.length > 0) {
					for (var j in wary) {
						wind += wary[j].attributes().get("title") + ",";
					}
				}
				if (wind) {
					wind = wind.replace(/\,+$/g, '');
				}
				
				var w = {
					"date": elements[i].child(0).text(),
					"weather": elements[i].child(3).text(),
					"weatherDCode": weatherDCode,
					"weatherNCode": weatherNCode,
					"temperature": temperature,
					"wind": wind,
					"windGrade": elements[i].child(5).select("i").text()
				};
				weatherInfo.push(w);
			}
			
			if (weatherInfo && weatherInfo.length > 0) {
//				Log(toJsonString(weatherInfo));
				RedisPut({
					"scope": "weather", 
					"key": "tongzhou-7d", 
					"value": weatherInfo
				});
			}
		} catch (error) {
		}
	}
};
