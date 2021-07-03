package star.smscore.handler.sgip;

import org.marre.sms.SmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.BaseMessage;
import star.smscore.codec.cmpp.wap.AbstractLongMessageHandler;
import star.smscore.codec.sgip12.msg.SgipDeliverRequestMessage;
import star.smscore.codec.sgip12.msg.SgipDeliverResponseMessage;
import star.smscore.connect.manager.EndpointEntity;

import io.netty.channel.ChannelHandler.Sharable;
@Sharable
public class SgipDeliverLongMessageHandler extends AbstractLongMessageHandler<SgipDeliverRequestMessage> {
	public SgipDeliverLongMessageHandler(EndpointEntity entity) {
		super(entity);
	}

	private final Logger logger = LoggerFactory.getLogger(SgipDeliverLongMessageHandler.class);
	@Override
	protected BaseMessage response(SgipDeliverRequestMessage msg) {
		
		//短信片断未接收完全，直接给网关回复resp，等待其它片断
		SgipDeliverResponseMessage responseMessage = new SgipDeliverResponseMessage(msg.getHeader());
		responseMessage.setResult((short)0);
		return responseMessage;
	}

	@Override
	protected boolean needHandleLongMessage(SgipDeliverRequestMessage msg) {
	
		return true;
	}

	@Override
	protected String generateFrameKey(SgipDeliverRequestMessage msg) {
		return msg.getUsernumber()+msg.getSpnumber();
	}

	@Override
	protected void resetMessageContent(SgipDeliverRequestMessage t, SmsMessage content) {
		t.setMsgContent(content);
	}

}
