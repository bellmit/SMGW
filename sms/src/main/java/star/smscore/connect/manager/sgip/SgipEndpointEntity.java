package star.smscore.connect.manager.sgip;

import star.smscore.common.GlobalConstance;
import star.smscore.connect.manager.EndpointEntity;

public abstract class SgipEndpointEntity extends EndpointEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = -441048745116970563L;
	private String loginName = GlobalConstance.emptyString;
	private String loginPassowrd = GlobalConstance.emptyString;
	private long nodeId = 0;
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getLoginPassowrd() {
		return loginPassowrd;
	}
	public void setLoginPassowrd(String loginPassowrd) {
		this.loginPassowrd = loginPassowrd;
	}
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}    
}
