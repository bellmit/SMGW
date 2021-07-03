package star.smscore.codec.cmpp.msg;

import star.smscore.codec.cmpp.packet.CmppPacketType;

/**
 *
 */
public class CmppTerminateRequestMessage extends DefaultMessage{
	private static final long serialVersionUID = 814288661389104951L;
	
	public CmppTerminateRequestMessage(Header header) {
		super(CmppPacketType.CMPPTERMINATEREQUEST, header);
	}
	public CmppTerminateRequestMessage() {
		super(CmppPacketType.CMPPTERMINATEREQUEST);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("CmppTerminateRequestMessage [toString()=%s]",
				super.toString());
	}
	
}
