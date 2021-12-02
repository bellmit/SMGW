package star.sms.smpp.handler;

import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import star.sms._frame.utils.SpringUtil;
import star.sms.sms.service.SmsService;
import star.smscore.codec.smpp.msg.DeliverSm;
import star.smscore.codec.smpp.msg.DeliverSmReceipt;
import star.smscore.codec.smpp.msg.DeliverSmResp;
import star.smscore.codec.smpp.msg.QuerySm;
import star.smscore.codec.smpp.msg.QuerySmResp;
import star.smscore.codec.smpp.msg.SubmitSm;
import star.smscore.codec.smpp.msg.SubmitSmResp;
import star.smscore.common.util.ChannelUtil;
import star.smscore.common.util.StandardCharsets;

@Slf4j
public class SMPPMessageReceiveHandler extends MessageReceiveHandler {

	@Override
	protected ChannelFuture reponse(final ChannelHandlerContext ctx, Object msg)  {
		
		if(msg instanceof DeliverSmReceipt) {
			DeliverSmReceipt message = (DeliverSmReceipt) msg;
			log.info("smpp发送成功结果类型1，response:{}",msg);
			DeliverSmResp res = ((DeliverSm) msg).createResponse();
			SmsService smsService = SpringUtil.getBean(star.sms.sms.service.SmsService.class);
			smsService.updateSmsForSmppResponse(message.getId(), message.getStat());
			res.setMessageId(String.valueOf(System.currentTimeMillis()));
			return ctx.writeAndFlush(res);
		}else if (msg instanceof DeliverSm ) {
			log.info("smpp发送成功结果类型2");
			DeliverSmResp res = ((DeliverSm) msg).createResponse();
			String msgcontent = ((DeliverSm) msg).getMsgContent();
			res.setMessageId(DigestUtils.md5Hex(msgcontent.getBytes(StandardCharsets.UTF_8)));
			return ctx.writeAndFlush(res);
		}else if (msg instanceof QuerySm ) {
			log.info("smpp发送成功结果类型QuerySm");
			QuerySmResp res = ((QuerySm) msg).createResponse();
			return ctx.writeAndFlush(res);
		} else if (msg instanceof SubmitSm) {
			log.info("smpp发送成功结果类型3");
			SubmitSmResp res = ((SubmitSm) msg).createResponse();
			String msgcontent = ((SubmitSm) msg).getMsgContent();
			byte[] receive = msgcontent.getBytes(StandardCharsets.UTF_8);
			res.setMessageId(DigestUtils.md5Hex(receive));
			ChannelFuture future = ctx.writeAndFlush(res);

			List<SubmitSm> frags = ((SubmitSm) msg).getFragments();
			if (frags != null && !frags.isEmpty()) {
				for (SubmitSm fragment : frags) {

					SubmitSmResp fragres = ((SubmitSm) fragment).createResponse();
					res.setMessageId(String.valueOf(System.currentTimeMillis()));
					ctx.writeAndFlush(fragres);

					if (((SubmitSm) msg).getRegisteredDelivery() == 1) {
						DeliverSmReceipt report = new DeliverSmReceipt();
						report.setId(String.valueOf(fragment.getSequenceNumber()));
						report.setSourceAddress(((SubmitSm) msg).getDestAddress());
						report.setDestAddress(((SubmitSm) msg).getSourceAddress());
						report.setStat("DELIVRD");
						report.setText(fragment.getMsgContent());
						report.setSubmit_date(DateFormatUtils.format(new Date(), "yyMMddHHmm"));
						report.setDone_date(DateFormatUtils.format(new Date(), "yyMMddHHmm"));
						ctx.writeAndFlush(report);
					}
				}
			}
			if (((SubmitSm) msg).getRegisteredDelivery() == 1) {
				DeliverSmReceipt report = new DeliverSmReceipt();
				report.setId(String.valueOf(res.getSequenceNumber()));
				report.setSourceAddress(((SubmitSm) msg).getDestAddress());
				report.setDestAddress(((SubmitSm) msg).getSourceAddress());
				report.setStat("DELIVRD");
				report.setText(((SubmitSm) msg).getMsgContent());
				report.setSubmit_date(DateFormatUtils.format(new Date(), "yyMMddHHmm"));
				report.setDone_date(DateFormatUtils.format(new Date(), "yyMMddHHmm"));
				try {
					ChannelUtil.syncWriteLongMsgToEntity(getEndpointEntity(), report);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return future;
		}
		return null;
	}

}
