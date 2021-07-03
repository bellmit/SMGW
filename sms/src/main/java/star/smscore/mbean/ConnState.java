package star.smscore.mbean;

import io.netty.channel.Channel;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import star.smscore.connect.manager.EndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.connect.manager.EndpointManager;
import star.smscore.connect.manager.ServerEndpoint;
import star.smscore.session.cmpp.SessionStateManager;

public class ConnState implements ConnStateMBean {
	@Override
	public String print(String entityId) {
		 StringBuilder sb = new StringBuilder();
		EndpointManager em = EndpointManager.INS;
		if(StringUtils.isEmpty(entityId)){
			Set<EndpointEntity> enlist = em.allEndPointEntity();
			for(EndpointEntity e : enlist){
				sb.append(e.getId()+":\n");
				sb.append(printOne(e));
			}
		}else{
			EndpointEntity e = em.getEndpointEntity(entityId);
			if(e!=null){
				sb.append(e.getId()+":\n");
				sb.append(printOne(e));
			}
		}
		return sb.toString();
	}

	private String printOne(EndpointEntity e){
		 StringBuilder sb = new StringBuilder();
		EndpointConnector econn = e.getSingletonConnector();
		
		if(econn == null) return ""; 
			
		 Channel[] carr = econn.getallChannel();
		 if(carr!=null && carr.length>0){
			 
			 for(int i= 0;i<carr.length;i++){
				 Channel ch = carr[i];
				 SessionStateManager ssm = (SessionStateManager)ch.pipeline().get("sessionStateManager");
				 sb.append("\tch[");
				 sb.append(ch.localAddress().toString());
				 
				 if(e instanceof ServerEndpoint){
					 sb.append("<-");
				 }else{
					 sb.append("->");
				 }
				 sb.append(ch.remoteAddress().toString() +"]");
				 sb.append("\tWaitting-resp=").append(ssm.getWaittingResp());
				 sb.append("\tWriteCount=").append(ssm.getWriteCount());
				 sb.append("\tReadCount=").append(ssm.getReadCount());
				 sb.append("\n");
			 }
		 }
		 return sb.toString();
	}
}
