package star.smscore.handler.sgip;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import star.smscore.codec.sgip12.msg.SgipUnbindResponseMessage;

public class SgipUnbindResponseMessageHandler extends SimpleChannelInboundHandler<SgipUnbindResponseMessage>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, SgipUnbindResponseMessage msg) throws Exception {
		ctx.channel().close();
	}}