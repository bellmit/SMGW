/**
 * 
 */
package star.smscore.handler.cmpp;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.codec.cmpp.msg.CmppActiveTestRequestMessage;
import star.smscore.codec.cmpp.msg.CmppActiveTestResponseMessage;
import star.smscore.codec.cmpp.packet.CmppPacketType;
import star.smscore.codec.cmpp.packet.PacketType;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
@Sharable
public class CmppActiveTestRequestMessageHandler extends SimpleChannelInboundHandler<CmppActiveTestRequestMessage> {
	private PacketType packetType;
	private static final Logger logger = LoggerFactory.getLogger(CmppActiveTestRequestMessageHandler.class);

	public CmppActiveTestRequestMessageHandler() {
		this(CmppPacketType.CMPPACTIVETESTREQUEST);
	}

	public CmppActiveTestRequestMessageHandler(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CmppActiveTestRequestMessage e) throws Exception {
		CmppActiveTestResponseMessage resp = new CmppActiveTestResponseMessage(e.getHeader().getSequenceId());
		ctx.writeAndFlush(resp);
	}

}
