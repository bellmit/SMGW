package star.smscore.session.sgip;

import java.util.concurrent.ConcurrentMap;

import star.smscore.codec.cmpp.msg.DefaultMessage;
import star.smscore.codec.cmpp.msg.Message;
import star.smscore.common.storedMap.VersionObject;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.connect.manager.sgip.SgipEndpointEntity;
import star.smscore.session.AbstractSessionStateManager;

import io.netty.util.concurrent.Promise;

public class SgipSessionStateManager extends AbstractSessionStateManager<Long, Message> {

	public SgipSessionStateManager(EndpointEntity entity, ConcurrentMap<Long, VersionObject<Message>> storeMap, boolean preSend) {
		super(entity, storeMap, preSend);
	}

	@Override
	protected Long getSequenceId(Message msg) {
		long seq = msg.getHeader().getSequenceId();
		long time = msg.getTimestamp();
		long node = msg.getHeader().getNodeId();
		return Long.valueOf(node << 32 | (seq & 0x0ffffffff));
	}

	@Override
	protected boolean needSendAgainByResponse(Message req, Message res) {
		return false;
	}

	@Override
	protected boolean closeWhenRetryFailed(Message req) {
		return getEntity().isCloseWhenRetryFailed();
	}
	
	//同步调用时，要设置sgip的NodeId
	public Promise<Message> writeMessagesync(Message msg){
		SgipEndpointEntity entity = (SgipEndpointEntity)getEntity();
		if(msg instanceof DefaultMessage && entity instanceof SgipEndpointEntity) {
			DefaultMessage message = (DefaultMessage)msg;
			if(message.isRequest() && entity.getNodeId()!=0 && message.getHeader().getNodeId()==0) {
				message.getHeader().setNodeId(entity.getNodeId());
			}
		}
		return super.writeMessagesync(msg);
	}

}
