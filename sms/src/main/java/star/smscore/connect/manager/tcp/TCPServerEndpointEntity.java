package star.smscore.connect.manager.tcp;

import star.smscore.connect.manager.EndpointConnector;
import star.smscore.connect.manager.EndpointEntity;
import star.smscore.connect.manager.ServerEndpoint;

public class TCPServerEndpointEntity extends EndpointEntity implements ServerEndpoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5961093716034771309L;

	public TCPServerEndpointEntity(int port) {
		setPort(port);
	}

	public TCPServerEndpointEntity(String host, int port) {
		setHost(host);
		setPort(port);
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
	protected <T extends EndpointConnector<EndpointEntity>> T buildConnector() {
		return null;
	}

	@Override
	public EndpointEntity getChild(String userName, ChannelType chType) {
		return null;
	}
}
