/**
 * 
 */
package star.smscore.codec.cmpp20.packet;

import star.smscore.codec.cmpp.packet.CmppDataType;
import star.smscore.codec.cmpp.packet.DataType;
import star.smscore.codec.cmpp.packet.PacketStructure;



/**
 * shifei(shifei@asiainfo.com)
 *
 */
public enum Cmpp20DeliverResponse implements PacketStructure {
	MSGID(CmppDataType.UNSIGNEDINT, true, 8),
	RESULT(CmppDataType.UNSIGNEDINT, true, 1);
	
    private DataType dataType;
    private boolean isFixFiledLength; 
    private int length;
    
    private Cmpp20DeliverResponse(DataType dataType, boolean isFixFiledLength, int length) {
        this.dataType = dataType;
        this.isFixFiledLength = isFixFiledLength;
        this.length = length;
    }
	@Override
	public DataType getDataType() {
		return dataType;
	}

	@Override
	public boolean isFixFiledLength() {
		return isFixFiledLength;
	}

	@Override
	public boolean isFixPacketLength() {
		return true;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public int getBodyLength() {
       
        return 9;
	}
}
