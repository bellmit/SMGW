package star.smscore.handler.cmpp;

import org.apache.commons.lang3.StringUtils;
import org.marre.sms.SmsMessage;

import star.smscore.BaseMessage;
import star.smscore.codec.cmpp.msg.CmppSubmitRequestMessage;
import star.smscore.codec.cmpp.msg.CmppSubmitResponseMessage;
import star.smscore.codec.cmpp.wap.AbstractLongMessageHandler;
import star.smscore.connect.manager.EndpointEntity;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class CMPPSubmitLongMessageHandler extends AbstractLongMessageHandler<CmppSubmitRequestMessage> {

	public CMPPSubmitLongMessageHandler(EndpointEntity entity) {
		super(entity);
	}

	@Override
	protected BaseMessage response( CmppSubmitRequestMessage msg) {
		//短信片断未接收完全，直接给网关回复resp，等待其它片断
		CmppSubmitResponseMessage responseMessage = new CmppSubmitResponseMessage(msg.getHeader());
		responseMessage.setResult(0);
		return responseMessage;
	}

	@Override
	protected boolean needHandleLongMessage(CmppSubmitRequestMessage msg) {
		return true;
	}

	@Override
	protected String generateFrameKey(CmppSubmitRequestMessage msg) {
		return StringUtils.join(msg.getDestterminalId(), "|")+msg.getSrcId();
	}

	@Override
	protected void resetMessageContent(CmppSubmitRequestMessage t, SmsMessage content) {
		t.setMsg(content);
	}

}
