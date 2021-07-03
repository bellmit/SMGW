/**
 * 
 */
package star.smscore.codec.cmpp20;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import star.smscore.codec.cmpp.msg.CmppSubmitResponseMessage;
import star.smscore.codec.cmpp.msg.Message;
import star.smscore.codec.cmpp.packet.CmppSubmitResponse;
import star.smscore.codec.cmpp.packet.PacketType;
import star.smscore.codec.cmpp20.packet.Cmpp20PacketType;
import star.smscore.codec.cmpp20.packet.Cmpp20SubmitResponse;
import star.smscore.common.util.DefaultMsgIdUtil;

import static star.smscore.common.util.NettyByteBufUtil.*;
/**
 * shifei(shifei@asiainfo.com)
 */
public class Cmpp20SubmitResponseMessageCodec extends MessageToMessageCodec<Message, CmppSubmitResponseMessage> {
	private PacketType packetType;

	/**
	 * 
	 */
	public Cmpp20SubmitResponseMessageCodec() {
		this(Cmpp20PacketType.CMPPSUBMITRESPONSE);
	}

	public Cmpp20SubmitResponseMessageCodec(PacketType packetType) {
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
		CmppSubmitResponseMessage responseMessage = new CmppSubmitResponseMessage(msg.getHeader());

		ByteBuf bodyBuffer =Unpooled.wrappedBuffer(msg.getBodyBuffer());

		responseMessage.setMsgId(DefaultMsgIdUtil.bytes2MsgId(toArray(bodyBuffer,Cmpp20SubmitResponse.MSGID.getLength())));
		responseMessage.setResult(bodyBuffer.readUnsignedByte());
		ReferenceCountUtil.release(bodyBuffer);
		out.add(responseMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppSubmitResponseMessage msg, List<Object> out) throws Exception {
		ByteBuf bodyBuffer = ctx.alloc().buffer(Cmpp20SubmitResponse.RESULT.getBodyLength());

		bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(msg.getMsgId()));
		bodyBuffer.writeByte((int) msg.getResult());
		msg.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);
	}
	


}
