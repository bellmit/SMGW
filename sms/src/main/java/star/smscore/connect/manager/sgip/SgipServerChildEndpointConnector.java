package star.smscore.connect.manager.sgip;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.AbstractEndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.connect.manager.smgp.SMGPServerChildEndpointEntity;
import star.smscore.handler.sgip.ReWriteNodeIdHandler;
import star.smscore.handler.sgip.SgipDeliverLongMessageHandler;
import star.smscore.handler.sgip.SgipSubmitLongMessageHandler;
import star.smscore.handler.sgip.SgipUnbindRequestMessageHandler;
import star.smscore.handler.sgip.SgipUnbindResponseMessageHandler;
import star.smscore.session.AbstractSessionStateManager;
import star.smscore.session.sgip.SgipSessionStateManager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

public class SgipServerChildEndpointConnector extends AbstractEndpointConnector{
	private static final Logger logger = LoggerFactory.getLogger(SgipServerChildEndpointConnector.class);
	public SgipServerChildEndpointConnector(EndpointEntity endpoint) {
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
		if (entity instanceof SgipServerChildEndpointEntity) {
			ChannelHandler handler = pipe.get(GlobalConstance.IdleCheckerHandlerName);
			if (handler != null) {
				pipe.replace(handler, GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, entity.getIdleTimeSec(), TimeUnit.SECONDS));
			}
		}
		pipe.addLast("reWriteNodeIdHandler", new ReWriteNodeIdHandler((SgipEndpointEntity)entity));
		//处理长短信
		pipe.addLast("SgipDeliverLongMessageHandler", new SgipDeliverLongMessageHandler(entity));
		pipe.addLast("SgipSubmitLongMessageHandler",  new SgipSubmitLongMessageHandler(entity));
		pipe.addLast("SgipUnbindResponseMessageHandler", new SgipUnbindResponseMessageHandler());
		pipe.addLast("SgipUnbindRequestMessageHandler", new SgipUnbindRequestMessageHandler());
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		
		
	}

	@Override
	protected void initSslCtx(Channel ch, EndpointEntity entity) {
		
	}

	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, ConcurrentMap storeMap, boolean preSend) {
		return new SgipSessionStateManager(entity, storeMap, preSend);
	}

}
