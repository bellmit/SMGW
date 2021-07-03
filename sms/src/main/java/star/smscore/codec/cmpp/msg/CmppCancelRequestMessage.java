package star.smscore.codec.cmpp.msg;

import star.smscore.codec.cmpp.packet.CmppPacketType;
import star.smscore.common.util.MsgId;

/**
 *
 */
public class CmppCancelRequestMessage extends DefaultMessage {
	private static final long serialVersionUID = -4633530203133110407L;
	private MsgId msgId = new MsgId();
	
	public CmppCancelRequestMessage(Header header) {
		super(CmppPacketType.CMPPCANCELREQUEST,header);
	}

	public CmppCancelRequestMessage() {
		super(CmppPacketType.CMPPCANCELREQUEST);
	}
	/**
	 * @return the msgId
	 */
	public MsgId getMsgId() {
		return msgId;
	}

	/**
	 * @param msgId the msgId to set
	 */
	public void setMsgId(MsgId msgId) {
		this.msgId = msgId;
	}
	
}
