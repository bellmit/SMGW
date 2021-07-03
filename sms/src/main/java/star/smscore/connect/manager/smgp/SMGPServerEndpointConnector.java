package star.smscore.connect.manager.smgp;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.AbstractServerEndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.session.AbstractSessionStateManager;
import star.smscore.session.smgp.SMGPSessionLoginManager;

public class SMGPServerEndpointConnector extends AbstractServerEndpointConnector{

	public SMGPServerEndpointConnector(EndpointEntity e) {
		super(e);
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		super.doinitPipeLine(pipeline);
		EndpointEntity entity = getEndpointEntity();
		pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
		pipeline.addLast("SmgpServerIdleStateHandler", GlobalConstance.smgpidleHandler);
		pipeline.addLast(SMGPCodecChannelInitializer.pipeName(), new SMGPCodecChannelInitializer(0x30));  //默认使用3.0协议，用户登陆后再更换为正确的协议
		pipeline.addLast("sessionLoginManager", new SMGPSessionLoginManager(getEndpointEntity()));
		
	}

}
