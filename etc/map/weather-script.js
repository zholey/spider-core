/**
 * 天气数据处理
 */
var bjWeatherFilter = function(json) {
	eval(json);
//	print("采集到的天气信息：" + json);
	print("采集到的天气信息：" + toJsonString(dataSK));
	
	var weatherInfo = {
			"__DATATYPE" : "ISCS_WEATHER",
			
			"dataSource" : "中国天气网",
			"sourceUrl" : "http://www.weather.com.cn",
			
			"region" : dataSK["cityname"],
			"obsTime" : (dataSK["date"] + dataSK["time"]),
			"weatherType" : dataSK["weather"],
			"weatherImg" : dataSK["weathere"],
			"temperature" : dataSK["temp"],
			"windDirection" : dataSK["WD"],
			"windLevel" : dataSK["WS"],
			"windSpeed" : dataSK["wse"],
			"precipitation" : dataSK["rain"],
			"aqIndex" : dataSK["aqi"],
			"pm25" : dataSK["aqi_pm25"]
	};
	
	var humidityMatchGrp = /(\d+(\.\d+)?)%/i.exec(dataSK["SD"]);
	if (humidityMatchGrp) {
		weatherInfo["humidity"] = humidityMatchGrp[1];
	}
	
	PostKfk(weatherInfo);
};