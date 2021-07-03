package star.smscore.connect.manager.smgp;

import star.smscore.connect.manager.ClientEndpoint;


public class SMGPClientEndpointEntity extends SMGPEndpointEntity implements ClientEndpoint{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4564015175057370204L;

	@Override
	protected SMGPClientEndpointConnector buildConnector() {
		
		return new SMGPClientEndpointConnector(this);
	}

}
