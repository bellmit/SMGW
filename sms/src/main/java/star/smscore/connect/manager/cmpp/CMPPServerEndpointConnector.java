package star.smscore.connect.manager.cmpp;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.AbstractServerEndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.session.AbstractSessionStateManager;
import star.smscore.session.cmpp.SessionLoginManager;
/**
 *@author Lihuanghe(18852780@qq.com)
 */
public class CMPPServerEndpointConnector extends AbstractServerEndpointConnector {
	private static final Logger logger = LoggerFactory.getLogger(CMPPServerEndpointConnector.class);
	public CMPPServerEndpointConnector(EndpointEntity e) {
		super(e);
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		super.doinitPipeLine(pipeline);
		CMPPCodecChannelInitializer codec = null;
		if (getEndpointEntity() instanceof CMPPEndpointEntity) {
			pipeline.addLast(GlobalConstance.IdleCheckerHandlerName,
					new IdleStateHandler(0, 0, getEndpointEntity().getIdleTimeSec(), TimeUnit.SECONDS));
			codec = new CMPPCodecChannelInitializer(((CMPPEndpointEntity) getEndpointEntity()).getVersion());

		} else {
			pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
			codec = new CMPPCodecChannelInitializer();
		}

		pipeline.addLast("CmppServerIdleStateHandler", GlobalConstance.idleHandler);
		pipeline.addLast(codec.pipeName(), codec);

		pipeline.addLast("sessionLoginManager", new SessionLoginManager(getEndpointEntity()));
	}

}
