/**
 * 
 */
package star.smscore.handler.cmpp;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.codec.cmpp.msg.CmppActiveTestResponseMessage;
import star.smscore.codec.cmpp.packet.CmppPacketType;
import star.smscore.codec.cmpp.packet.PacketType;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
@Sharable
public class CmppActiveTestResponseMessageHandler extends SimpleChannelInboundHandler<CmppActiveTestResponseMessage> {
	private static final Logger logger = LoggerFactory.getLogger(CmppActiveTestResponseMessageHandler.class);
	private PacketType packetType;

	public CmppActiveTestResponseMessageHandler() {
		this(CmppPacketType.CMPPACTIVETESTRESPONSE);
	}

	public CmppActiveTestResponseMessageHandler(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext paramChannelHandlerContext, CmppActiveTestResponseMessage paramI) throws Exception {
				
	}

}
