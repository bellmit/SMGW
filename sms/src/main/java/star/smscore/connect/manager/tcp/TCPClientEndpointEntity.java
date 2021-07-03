package star.smscore.connect.manager.tcp;

import star.smscore.connect.manager.ClientEndpoint;
import star.smscore.connect.manager.EndpointConnector;
import star.smscore.connect.manager.EndpointEntity;

public class TCPClientEndpointEntity extends EndpointEntity implements ClientEndpoint {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6580678038168372304L;

	public TCPClientEndpointEntity(String host, int port) {
		setHost(host);
		setPort(port);
	}

	@Override
	protected TCPClientEndpointConnector buildConnector() {

		return new TCPClientEndpointConnector(this);
	}

}
