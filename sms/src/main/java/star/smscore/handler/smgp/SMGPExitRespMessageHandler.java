package star.smscore.handler.smgp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

import star.smscore.codec.smgp.msg.SMGPExitRespMessage;
@Sharable
public class SMGPExitRespMessageHandler extends SimpleChannelInboundHandler<SMGPExitRespMessage>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, SMGPExitRespMessage msg) throws Exception {
		ctx.channel().close();
	}}