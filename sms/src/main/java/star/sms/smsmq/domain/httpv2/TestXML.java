package star.sms.smsmq.domain.httpv2;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class TestXML {
	/*//sendResponse 测试
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
			+"<returnsms>"
			+"<returnstatus>status</returnstatus>"
			+"<message>message</message>"
			+"<remainpoint>remainpoint</remainpoint>"
			+"<taskID>taskID</taskID>"
			+"<successCounts>successCounts</successCounts>"
			+"</returnsms>";
        ObjectMapper mapper = new XmlMapper();
        Object po = null;
        po = mapper.readValue(xml, HttpV2SendResponse.class);
        System.out.println(po.toString());
	}*/
	
	/*//overageResponse 测试
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
			+"<returnsms>"
			+"<returnstatus>status</returnstatus>"
			+"<message>message</message>"
			+"<payinfo>payinfo</payinfo>"
			+"<overage>overage</overage>"
			+"<sendTotal>sendTotal</sendTotal>"
			+"</returnsms>";
        ObjectMapper mapper = new XmlMapper();
        Object po = null;
        po = mapper.readValue(xml, HttpV2OverageResponse.class);
        System.out.println(po.toString());
	}*/
/*	
	//checkkeywordResponse 测试
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
				+"<returnsms>"
				+"<message>message</message>"
				+"</returnsms>";
		ObjectMapper mapper = new XmlMapper();
		Object po = null;
		po = mapper.readValue(xml, HttpV2CheckkeywordResponse.class);
		System.out.println(po.toString());
	}*/
/*	
	//statusResponse 测试
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
				+"<returnsms>"
				+"<statusbox>"
				+"<mobile>15023239810</mobile>"
				+"<taskid>1212</taskid>"
				+"<status>10</status>"
				+"<receivetime>2011-12-02 22:12:11</receivetime>"
				+"<errorcode>DELIVRD</errorcode>"
				+"<extno>01</extno>"
				+"</statusbox>"
				+"<statusbox>"
				+"<mobile>15023239811</mobile>"
				+"<taskid>1212</taskid>"
				+"<status>20</status>"
				+"<receivetime>2011-12-02 22:12:11</receivetime>"
				+"<errorcode>2</errorcode>"
				+"<extno></extno>"
				+"</statusbox>"
				+"<errorstatus>"
				+"<error>1</error>"
				+"<remark>用户名或密码不能为空</remark>"
				+"</errorstatus>"
				+"</returnsms>";
		ObjectMapper mapper = new XmlMapper();
		*//**格式化状态查询xml，不然无法封装成list start**//*
		if(xml.indexOf("<statusbox>")>0) xml = xml.substring(0,xml.indexOf("<statusbox>"))+"<statusboxlist>"+xml.substring(xml.indexOf("<statusbox>"));
		if(xml.lastIndexOf("</statusbox>")>0) xml = xml.substring(0,xml.lastIndexOf("</statusbox>")+12)+"</statusboxlist>"+xml.substring(xml.lastIndexOf("</statusbox>")+12);
		if(xml.indexOf("<errorstatus>")>0) xml = xml.substring(0,xml.indexOf("<errorstatus>"))+"<errorstatuslist>"+xml.substring(xml.indexOf("<errorstatus>"));
		if(xml.lastIndexOf("</errorstatus>")>0) xml = xml.substring(0,xml.lastIndexOf("</errorstatus>")+14)+"</errorstatuslist>"+xml.substring(xml.lastIndexOf("</errorstatus>")+14);
		*//**格式化状态查询xml，不然无法封装成list end**//*
//		System.out.println(xml);
		Object po = null;
		po = mapper.readValue(xml, HttpV2StatusResponse.class);
		System.out.println(po.toString());
	}*/
	
	//queryResponse 测试
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
				+"<returnsms>"
				+"<callbox>"
				+"<mobile>15023239810</mobile>"
				+"<taskid>1212</taskid>"
				+"<content>你好，我不需要</content>"
				+"<receivetime>2011-12-02 22:12:11</receivetime>"
				+"<extno>01</extno>"
				+"</callbox>"
				+"<callbox>"
				+"<mobile>15023239811</mobile>"
				+"<taskid>1212</taskid>"
				+"<content>你好，本次活动路线是怎么的</content>"
				+"<receivetime>2011-12-02 22:12:11</receivetime>"
				+"<extno>01</extno>"
				+"</callbox>"
				
				  +"<errorstatus>" +"<error>1</error>" +"<remark>用户名或密码不能为空</remark>"
				  +"</errorstatus>"
				 
				+"</returnsms>";
		ObjectMapper mapper = new XmlMapper();
		/**格式化状态查询xml，不然无法封装成list start**/
		if(xml.indexOf("<callbox>")>0) xml = xml.substring(0,xml.indexOf("<callbox>"))+"<callboxlist>"+xml.substring(xml.indexOf("<callbox>"));
		if(xml.lastIndexOf("</callbox>")>0) xml = xml.substring(0,xml.lastIndexOf("</callbox>")+10)+"</callboxlist>"+xml.substring(xml.lastIndexOf("</callbox>")+10);
		if(xml.indexOf("<errorstatus>")>0) xml = xml.substring(0,xml.indexOf("<errorstatus>"))+"<errorstatuslist>"+xml.substring(xml.indexOf("<errorstatus>"));
		if(xml.lastIndexOf("</errorstatus>")>0) xml = xml.substring(0,xml.lastIndexOf("</errorstatus>")+14)+"</errorstatuslist>"+xml.substring(xml.lastIndexOf("</errorstatus>")+14);
		/**格式化状态查询xml，不然无法封装成list end**/
//		System.out.println(xml);
		HttpV2QueryResponse po = mapper.readValue(xml, HttpV2QueryResponse.class);
		System.out.println(po.toString());
		//System.out.println(po.getErrorstatusList().size());
		//System.out.println(po.getCallboxList().size());
	}
	
}
