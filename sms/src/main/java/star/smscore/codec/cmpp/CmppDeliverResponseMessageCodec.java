/**
 * 
 */
package star.smscore.codec.cmpp;

import static star.smscore.common.util.NettyByteBufUtil.toArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import star.smscore.codec.cmpp.msg.CmppDeliverResponseMessage;
import star.smscore.codec.cmpp.msg.Message;
import star.smscore.codec.cmpp.packet.CmppDeliverResponse;
import star.smscore.codec.cmpp.packet.CmppPacketType;
import star.smscore.codec.cmpp.packet.PacketType;
import star.smscore.common.util.DefaultMsgIdUtil;
/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class CmppDeliverResponseMessageCodec extends MessageToMessageCodec<Message, CmppDeliverResponseMessage> {
	private PacketType packetType;

	/**
	 * 
	 */
	public CmppDeliverResponseMessageCodec() {
		this(CmppPacketType.CMPPDELIVERRESPONSE);
	}

	public CmppDeliverResponseMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		int commandId = msg.getHeader().getCommandId();
		if (packetType.getCommandId() != commandId) {
			// 不解析，交给下一个codec
			out.add(msg);
			return;
		}
		CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(msg.getHeader());

		ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());

		responseMessage.setMsgId(DefaultMsgIdUtil.bytes2MsgId(toArray(bodyBuffer,CmppDeliverResponse.MSGID.getLength())));
		responseMessage.setResult(bodyBuffer.readUnsignedInt());
		ReferenceCountUtil.release(bodyBuffer);
		out.add(responseMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppDeliverResponseMessage msg, List<Object> out) throws Exception {

		ByteBuf bodyBuffer = ctx.alloc().buffer(CmppDeliverResponse.MSGID.getBodyLength());
		bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(msg.getMsgId()));
		bodyBuffer.writeInt((int) msg.getResult());

		msg.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);

	}

}
