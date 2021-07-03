package star.smscore.codec.cmpp;

import static star.smscore.common.util.NettyByteBufUtil.toArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import star.smscore.codec.cmpp.msg.CmppConnectRequestMessage;
import star.smscore.codec.cmpp.msg.Message;
import star.smscore.codec.cmpp.packet.CmppConnectRequest;
import star.smscore.codec.cmpp.packet.CmppPacketType;
import star.smscore.codec.cmpp.packet.PacketType;
import star.smscore.common.GlobalConstance;
import star.smscore.common.util.CMPPCommonUtil;
/**
 *
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class CmppConnectRequestMessageCodec extends MessageToMessageCodec<Message, CmppConnectRequestMessage> {
	private PacketType packetType;

	public CmppConnectRequestMessageCodec() {
		this(CmppPacketType.CMPPCONNECTREQUEST);
	}

	public CmppConnectRequestMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		int commandId = msg.getHeader().getCommandId();
		if (packetType.getCommandId() != commandId)
		{
			//不解析，交给下一个codec
			out.add(msg);
			return;
		}
		CmppConnectRequestMessage requestMessage = new CmppConnectRequestMessage(msg.getHeader());

		ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());
		requestMessage.setSourceAddr(bodyBuffer.readCharSequence(CmppConnectRequest.SOURCEADDR.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());

		requestMessage.setAuthenticatorSource(toArray(bodyBuffer,CmppConnectRequest.AUTHENTICATORSOURCE.getLength()));

		requestMessage.setVersion(bodyBuffer.readUnsignedByte());
		requestMessage.setTimestamp(bodyBuffer.readUnsignedInt());
		
		ReferenceCountUtil.release(bodyBuffer);
		out.add(requestMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppConnectRequestMessage msg, List<Object> out) throws Exception {

		ByteBuf bodyBuffer =  ctx.alloc().buffer(CmppConnectRequest.AUTHENTICATORSOURCE.getBodyLength());

		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(msg.getSourceAddr().getBytes(GlobalConstance.defaultTransportCharset),
				CmppConnectRequest.SOURCEADDR.getLength(), 0));
		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(msg.getAuthenticatorSource(),CmppConnectRequest.AUTHENTICATORSOURCE.getLength(),0));
		bodyBuffer.writeByte(msg.getVersion());
		bodyBuffer.writeInt((int) msg.getTimestamp());

		msg.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);
	}

}
