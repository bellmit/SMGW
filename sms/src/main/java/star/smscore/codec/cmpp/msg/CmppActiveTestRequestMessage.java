package star.smscore.codec.cmpp.msg;

import star.smscore.codec.cmpp.packet.CmppPacketType;

/**
 * @author
 */
public class CmppActiveTestRequestMessage extends DefaultMessage {
	private static final long serialVersionUID = 4496674961657465872L;
	
	public CmppActiveTestRequestMessage() {
		super(CmppPacketType.CMPPACTIVETESTREQUEST);
	}
	public CmppActiveTestRequestMessage(Header header) {
		super(CmppPacketType.CMPPACTIVETESTREQUEST,header);
	}
}
