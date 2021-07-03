package star.smscore.handler.smgp;

import org.apache.commons.lang3.StringUtils;
import org.marre.sms.SmsMessage;

import star.smscore.BaseMessage;
import star.smscore.codec.cmpp.wap.AbstractLongMessageHandler;
import star.smscore.codec.smgp.msg.MsgId;
import star.smscore.codec.smgp.msg.SMGPSubmitMessage;
import star.smscore.codec.smgp.msg.SMGPSubmitRespMessage;
import star.smscore.connect.manager.EndpointEntity;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class SMGPSubmitLongMessageHandler extends AbstractLongMessageHandler<SMGPSubmitMessage> {

	public SMGPSubmitLongMessageHandler(EndpointEntity entity) {
		super(entity);
	}

	@Override
	protected BaseMessage response(SMGPSubmitMessage msg) {
		//短信片断未接收完全，直接给网关回复resp，等待其它片断
		SMGPSubmitRespMessage responseMessage = new SMGPSubmitRespMessage();
		responseMessage.setSequenceNo(msg.getSequenceNo());
		responseMessage.setStatus(0);
		responseMessage.setMsgId( new MsgId());
		return responseMessage;
	}

	@Override
	protected boolean needHandleLongMessage(SMGPSubmitMessage msg) {
		return true;
	}

	@Override
	protected String generateFrameKey(SMGPSubmitMessage msg) {
		return StringUtils.join(msg.getDestTermIdArray(), "|")+msg.getSrcTermId();
	}
	
	@Override
	protected void resetMessageContent(SMGPSubmitMessage t, SmsMessage content) {
		t.setMsgContent(content);
	}

}
