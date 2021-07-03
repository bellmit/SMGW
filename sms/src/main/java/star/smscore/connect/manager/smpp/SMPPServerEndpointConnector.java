package star.smscore.connect.manager.smpp;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.AbstractServerEndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.session.AbstractSessionStateManager;
import star.smscore.session.smpp.SMPPSessionLoginManager;

public class SMPPServerEndpointConnector extends AbstractServerEndpointConnector{

	public SMPPServerEndpointConnector(EndpointEntity e) {
		super(e);
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		super.doinitPipeLine(pipeline);
		EndpointEntity entity = getEndpointEntity();
		pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
		pipeline.addLast("SmppServerIdleStateHandler", GlobalConstance.smppidleHandler);
		pipeline.addLast(SMPPCodecChannelInitializer.pipeName(), new SMPPCodecChannelInitializer());
		pipeline.addLast("sessionLoginManager", new SMPPSessionLoginManager(getEndpointEntity()));
		
	}
}
