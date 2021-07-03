package star.smscore.connect.manager.cmpp;

import star.smscore.connect.manager.ClientEndpoint;

/**
 *@author Lihuanghe(18852780@qq.com)
 */
public class CMPPClientEndpointEntity extends CMPPEndpointEntity implements ClientEndpoint {

	@Override
	protected CMPPClientEndpointConnector buildConnector() {
		
		return new CMPPClientEndpointConnector(this);
	}
}
