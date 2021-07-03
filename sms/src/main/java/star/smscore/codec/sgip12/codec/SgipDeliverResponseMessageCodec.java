/**
 * 
 */
package star.smscore.codec.sgip12.codec;

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
import star.smscore.codec.cmpp.packet.PacketType;
import star.smscore.codec.sgip12.msg.SgipDeliverResponseMessage;
import star.smscore.codec.sgip12.packet.SgipDeliverResponse;
import star.smscore.codec.sgip12.packet.SgipPacketType;
import star.smscore.common.GlobalConstance;
import star.smscore.common.util.CMPPCommonUtil;
import star.smscore.common.util.DefaultMsgIdUtil;
/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class SgipDeliverResponseMessageCodec extends MessageToMessageCodec<Message, SgipDeliverResponseMessage> {
	private PacketType packetType;

	/**
	 * 
	 */
	public SgipDeliverResponseMessageCodec() {
		this(SgipPacketType.DELIVERRESPONSE);
	}

	public SgipDeliverResponseMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		int commandId =  msg.getHeader().getCommandId();
		if (packetType.getCommandId() != commandId) {
			// 不解析，交给下一个codec
			out.add(msg);
			return;
		}
		SgipDeliverResponseMessage responseMessage = new SgipDeliverResponseMessage(msg.getHeader());
		responseMessage.setTimestamp(msg.getTimestamp());
		ByteBuf  bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());
		
		responseMessage.setResult(bodyBuffer.readUnsignedByte());
		responseMessage.setReserve(bodyBuffer.readCharSequence(
				SgipDeliverResponse.RESERVE.getLength(),GlobalConstance.defaultTransportCharset).toString(
						).trim());
		ReferenceCountUtil.release(bodyBuffer);
		out.add(responseMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, SgipDeliverResponseMessage msg, List<Object> out) throws Exception {

		ByteBuf bodyBuffer = ctx.alloc().buffer(SgipDeliverResponse.RESULT.getBodyLength());
		
		bodyBuffer.writeByte(msg.getResult());
		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(msg.getReserve()
				.getBytes(GlobalConstance.defaultTransportCharset),
				SgipDeliverResponse.RESERVE.getLength(), 0));

		msg.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);

	}

}
