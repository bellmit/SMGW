package star.smscore.connect.manager.sgip;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.AbstractClientEndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.handler.sgip.ReWriteNodeIdHandler;
import star.smscore.handler.sgip.SgipDeliverLongMessageHandler;
import star.smscore.handler.sgip.SgipSubmitLongMessageHandler;
import star.smscore.handler.sgip.SgipUnbindRequestMessageHandler;
import star.smscore.handler.sgip.SgipUnbindResponseMessageHandler;
import star.smscore.session.AbstractSessionStateManager;
import star.smscore.session.sgip.SgipSessionLoginManager;
import star.smscore.session.sgip.SgipSessionStateManager;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

public class SgipClientEndpointConnector extends AbstractClientEndpointConnector {


	private static final Logger logger = LoggerFactory.getLogger(SgipClientEndpointConnector.class);
	
	public SgipClientEndpointConnector(EndpointEntity endpoint) {
		super(endpoint);
	}
	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, ConcurrentMap storeMap, boolean preSend) {
		return new SgipSessionStateManager(entity, storeMap, preSend);
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity entity) {
		pipe.addLast("reWriteNodeIdHandler", new ReWriteNodeIdHandler((SgipEndpointEntity)entity));
		//处理长短信
		pipe.addLast("SgipDeliverLongMessageHandler", new SgipDeliverLongMessageHandler(entity));
		pipe.addLast("SgipSubmitLongMessageHandler",  new SgipSubmitLongMessageHandler(entity));
		pipe.addLast("SgipUnbindResponseMessageHandler", new SgipUnbindResponseMessageHandler());
		pipe.addLast("SgipUnbindRequestMessageHandler", new SgipUnbindRequestMessageHandler());
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		EndpointEntity entity = getEndpointEntity();
		pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
		pipeline.addLast("SgipServerIdleStateHandler", GlobalConstance.sgipidleHandler);
		pipeline.addLast(SgipCodecChannelInitializer.pipeName(), new SgipCodecChannelInitializer());
		pipeline.addLast("sessionLoginManager", new SgipSessionLoginManager(getEndpointEntity()));
	}

}
