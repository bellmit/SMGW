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

import star.smscore.codec.cmpp.msg.CmppCancelResponseMessage;
import star.smscore.codec.cmpp.msg.Message;
import star.smscore.codec.cmpp.packet.CmppCancelResponse;
import star.smscore.codec.cmpp.packet.CmppPacketType;
import star.smscore.codec.cmpp.packet.PacketType;
/**
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class CmppCancelResponseMessageCodec extends MessageToMessageCodec<Message, CmppCancelResponseMessage> {
	private PacketType packetType;
	
	public CmppCancelResponseMessageCodec() {
		this(CmppPacketType.CMPPCANCELRESPONSE);
	}

	public CmppCancelResponseMessageCodec(PacketType packetType) {
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

		CmppCancelResponseMessage responseMessage = new CmppCancelResponseMessage(msg.getHeader());
		ByteBuf  bodyBuffer = Unpooled.wrappedBuffer( msg.getBodyBuffer());
		responseMessage.setSuccessId(bodyBuffer.readUnsignedInt());
		ReferenceCountUtil.release(bodyBuffer);
		out.add(responseMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppCancelResponseMessage msg, List<Object> out) throws Exception {

		ByteBuf bodyBuffer =  ctx.alloc().buffer(CmppCancelResponse.SUCCESSID.getBodyLength());
        bodyBuffer.writeInt((int) msg.getSuccessId());
		
		msg.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);
	}

}
