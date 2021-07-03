package star.smscore.codec.sgip12.msg;

import star.smscore.codec.cmpp.msg.DefaultMessage;
import star.smscore.codec.cmpp.msg.Header;
import star.smscore.codec.cmpp.packet.PacketType;
import star.smscore.common.util.SequenceNumber;

public abstract class SgipDefaultMessage extends DefaultMessage {

	public SgipDefaultMessage(PacketType packetType, Header header) {
		super(packetType,header);
	}
	public SgipDefaultMessage(PacketType packetType) {
		super(packetType);
	}
	public SequenceNumber getSequenceNumber() {
		return new SequenceNumber(getTimestamp(),getHeader().getNodeId(),getSequenceNo()) ;
	}
}
