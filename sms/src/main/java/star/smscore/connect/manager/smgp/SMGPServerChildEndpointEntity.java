package star.smscore.connect.manager.smgp;

import star.smscore.connect.manager.EndpointEntity;
import star.smscore.connect.manager.ServerEndpoint;


public class SMGPServerChildEndpointEntity extends SMGPEndpointEntity implements ServerEndpoint{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6946326952395639937L;

	@SuppressWarnings("unchecked")
	@Override
	protected SMGPServerChildEndpointConnector buildConnector() {
		return new SMGPServerChildEndpointConnector(this);
	}

	@Override
	public void addchild(EndpointEntity entity) {
	}

	@Override
	public void removechild(EndpointEntity entity) {
		
	}

	@Override
	public EndpointEntity getChild(String userName) {
		return null;
	}

	@Override
	public EndpointEntity getChild(String userName, ChannelType chType) {
		return null;
	}


}
