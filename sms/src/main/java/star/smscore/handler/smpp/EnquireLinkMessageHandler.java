package star.smscore.handler.smpp;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import star.smscore.codec.smpp.msg.EnquireLink;

@Sharable
public class EnquireLinkMessageHandler extends SimpleChannelInboundHandler<EnquireLink>{
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, EnquireLink msg) throws Exception {
		ctx.writeAndFlush(msg.createResponse());
		
	}}