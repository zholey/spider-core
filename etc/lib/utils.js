

/**
 * 将指定的数据拼接为一个JSON格式字符串
 */
var toJsonString = function (json) {

	var str = "";

	if (json != undefined && json != null) {

		if (json instanceof Array) {

			str = "[";

			for (var i = 0; i < json.length; i++) {
				str += toJsonString(json[i]) + ",";
			}

			str = str.replace(/\,\s*$/i, "") + "]";

		} else if (typeof json == "object") {

			str = "{";

			for (var p in json) {
				str += "\"" + p + "\":" + toJsonString(json[p]) + ",";
			}

			str = str.replace(/\,\s*$/i, "") + "}";

		} else {
			json = json.toString();

			json = json.replace(/\"/g, "\\\"");
			json = json.replace(/\r\n/g, "<br/>");
			json = json.replace(/\r/g, "\\r");
			json = json.replace(/\n/g, "\\n");

			str = "\"" + json + "\"";
		}

	} else {
		str = "\"\"";
	}

	return str;
};

var toGB2312 = function(str) {
	return unescape(str.replace(/\\u/gi,'%u'))
};
