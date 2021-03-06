package star.smscore.connect.manager.smpp;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.AbstractEndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.handler.smpp.EnquireLinkMessageHandler;
import star.smscore.handler.smpp.EnquireLinkRespMessageHandler;
import star.smscore.handler.smpp.SMPPLongMessageHandler;
import star.smscore.handler.smpp.UnbindMessageHandler;
import star.smscore.handler.smpp.UnbindRespMessageHandler;
import star.smscore.session.AbstractSessionStateManager;
import star.smscore.session.smpp.SMPPSessionStateManager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

public class SMPPServerChildEndpointConnector extends AbstractEndpointConnector{
	private static final Logger logger = LoggerFactory.getLogger(SMPPServerChildEndpointConnector.class);
	public SMPPServerChildEndpointConnector(EndpointEntity endpoint) {
		super(endpoint);
	}

	@Override
	public ChannelFuture open() throws Exception {
		return null;
	}

	@Override
	protected SslContext createSslCtx() {
		return null;
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity entity) {
		
		// 修改连接空闲时间,使用server.xml里配置的连接空闲时间生效
		if (entity instanceof SMPPServerChildEndpointEntity) {
			ChannelHandler handler = pipe.get(GlobalConstance.IdleCheckerHandlerName);
			if (handler != null) {
				pipe.replace(handler, GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
			}
		}
		pipe.addLast("SMPPLongMessageHandler", new SMPPLongMessageHandler(entity));
		pipe.addLast("EnquireLinkMessageHandler",new EnquireLinkMessageHandler());
		pipe.addLast("EnquireLinkRespMessageHandler",new EnquireLinkRespMessageHandler());
		pipe.addLast("UnbindRespMessageHandler", new UnbindRespMessageHandler());
		pipe.addLast("UnbindMessageHandler", new UnbindMessageHandler());
		
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		
		
	}

	@Override
	protected void initSslCtx(Channel ch, EndpointEntity entity) {
		
	}

	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, ConcurrentMap storeMap, boolean preSend) {
		return new SMPPSessionStateManager(entity, storeMap, preSend);
	}

}
