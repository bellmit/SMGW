/**
 * 
 */
package star.smscore.codec.cmpp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import star.smscore.codec.cmpp.msg.CmppActiveTestRequestMessage;
import star.smscore.codec.cmpp.msg.Message;
import star.smscore.codec.cmpp.packet.CmppPacketType;
import star.smscore.codec.cmpp.packet.PacketType;
import star.smscore.common.GlobalConstance;

/**
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class CmppActiveTestRequestMessageCodec extends MessageToMessageCodec<Message, CmppActiveTestRequestMessage> {
	private PacketType packetType;

	public CmppActiveTestRequestMessageCodec() {
		this(CmppPacketType.CMPPACTIVETESTREQUEST);
	}

	public CmppActiveTestRequestMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		int commandId = msg.getHeader().getCommandId();
		if (packetType.getCommandId() != commandId) {
			//不解析，交给下一个codec
			out.add(msg);
			return;
		}

		CmppActiveTestRequestMessage requestMessage = new CmppActiveTestRequestMessage(msg.getHeader());
		out.add(requestMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppActiveTestRequestMessage msg, List<Object> out) throws Exception {
		msg.setBodyBuffer(GlobalConstance.emptyBytes);
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		out.add(msg);
	}

}
