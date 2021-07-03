package star.sms.logs.domain;

import org.apache.commons.lang3.StringUtils;

/**
 * 操作日志枚举信息
 * @author
 */
public enum LogsEnum{
	LOG1("1", "账号上线"), 
	LOG2("2", "账号下线"), 
	LOG3("3", "修改昵称"),
	LOG4("4", "修改头像"),
	LOG5("5", "修改签名"),
	LOG6("6", "修改密码"),
	LOG7("7", "清除朋友圈"),
	LOG8("8", "关注公众号"),
	LOG9("9", "查看文章"),
	LOG10("10", "文章点赞"),
	LOG11("11","阅读并点赞");

	// 成员变量
    private String name;
    private String code;
	 // 构造方法
    private LogsEnum(String code,String name) {
        this.name = name;
        this.code = code;
    }
    
 	//获取编码
    public static String getCode(String name) {
    	if(StringUtils.isNotEmpty(name)) {
    		for (LogsEnum c : LogsEnum.values()) {
    			if (c.getName().equals(name) || name.indexOf(c.getName())>=0) {
    				return c.code;
    			}
    		}
    	}
        return null;
    }
    
 	//获取名称
    public static String getName(String code) {
        for (LogsEnum c : LogsEnum.values()) {
	        if (c.getCode().equals(code)) {
	            return c.name;
	        }
        }
        return null;
    }
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
