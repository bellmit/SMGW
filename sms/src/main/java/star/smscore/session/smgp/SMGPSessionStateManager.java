package star.smscore.session.smgp;

import java.util.concurrent.ConcurrentMap;

import star.smscore.codec.cmpp.msg.Message;
import star.smscore.codec.smgp.msg.SMGPActiveTestMessage;
import star.smscore.codec.smgp.msg.SMGPBaseMessage;
import star.smscore.codec.smgp.msg.SMGPSubmitRespMessage;
import star.smscore.common.storedMap.VersionObject;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.session.AbstractSessionStateManager;

public class SMGPSessionStateManager extends AbstractSessionStateManager<Integer, SMGPBaseMessage> {

	public SMGPSessionStateManager(EndpointEntity entity, ConcurrentMap<Integer, VersionObject<SMGPBaseMessage>> storeMap, boolean preSend) {
		super(entity, storeMap, preSend);
	}

	@Override
	protected Integer getSequenceId(SMGPBaseMessage msg) {
		
		return msg.getSequenceNo();
	}

	@Override
	protected boolean needSendAgainByResponse(SMGPBaseMessage req, SMGPBaseMessage res) {
		if(res instanceof SMGPSubmitRespMessage){
			SMGPSubmitRespMessage submitresp = (SMGPSubmitRespMessage)res;
			//TODO 电信的超速错误码现在不知道
			return false;
		}
		return false;
	}
	
	protected boolean closeWhenRetryFailed(SMGPBaseMessage req) {
		if(req instanceof SMGPActiveTestMessage) {
			return true;
		}
		return getEntity().isCloseWhenRetryFailed();
	};

}
