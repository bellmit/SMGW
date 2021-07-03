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

import star.smscore.codec.cmpp.msg.Message;
import star.smscore.codec.cmpp.packet.PacketType;
import star.smscore.codec.sgip12.msg.SgipReportRequestMessage;
import star.smscore.codec.sgip12.packet.SgipPacketType;
import star.smscore.codec.sgip12.packet.SgipReportRequest;
import star.smscore.common.GlobalConstance;
import star.smscore.common.util.CMPPCommonUtil;
import star.smscore.common.util.DefaultSequenceNumberUtil;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class SgipReportRequestMessageCodec extends MessageToMessageCodec<Message, SgipReportRequestMessage> {
	private PacketType packetType;

	public SgipReportRequestMessageCodec() {
		this(SgipPacketType.REPORTREQUEST);
	}

	public SgipReportRequestMessageCodec(PacketType packetType) {
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

		SgipReportRequestMessage requestMessage = new SgipReportRequestMessage(msg.getHeader());
		requestMessage.setTimestamp(msg.getTimestamp());
		ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());
		
		byte[] seqbytes = new byte[SgipReportRequest.SUBMITSEQUENCENUMBER.getLength()];
		bodyBuffer.readBytes(seqbytes);
		
		requestMessage.setSequenceId(DefaultSequenceNumberUtil.bytes2SequenceN(seqbytes));
		
		requestMessage.setReporttype(bodyBuffer.readUnsignedByte());
		requestMessage.setUsernumber(bodyBuffer.readCharSequence(SgipReportRequest.USERNUMBER.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());
		requestMessage.setState(bodyBuffer.readUnsignedByte());
		requestMessage.setErrorcode(bodyBuffer.readUnsignedByte());
		requestMessage.setReserve(bodyBuffer.readCharSequence(SgipReportRequest.RESERVE.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());

		ReferenceCountUtil.release(bodyBuffer);
		out.add(requestMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, SgipReportRequestMessage msg, List<Object> out) throws Exception {

		ByteBuf bodyBuffer = ctx.alloc().buffer(SgipReportRequest.USERNUMBER.getBodyLength());
		bodyBuffer.writeBytes(DefaultSequenceNumberUtil.sequenceN2Bytes(msg.getSequenceId()));
		bodyBuffer.writeByte(msg.getReporttype());
		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(msg.getUsernumber().getBytes(GlobalConstance.defaultTransportCharset),
				SgipReportRequest.USERNUMBER.getLength(), 0));
		bodyBuffer.writeByte(msg.getState());
		bodyBuffer.writeByte(msg.getErrorcode());
		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(msg.getReserve().getBytes(GlobalConstance.defaultTransportCharset),
				SgipReportRequest.RESERVE.getLength(), 0));

		msg.setBodyBuffer(toArray(bodyBuffer, bodyBuffer.readableBytes()));
		ReferenceCountUtil.release(bodyBuffer);
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		out.add(msg);
	}

}
