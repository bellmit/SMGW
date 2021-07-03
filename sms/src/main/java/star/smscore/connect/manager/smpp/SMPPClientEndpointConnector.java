package star.smscore.connect.manager.smpp;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.AbstractClientEndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.handler.smpp.EnquireLinkMessageHandler;
import star.smscore.handler.smpp.EnquireLinkRespMessageHandler;
import star.smscore.handler.smpp.SMPPLongMessageHandler;
import star.smscore.handler.smpp.UnbindMessageHandler;
import star.smscore.handler.smpp.UnbindRespMessageHandler;
import star.smscore.session.AbstractSessionStateManager;
import star.smscore.session.smpp.SMPPSessionLoginManager;
import star.smscore.session.smpp.SMPPSessionStateManager;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

public class SMPPClientEndpointConnector extends AbstractClientEndpointConnector {


	private static final Logger logger = LoggerFactory.getLogger(SMPPClientEndpointConnector.class);
	
	public SMPPClientEndpointConnector(EndpointEntity endpoint) {
		super(endpoint);
	}
	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, ConcurrentMap storeMap, boolean preSend) {
		return new SMPPSessionStateManager(entity, storeMap, preSend);
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity entity) {
		pipe.addLast( "SMPPLongMessageHandler", new SMPPLongMessageHandler(entity));
		pipe.addLast("EnquireLinkMessageHandler",new EnquireLinkMessageHandler());
		pipe.addLast("EnquireLinkRespMessageHandler",new EnquireLinkRespMessageHandler());
		pipe.addLast("UnbindRespMessageHandler", new UnbindRespMessageHandler());
		pipe.addLast("UnbindMessageHandler", new UnbindMessageHandler());
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		EndpointEntity entity = getEndpointEntity();
		pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
		pipeline.addLast("SmppServerIdleStateHandler", GlobalConstance.smppidleHandler);
		pipeline.addLast(SMPPCodecChannelInitializer.pipeName(), new SMPPCodecChannelInitializer());
		pipeline.addLast("sessionLoginManager", new SMPPSessionLoginManager(getEndpointEntity()));
	}

}
