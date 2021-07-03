/**
 * 
 */
package star.smscore.handler.cmpp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

import star.smscore.codec.cmpp.msg.CmppTerminateResponseMessage;
import star.smscore.codec.cmpp.packet.CmppPacketType;
import star.smscore.codec.cmpp.packet.PacketType;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
@Sharable
public class CmppTerminateResponseMessageHandler extends SimpleChannelInboundHandler<CmppTerminateResponseMessage> {
	private PacketType packetType;

	/**
	 * 
	 */
	public CmppTerminateResponseMessageHandler() {
		this(CmppPacketType.CMPPTERMINATERESPONSE);
	}

	public CmppTerminateResponseMessageHandler(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, CmppTerminateResponseMessage e) throws Exception {
		ctx.channel().close();
	}

}
