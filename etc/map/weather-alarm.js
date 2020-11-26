/**
 * 天气数据处理
 */
var bjAlarmFilter = function(json) {
//	Log(json);
	var alarminfo;
	eval(json);
	
	if (alarminfo && alarminfo["data"] && alarminfo["data"].length) {
		
		// 从返回结果里查找“北京市”的预警信息
		var beijingAlarm;
		var filter = function(d) {
			return d && d.length && d.length > 0 && d[0] === "北京市";
		};
		
		for (var i in alarminfo["data"]) {
			if (filter(alarminfo["data"][i])) {
				beijingAlarm = alarminfo["data"][i];
				break;
			}
		}
		
//		Log("beijingAlarm:" + beijingAlarm);
		
		// 查找北京市 预警详情
		if (beijingAlarm && beijingAlarm.length && beijingAlarm.length > 0) {
			var beijingAlarmUrl = beijingAlarm[1];
			
//			Log(beijingAlarmUrl);
			
			var alarmData = HttpGet({
				url: "http://product.weather.com.cn/alarm/webdata/" + beijingAlarmUrl + "?_=" + (new Date()).getTime()
			});
			
			if (alarmData) {
//				Log(alarmData);
				eval(alarmData);
				
				// 检查采集到的预警信息是否符合要求
				if (alarminfo && alarminfo["head"] && alarminfo["ALERTID"]) {
					
					var weatherInfo = {
							"__DATATYPE" : "ISCS_WEATHER_ALARM",
							
							"dataSource" : "中国天气网",
							"iconUrl" : "http://www.weather.com.cn/m2/i/about/alarmpic/" + alarminfo["TYPECODE"] + alarminfo["LEVELCODE"] + ".gif",
							"sourceUrl" : "http://www.weather.com.cn/alarm/newalarmcontent.shtml?file=" + beijingAlarmUrl,
							"region" : alarminfo["PROVINCE"],
							"obsTime" : alarminfo["ISSUETIME"],
							"beginTime" : alarminfo["ISSUETIME"],
							"endTime" : alarminfo["RELIEVETIME"],
							
							"disType" : alarminfo["SIGNALTYPE"],
							"warnGrade" : alarminfo["SIGNALLEVEL"],
							"warnLevel" : alarminfo["LEVELCODE"],
							"warnContent" : alarminfo["ISSUECONTENT"]
					};

//					Log(toJsonString(weatherInfo));
					
					PostKfk({
						message: weatherInfo
					});
				}
			}
		} else {
			Log("无预警");
		}
	}
};
