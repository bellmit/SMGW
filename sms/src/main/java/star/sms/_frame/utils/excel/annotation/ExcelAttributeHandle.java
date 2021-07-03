package star.sms._frame.utils.excel.annotation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import star.sms._frame.utils.RegUtil;

/**
 * @Description
 * @create 2019-03-15 13:47
 */
public class ExcelAttributeHandle {

    public static final String TIME="time";
    public static final String IS_DELETE="is_delete";
    public static final String ONLINE="online";
    public static final String OUTFLAG="outFlag";
    public static final String ISPUBLIC="isPublic";
    public static final String BASICSTATE="basicState";
    public static final String SENDSTATUS="sendStatus";

    public static String handle(String type, Object val){
        String format= null;
        switch (type){
            case "time": {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (val instanceof String) {
                    String regex = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).0";
                    //编译
                    Pattern pattern = Pattern.compile(regex);
                    //匹配
                    Matcher matcher = pattern.matcher(val.toString());
                    //得到匹配结果，布尔值
                    boolean matches = matcher.matches();
                    if (matches) {
                        List list = RegUtil.getMatcher(regex, val.toString());
                        format = list.get(0).toString();
                    } else {
                        format = (String) val;
                    }
                } else {
                    format = simpleDateFormat.format(val);
                }
                break;
            }
            case "online": {
                format = Integer.valueOf(val.toString()) == 1 ? "在库" : "离线";
                break;
            }
            case "outFlag": {
            	format = Integer.valueOf(val.toString()) == 1 ? "借出" : "在库";
            	break;
            }
            case "isPublic": {
            	format = Integer.valueOf(val.toString()) == 1 ? "公开" : "保密";
            	break;
            }
            case "is_delete": {
                format = Integer.valueOf(val.toString()) == 0 ? "否" : "是";
                break;
            }
            case "sendStatus": {
            	int state=Integer.valueOf(val.toString());
            	if(state==0){
            		format="待发送";
            	}else if(state==1){
            		format="发送成功";
            	}else if(state==2){
            		format="发送失败";
            	}else if(state==3){
            		format="已终止";
            	}else if(state==4){
            		format="发送中";
            	} 
                break;
            }
            case "basicState": {
            	int state=Integer.valueOf(val.toString());
            	if(state==0){
            		format="待受理";
            	}else if(state==1){
            		format="不受理";
            	}else if(state==2){
            		format="待指派";
            	}else if(state==3){
            		format="待确认";
            	}else if(state==4){
            		format="处理中";
            	}else if(state==5){
            		format="终止审核中";
            	}else if(state==6){
            		format="已终止";
            	}else if(state==7){
            		format="已归档";
            	}else if(state==8){
            		format="归档审批中";
            	} 
            	break;
            }
            default:break;
        }
        return format;
    }
}
