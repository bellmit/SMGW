/** 
* 自定义HashMap对象 
*/
function HashMap() {
	var obj = {};
	this.put = function(key, value) {
		obj[key] = value; //把键值绑定到obj对象上
	}
	//size方法，获取Map容器的个数
	this.size = function() {
		var count = 0;
		for (var attr in obj) {
			count++;
		}
		return count;
	}
	//get方法，根据key获取value的值
	this.get = function(key) {
		if (obj[key] || obj[key] === 0 || obj[key] === false) {
			return obj[key]
		} else {
			return null;
		}
	}
	//remove方法,删除方法
	this.remove = function(key) {
		if (obj[key] || obj[key] === 0 || obj[key] === false) {
			delete obj[key]
		}
	}
	//each方法,遍历方法
	this.eachMap = function(callBack) {
		for (var attr in obj) {
			callBack(attr, obj[attr])
		}
	}
}