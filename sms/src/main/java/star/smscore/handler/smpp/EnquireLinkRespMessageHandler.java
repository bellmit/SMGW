package star.smscore.handler.smpp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

import star.smscore.codec.smpp.msg.EnquireLinkResp;
@Sharable
public class EnquireLinkRespMessageHandler  extends SimpleChannelInboundHandler<EnquireLinkResp>{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, EnquireLinkResp msg) throws Exception {
		
	}
}


