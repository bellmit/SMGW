package star.smscore.handler.smgp;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import star.smscore.codec.smgp.msg.SMGPActiveTestRespMessage;
@Sharable
public class SMGPActiveTestRespMessageHandler  extends SimpleChannelInboundHandler<SMGPActiveTestRespMessage>{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, SMGPActiveTestRespMessage msg) throws Exception {
		
	}
}


