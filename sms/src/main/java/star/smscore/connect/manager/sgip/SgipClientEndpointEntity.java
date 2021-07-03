package star.smscore.connect.manager.sgip;

import star.smscore.connect.manager.ClientEndpoint;


public class SgipClientEndpointEntity extends SgipEndpointEntity implements ClientEndpoint{

	@Override
	protected SgipClientEndpointConnector buildConnector() {
		
		return new SgipClientEndpointConnector(this);
	}

}
