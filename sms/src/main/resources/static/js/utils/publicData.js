let publicData = {
	//保存用户
	saveLoginUser : function(loginUserObject) {
		window.localStorage.setItem("key_" + "loginUser", JSON.stringify(loginUserObject));
	},
	//得到用户
	getLoginUser : function() {
		let loginUser = window.localStorage.getItem("key_" + "loginUser");
		let loginUserJson = JSON.parse(loginUser);
		return loginUserJson;
	},
	//保存参数
	saveStorageData : function(dataKey, dataValue) {
		window.localStorage.setItem("key_" + dataKey, JSON.stringify(dataValue));
	},
	//得到参数
	getStorageData : function(dataKey) {
		let dataValue = window.localStorage.getItem("key_" + dataKey);
		let dataValueJson = JSON.parse(dataValue);
		return dataValueJson;
	},
	//得到传递参数
	getQueryVariable : function(variable) {
		var query = window.location.search.substring(1);
		var vars = query.split("&");
		for (var i = 0; i < vars.length; i++) {
			var pair = vars[i].split("=");
			if (pair[0] == variable) {
				return pair[1];
			}
		}
		return (false);
	}
}
module.exports = publicData