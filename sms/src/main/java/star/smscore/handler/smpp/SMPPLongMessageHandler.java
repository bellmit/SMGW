package star.smscore.handler.smpp;

import org.marre.sms.SmsMessage;

import star.smscore.BaseMessage;
import star.smscore.codec.cmpp.wap.AbstractLongMessageHandler;
import star.smscore.codec.smpp.msg.BaseSm;
import star.smscore.codec.smpp.msg.DeliverSm;
import star.smscore.codec.smpp.msg.DeliverSmReceipt;
import star.smscore.codec.smpp.msg.SubmitSm;
import star.smscore.common.NotSupportedException;
import star.smscore.connect.manager.EndpointEntity;

public class SMPPLongMessageHandler extends AbstractLongMessageHandler<BaseSm> {
	
	
	public SMPPLongMessageHandler(EndpointEntity entity) {
		super(entity);
	}

	@Override
	protected BaseMessage response(BaseSm msg) {
		return msg.createResponse();
	}

	@Override
	protected boolean needHandleLongMessage(BaseSm msg) {
		
		if(msg instanceof DeliverSmReceipt)
		{
			return false;
		}
		return true;
	}

	@Override
	protected String generateFrameKey(BaseSm msg) throws Exception{
		if(msg instanceof SubmitSm){
			return msg.getDestAddress().getAddress()+msg.getSourceAddress().getAddress();
		}else if(msg instanceof DeliverSm){
			return msg.getSourceAddress().getAddress()+msg.getDestAddress().getAddress();
		}else{
			throw new NotSupportedException("not support LongMessage Type  "+ msg.getClass());
		}
	}

	@Override
	protected void resetMessageContent(BaseSm t, SmsMessage content) {
		t.setSmsMsg(content);
	}
	
}