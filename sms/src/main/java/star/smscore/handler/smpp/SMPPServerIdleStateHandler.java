package star.smscore.handler.smpp;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import star.smscore.codec.smpp.msg.EnquireLink;
import star.smscore.common.GlobalConstance;
import star.smscore.session.cmpp.SessionState;
@Sharable
public class SMPPServerIdleStateHandler extends ChannelDuplexHandler {
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.ALL_IDLE) {
            	//如果是CMPP连接未建立，直接关闭
            	if(ctx.channel().attr(GlobalConstance.attributeKey).get() != SessionState.Connect){
            		ctx.close();
            	}else{
            		ctx.channel().writeAndFlush(new EnquireLink());
            	}
            } 
        }else{
        	ctx.fireUserEventTriggered(evt);
        }
    }
}