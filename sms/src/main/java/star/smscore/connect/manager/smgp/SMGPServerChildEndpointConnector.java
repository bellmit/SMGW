package star.smscore.connect.manager.smgp;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.AbstractEndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.handler.smgp.SMGPActiveTestMessageHandler;
import star.smscore.handler.smgp.SMGPActiveTestRespMessageHandler;
import star.smscore.handler.smgp.SMGPDeliverLongMessageHandler;
import star.smscore.handler.smgp.SMGPExitMessageHandler;
import star.smscore.handler.smgp.SMGPExitRespMessageHandler;
import star.smscore.handler.smgp.SMGPSubmitLongMessageHandler;
import star.smscore.session.AbstractSessionStateManager;
import star.smscore.session.smgp.SMGPSessionStateManager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

public class SMGPServerChildEndpointConnector extends AbstractEndpointConnector{
	private static final Logger logger = LoggerFactory.getLogger(SMGPServerChildEndpointConnector.class);
	public SMGPServerChildEndpointConnector(EndpointEntity endpoint) {
		super(endpoint);
	}

	@Override
	public ChannelFuture open() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SslContext createSslCtx() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity entity) {
		
		// 修改连接空闲时间,使用server.xml里配置的连接空闲时间生效
		if (entity instanceof SMGPServerChildEndpointEntity) {
			ChannelHandler handler = pipe.get(GlobalConstance.IdleCheckerHandlerName);
			if (handler != null) {
				pipe.replace(handler, GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
			}
		}
		//处理长短信
		pipe.addLast("SMGPDeliverLongMessageHandler", new SMGPDeliverLongMessageHandler(entity));
		pipe.addLast("SMGPSubmitLongMessageHandler",  new SMGPSubmitLongMessageHandler(entity));
		pipe.addLast("SMGPActiveTestMessageHandler",new SMGPActiveTestMessageHandler());
		pipe.addLast("SMGPActiveTestRespMessageHandler",new SMGPActiveTestRespMessageHandler());
		pipe.addLast("SMGPExitRespMessageHandler", new SMGPExitRespMessageHandler());
		pipe.addLast("SMGPExitMessageHandler", new SMGPExitMessageHandler());
		
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		
		
	}

	@Override
	protected void initSslCtx(Channel ch, EndpointEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, ConcurrentMap storeMap, boolean preSend) {
		return new SMGPSessionStateManager(entity, storeMap, preSend);
	}

}
