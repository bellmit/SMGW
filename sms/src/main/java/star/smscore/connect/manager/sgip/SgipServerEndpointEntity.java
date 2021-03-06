package star.smscore.connect.manager.sgip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import star.smscore.connect.manager.EndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.connect.manager.ServerEndpoint;
import star.smscore.connect.manager.cmpp.CMPPServerChildEndpointEntity;
import star.smscore.connect.manager.cmpp.CMPPServerEndpointConnector;

public class SgipServerEndpointEntity extends EndpointEntity implements ServerEndpoint {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7749225357445133670L;
	private Map<String,SgipServerChildEndpointEntity> childrenEndpoint = new ConcurrentHashMap<String,SgipServerChildEndpointEntity>() ;
	
	
	public void addchild(EndpointEntity entity)
	{
		
		childrenEndpoint.put(((SgipServerChildEndpointEntity)entity).getLoginName().trim(), (SgipServerChildEndpointEntity)entity);
	}
	
	public void removechild(EndpointEntity entity){
		childrenEndpoint.remove(((SgipServerChildEndpointEntity)entity).getLoginName().trim());
	}
	
	public EndpointEntity getChild(String userName)
	{
		return childrenEndpoint.get(userName);
	}
	
	public List<EndpointEntity> getAllChild()
	{
		List<EndpointEntity> list = new ArrayList<EndpointEntity>();
		for(Map.Entry<String,SgipServerChildEndpointEntity> entry : childrenEndpoint.entrySet()){
			list.add(entry.getValue());
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	@Override
	protected SgipServerEndpointConnector buildConnector() {
		return new SgipServerEndpointConnector(this);
	}

	@Override
	public EndpointEntity getChild(String userName, ChannelType chType) {
		return null;
	}

}
