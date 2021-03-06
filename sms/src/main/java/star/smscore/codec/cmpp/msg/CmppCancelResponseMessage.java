package star.smscore.codec.cmpp.msg;

import star.smscore.codec.cmpp.packet.CmppPacketType;

/**
 *
 */
public class CmppCancelResponseMessage extends DefaultMessage {
	private static final long serialVersionUID = -1111862395776885021L;
	private long successId = 1;
	
	public CmppCancelResponseMessage(int sequenceId) {
		super(CmppPacketType.CMPPCANCELRESPONSE,sequenceId);
	}
	
	public CmppCancelResponseMessage(Header header) {
		super(CmppPacketType.CMPPCANCELRESPONSE,header);
	}
	/**
	 * @return the successId
	 */
	public long getSuccessId() {
		return successId;
	}

	/**
	 * @param successId the successId to set
	 */
	public void setSuccessId(long successId) {
		this.successId = successId;
	}
	
}
