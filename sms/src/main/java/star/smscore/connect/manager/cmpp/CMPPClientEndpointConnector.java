package star.smscore.connect.manager.cmpp;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.AbstractClientEndpointConnector;
import star.smscore.connect.manager.ClientEndpoint;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.handler.cmpp.CMPPDeliverLongMessageHandler;
import star.smscore.handler.cmpp.CMPPSubmitLongMessageHandler;
import star.smscore.handler.cmpp.ReWriteSubmitMsgSrcHandler;
import star.smscore.session.AbstractSessionStateManager;
import star.smscore.session.cmpp.SessionLoginManager;
import star.smscore.session.cmpp.SessionStateManager;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

/**
 *@author Lihuanghe(18852780@qq.com)
 */

public class CMPPClientEndpointConnector extends AbstractClientEndpointConnector {
	private static final Logger logger = LoggerFactory.getLogger(CMPPClientEndpointConnector.class);
	

	
	public CMPPClientEndpointConnector(CMPPClientEndpointEntity e)
	{
		super(e);
		
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity cmppentity) {
		CMPPEndpointEntity entity = (CMPPEndpointEntity)cmppentity;
		
		if (entity instanceof ClientEndpoint) {
			pipe.addAfter(GlobalConstance.codecName, "reWriteSubmitMsgSrcHandler", new ReWriteSubmitMsgSrcHandler(entity) );
		}
		//处理长短信
		pipe.addLast( "CMPPDeliverLongMessageHandler", new CMPPDeliverLongMessageHandler(entity));
		pipe.addLast("CMPPSubmitLongMessageHandler",  new CMPPSubmitLongMessageHandler(entity));
		
		pipe.addLast("CmppActiveTestRequestMessageHandler", GlobalConstance.activeTestHandler);
		pipe.addLast("CmppActiveTestResponseMessageHandler", GlobalConstance.activeTestRespHandler);
		pipe.addLast("CmppTerminateRequestMessageHandler", GlobalConstance.terminateHandler);
		pipe.addLast("CmppTerminateResponseMessageHandler", GlobalConstance.terminateRespHandler);
		
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		CMPPCodecChannelInitializer codec = null;
		EndpointEntity entity = getEndpointEntity();
		if (entity instanceof CMPPEndpointEntity) {
			pipeline.addLast(GlobalConstance.IdleCheckerHandlerName,
					new IdleStateHandler(0, 0, ((CMPPEndpointEntity) getEndpointEntity()).getIdleTimeSec(), TimeUnit.SECONDS));
			
			codec = new CMPPCodecChannelInitializer(((CMPPEndpointEntity) getEndpointEntity()).getVersion());

		} else {
			pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
			
			codec = new CMPPCodecChannelInitializer();
		}
		pipeline.addLast("CmppServerIdleStateHandler", GlobalConstance.idleHandler);
		pipeline.addLast(codec.pipeName(), codec);
		pipeline.addLast("sessionLoginManager", new SessionLoginManager(getEndpointEntity()));
	}



	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, ConcurrentMap storeMap,
			boolean preSend) {
		return new SessionStateManager(entity, storeMap, preSend);
	}

}
