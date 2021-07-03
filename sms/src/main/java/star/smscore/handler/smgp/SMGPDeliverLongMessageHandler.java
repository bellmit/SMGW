package star.smscore.handler.smgp;

import org.marre.sms.SmsMessage;

import star.smscore.BaseMessage;
import star.smscore.codec.cmpp.wap.AbstractLongMessageHandler;
import star.smscore.codec.smgp.msg.SMGPDeliverMessage;
import star.smscore.codec.smgp.msg.SMGPDeliverRespMessage;
import star.smscore.connect.manager.EndpointEntity;

import io.netty.channel.ChannelHandler.Sharable;
@Sharable
public class SMGPDeliverLongMessageHandler extends AbstractLongMessageHandler<SMGPDeliverMessage> {

	public SMGPDeliverLongMessageHandler(EndpointEntity entity) {
		super(entity);
	}

	@Override
	protected BaseMessage response( SMGPDeliverMessage msg) {
		
		//短信片断未接收完全，直接给网关回复resp，等待其它片断
		SMGPDeliverRespMessage responseMessage = new SMGPDeliverRespMessage();
		responseMessage.setSequenceNo(msg.getSequenceNo());
		responseMessage.setStatus(0);
		responseMessage.setMsgId(msg.getMsgId());
		return responseMessage;
	}

	@Override
	protected boolean needHandleLongMessage(SMGPDeliverMessage msg) {
	
		return !msg.isReport();
	}

	@Override
	protected String generateFrameKey(SMGPDeliverMessage msg) {
		return msg.getSrcTermId()+msg.getDestTermId();
	}

	@Override
	protected void resetMessageContent(SMGPDeliverMessage t, SmsMessage content) {
		t.setMsgContent(content);
	}

}
