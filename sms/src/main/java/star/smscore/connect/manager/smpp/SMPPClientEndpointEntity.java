package star.smscore.connect.manager.smpp;

import star.smscore.connect.manager.ClientEndpoint;


public class SMPPClientEndpointEntity extends SMPPEndpointEntity implements ClientEndpoint{

	@Override
	protected SMPPClientEndpointConnector buildConnector() {
		
		return new SMPPClientEndpointConnector(this);
	}

}
