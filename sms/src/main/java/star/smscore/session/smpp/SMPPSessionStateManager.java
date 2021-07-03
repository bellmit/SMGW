package star.smscore.session.smpp;

import java.util.concurrent.ConcurrentMap;

import star.smscore.codec.cmpp.msg.Message;
import star.smscore.codec.smpp.msg.EnquireLink;
import star.smscore.codec.smpp.msg.Pdu;
import star.smscore.common.storedMap.VersionObject;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.session.AbstractSessionStateManager;

public class SMPPSessionStateManager extends AbstractSessionStateManager<Integer, Pdu> {

	public SMPPSessionStateManager(EndpointEntity entity, ConcurrentMap<Integer, VersionObject<Pdu>> storeMap, boolean preSend) {
		super(entity, storeMap, preSend);
	}

	@Override
	protected Integer getSequenceId(Pdu msg) {
		
		return msg.getSequenceNumber();
	}

	@Override
	protected boolean needSendAgainByResponse(Pdu req, Pdu res) {
		return false;
	}
	protected boolean closeWhenRetryFailed(Pdu req) {
		if(req instanceof EnquireLink) {
			return true;
		}
		return getEntity().isCloseWhenRetryFailed();
	};
}
