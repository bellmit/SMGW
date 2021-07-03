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

import star.smscore.codec.cmpp.msg.CmppActiveTestResponseMessage;
import star.smscore.codec.cmpp.msg.Message;
import star.smscore.codec.cmpp.packet.CmppActiveTestResponse;
import star.smscore.codec.cmpp.packet.CmppPacketType;
import star.smscore.codec.cmpp.packet.PacketType;

/**
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 *
 */
public class CmppActiveTestResponseMessageCodec extends MessageToMessageCodec<Message, CmppActiveTestResponseMessage> {
	private PacketType packetType;
	
	public CmppActiveTestResponseMessageCodec() {
		this(CmppPacketType.CMPPACTIVETESTRESPONSE);
	}

	public CmppActiveTestResponseMessageCodec(PacketType packetType) {
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

		CmppActiveTestResponseMessage responseMessage = new CmppActiveTestResponseMessage(msg.getHeader());
		ByteBuf  bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());
		
		//甘肃测试环境回包缺少reserved字段，这里要容错
		if(bodyBuffer.readableBytes()>0)
			responseMessage.setReserved(bodyBuffer.readByte());
		
		ReferenceCountUtil.release(bodyBuffer);
		out.add(responseMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppActiveTestResponseMessage msg, List<Object> out) throws Exception {
		
		ByteBuf bodyBuffer = ctx.alloc().buffer(CmppActiveTestResponse.RESERVED.getBodyLength());
		bodyBuffer.writeByte(msg.getReserved());
		msg.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);
	}

}
