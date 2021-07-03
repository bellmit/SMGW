package star.smscore.connect.manager.smgp;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.AbstractClientEndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.handler.smgp.SMGPActiveTestMessageHandler;
import star.smscore.handler.smgp.SMGPActiveTestRespMessageHandler;
import star.smscore.handler.smgp.SMGPDeliverLongMessageHandler;
import star.smscore.handler.smgp.SMGPExitMessageHandler;
import star.smscore.handler.smgp.SMGPExitRespMessageHandler;
import star.smscore.handler.smgp.SMGPSubmitLongMessageHandler;
import star.smscore.session.AbstractSessionStateManager;
import star.smscore.session.smgp.SMGPSessionLoginManager;
import star.smscore.session.smgp.SMGPSessionStateManager;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

public class SMGPClientEndpointConnector extends AbstractClientEndpointConnector {


	private static final Logger logger = LoggerFactory.getLogger(SMGPClientEndpointConnector.class);
	
	public SMGPClientEndpointConnector(EndpointEntity endpoint) {
		super(endpoint);
	}
	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, ConcurrentMap storeMap, boolean preSend) {
		return new SMGPSessionStateManager(entity, storeMap, preSend);
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity entity) {
		//处理长短信
		pipe.addLast( "SMGPDeliverLongMessageHandler", new SMGPDeliverLongMessageHandler(entity));
		pipe.addLast("SMGPSubmitLongMessageHandler",  new SMGPSubmitLongMessageHandler(entity));
		pipe.addLast("SMGPActiveTestMessageHandler",new SMGPActiveTestMessageHandler());
		pipe.addLast("SMGPActiveTestRespMessageHandler",new SMGPActiveTestRespMessageHandler());
		pipe.addLast("SMGPExitRespMessageHandler", new SMGPExitRespMessageHandler());
		pipe.addLast("SMGPExitMessageHandler", new SMGPExitMessageHandler());
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		EndpointEntity entity = getEndpointEntity();
		pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
		pipeline.addLast("SmgpServerIdleStateHandler", GlobalConstance.smgpidleHandler);
		pipeline.addLast(SMGPCodecChannelInitializer.pipeName(), new SMGPCodecChannelInitializer((int)((SMGPEndpointEntity)entity).getClientVersion()));
		pipeline.addLast("sessionLoginManager", new SMGPSessionLoginManager(getEndpointEntity()));
	}

}
