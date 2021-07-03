package star.smscore.connect.manager.smgp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import star.smscore.codec.smgp.codec.SMGPMessageCodec;
import star.smscore.common.GlobalConstance;
import star.smscore.handler.smgp.SMGPDeliverLongMessageHandler;
import star.smscore.handler.smgp.SMGPSubmitLongMessageHandler;

public class SMGPCodecChannelInitializer extends ChannelInitializer<Channel> {
	private static final Logger logger = LoggerFactory.getLogger(SMGPCodecChannelInitializer.class);

	private int version;

	public SMGPCodecChannelInitializer(int version) {
		this.version = version;
	}

	public static String pipeName() {
		return "smgpCodec";
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addBefore(pipeName(), "FrameDecoder", new LengthFieldBasedFrameDecoder(4 * 1024, 0, 4, -4, 0, true));

		pipeline.addBefore(pipeName(), GlobalConstance.codecName, new SMGPMessageCodec(version));

	}

}
