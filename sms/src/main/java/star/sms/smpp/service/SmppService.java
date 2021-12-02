
package star.sms.smpp.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.marre.sms.SmppSmsDcs;
import org.marre.sms.SmsAlphabet;
import org.marre.sms.SmsMsgClass;
import org.marre.sms.SmsTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import star.sms.account.domain.AccountInfo;
import star.sms.config.SystemConfig;
import star.sms.smpp.handler.SMPPMessageReceiveHandler;
import star.sms.sms.service.SmsService;
import star.smscore.BaseMessage;
import star.smscore.codec.smpp.msg.SubmitSm;
import star.smscore.codec.smpp.msg.SubmitSmResp;
import star.smscore.common.util.ChannelUtil;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.connect.manager.EndpointEntity.ChannelType;
import star.smscore.connect.manager.EndpointEntity.SupportLongMessage;
import star.smscore.connect.manager.EndpointManager;
import star.smscore.connect.manager.smpp.SMPPClientEndpointEntity;
import star.smscore.handler.api.BusinessHandlerInterface;

@Slf4j
@Service
@Transactional
public class SmppService {
	@Autowired
	private SmsService smsService;
	@Autowired
	private SystemConfig systemConfig;
	
	/**
	 * 接受mq消息发送
	 * @param id
	 * @param smsId
	 * @param phone
	 * @param smsContent
	 * @throws Exception
	 */
	public void sendSms(int platManagerId,String accountId,Integer smsId,String phone,String smsContent) throws Exception {
		if(StringUtils.isEmpty(accountId)) return;
		log.info("SMPP通道开始发送短信息，id:"+accountId+",smsId:"+smsId+",phone:"+phone+",smsContent:"+smsContent);
		SubmitSm request = new SubmitSm();
		if(!phone.startsWith("86")) phone = "86"+phone;
		request.setDestAddress(new star.smscore.codec.smpp.Address((byte)1,(byte)1,phone));
		request.setSourceAddress(new star.smscore.codec.smpp.Address((byte)5,(byte)0,"MelroseLabs"));
		request.setSmsMsg(new SmsTextMessage(smsContent, SmppSmsDcs.getGeneralDataCodingDcs(SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN)));
		request.setRegisteredDelivery((byte)1);
		
		List<Promise<BaseMessage>> futures = ChannelUtil.syncWriteLongMsgToEntity(accountId,request);
		if(futures!=null) {
			for(Promise<BaseMessage>  promise: futures){
				//接收到response后回调Listener方法
				promise.addListener(new GenericFutureListener() {
					@Override
					public void operationComplete(Future future) throws Exception {
						//接收成功，如果失败可以获取失败原因，比如遇到连接突然中断错误等等
						if(future.isSuccess()){
							//打印收到的response消息
							log.info("smpp消息发送成功，response:{}",future.get());
							SubmitSmResp response = (SubmitSmResp) future.get();
							log.info("smpp消息发送成功，messageId:{}",response.getMessageId());
							//修改短信状态，并计费
							if(response.getCommandStatus()==0) {
								smsService.updateSmsForSmpp(platManagerId,accountId,smsId,response,true);
							}else {
								smsService.updateSmsForSmpp(platManagerId,accountId,smsId,response,false);
							}
						}else{
							//打印错误原因
							log.error("smpp消息发送失败，response:{}",future.cause());
							SubmitSmResp response = (SubmitSmResp) future.get();
							smsService.updateSmsForSmpp(platManagerId,accountId,smsId,response,false);
						}
					}
				});
			}
		}else {
			//通道尚未注册成功
			/**
			 * 这里新增一个逻辑。就是判断一下通道是否是可以连接的，如果是不能连接，那么久状态设置为发送失败，如果可以连接则重置为0待发送状态
			 */
			EndpointEntity e = EndpointManager.INS.getEndpointEntity(accountId);
			if (e != null && e.getSingletonConnector() != null && e.getSingletonConnector().getConnectionNum() > 0) {
				log.warn("短信发送失败，重置为待发送状态，smpp通道id {}", accountId);
				smsService.updateSmsForSmppInit(platManagerId, smsId);
			} else if (e == null) {
				log.warn("短信发送失败，尚未绑定通道，smpp通道id {}", accountId);
				SubmitSmResp response = new SubmitSmResp();
				response.setMessageId("");
				response.setResultMessage("线路中断");
				response.setCommandStatus(-1);
				smsService.updateSmsForSmpp(platManagerId, accountId, smsId, response, false);
			} else {
				log.warn("smpp通道  {} 未知错误，发送消息失败 ", accountId);
				SubmitSmResp response = new SubmitSmResp();
				response.setMessageId("");
				response.setResultMessage("线路中断");
				response.setCommandStatus(-1);
				smsService.updateSmsForSmpp(platManagerId, accountId, smsId, response, false);
			}

		}
	}
	/**
	 * 删除通道
	 * @param id
	 */
	public void disableChannel(String id) {
		if(!systemConfig.getIsTest()) {
			if(!systemConfig.getIsAdmin()) return;
		}
		final EndpointManager manager = EndpointManager.INS;
		try {
			manager.remove(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建通道
	 * @param account
	 */
	public void enableChannel(AccountInfo account) {
		if(!systemConfig.getIsTest()) {
			if(!systemConfig.getIsAdmin()) return;
		}
		final EndpointManager manager = EndpointManager.INS;
		try {
			SMPPClientEndpointEntity client = new SMPPClientEndpointEntity();
			client.setId(account.getId()+"");
			client.setHost(account.getIp().split(":")[0]);
			client.setPort(Integer.valueOf(account.getIp().split(":")[1]));
			client.setSystemId(account.getAccount());
			client.setPassword(account.getPassword());
			client.setChannelType(ChannelType.DUPLEX);
			
			client.setMaxChannels((short)1);
			client.setRetryWaitTimeSec((short)100);
			client.setUseSSL(false);
			client.setReSendFailMsg(true);
			//	client.setWriteLimit(200);
			//	client.setReadLimit(200);
			client.setSupportLongmsg(SupportLongMessage.SEND);  //接收长短信时不自动合并
			List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
			clienthandlers.add( new SMPPMessageReceiveHandler()); 
			client.setBusinessHandlerSet(clienthandlers);
			
			manager.openEndpoint(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
