package star.smscore.connect.manager.cmpp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.codec.cmpp.CMPPMessageCodecAggregator;
import star.smscore.codec.cmpp.CmppHeaderCodec;
import star.smscore.codec.cmpp20.CMPP20MessageCodecAggregator;
import star.smscore.codec.cmpp7F.CMPP7FMessageCodecAggregator;
import star.smscore.common.GlobalConstance;
import star.smscore.common.NotSupportedException;
import star.smscore.handler.cmpp.CMPPDeliverLongMessageHandler;
import star.smscore.handler.cmpp.CMPPSubmitLongMessageHandler;

/**
 * @author Lihuanghe(18852780@qq.com)
 *         初始化pipeline，解码是在当前handler前插入，不是使用pipeLine.addLast方法。注意使用
 **/
public class CMPPCodecChannelInitializer extends ChannelInitializer<Channel> {
	private static final Logger logger = LoggerFactory.getLogger(CMPPCodecChannelInitializer.class);
	private int version;
	
	private final static int defaultVersion = 0x30;

	public CMPPCodecChannelInitializer() {
			this.version = defaultVersion;
	}
	
	public CMPPCodecChannelInitializer(int version) {
			this.version = version;
	}

	public static String pipeName() {
		return "cmppCodec";
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		/*
		 * 消息总长度(含消息头及消息体) 最大消消息长度要从配置里取 处理粘包，断包
		 */
		pipeline.addBefore(pipeName(), "FrameDecoder", new LengthFieldBasedFrameDecoder(4 * 1024 , 0, 4, -4, 0, true));

		pipeline.addBefore(pipeName(), "CmppHeaderCodec", new CmppHeaderCodec());

		pipeline.addBefore(pipeName(), GlobalConstance.codecName, getCodecHandler(version));
		
	}

	public static ChannelDuplexHandler getCodecHandler(int version) throws Exception {
		if (version == 0x30L) {
			return CMPPMessageCodecAggregator.getInstance();
		} else if (version == 0x20L) {
			return CMPP20MessageCodecAggregator.getInstance();
		} else if (version == 0x7FL) {
			return CMPP7FMessageCodecAggregator.getInstance();
		}
		logger.error("not supported protocol version: {}", version);
		throw new NotSupportedException("not supported protocol version.");
	}

}
