package star.sms.smpp.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import star.sms.account.domain.AccountInfo;
import star.sms.account.service.AccountService;
import star.sms.config.SystemConfig;
import star.sms.smpp.handler.SMPPMessageReceiveHandler;
import star.smscore.connect.manager.EndpointEntity.ChannelType;
import star.smscore.connect.manager.EndpointEntity.SupportLongMessage;
import star.smscore.connect.manager.EndpointManager;
import star.smscore.connect.manager.smpp.SMPPClientEndpointEntity;
import star.smscore.handler.api.BusinessHandlerInterface;

@Slf4j
@Component
@Order(1) // 指定顺序
public class SmppRunner implements CommandLineRunner {
	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private AccountService accountService;
	
	@Override
	public void run(String... args) throws Exception {
		if(!systemConfig.getIsTest()) {
			if(!systemConfig.getIsAdmin()) return;
		}
		final EndpointManager manager = EndpointManager.INS;
		log.info("进入到smpp协议发送功能");
		List<AccountInfo> list = accountService.getSmppAccount();
		if(list!=null && list.size()>0) {
			for(AccountInfo account:list) {
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
//			client.setWriteLimit(200);
//			client.setReadLimit(200);
					client.setSupportLongmsg(SupportLongMessage.SEND);  //接收长短信时不自动合并
					List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
					clienthandlers.add( new SMPPMessageReceiveHandler()); 
					client.setBusinessHandlerSet(clienthandlers);
					
					manager.addEndpointEntity(client);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			manager.openAll();
			manager.startConnectionCheckTask();
			Thread.sleep(1000);
			System.out.println("start.....");
			LockSupport.park();
			EndpointManager.INS.close();
			log.info("退出smpp协议发送功能");
		}
	}

}
