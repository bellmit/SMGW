/**
 * 
 */
package star.smscore.codec.cmpp20.packet;

import star.smscore.codec.cmpp.packet.CmppDataType;
import star.smscore.codec.cmpp.packet.DataType;
import star.smscore.codec.cmpp.packet.PacketStructure;



/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public enum Cmpp20ConnectResponse implements PacketStructure {
    STATUS(CmppDataType.UNSIGNEDINT, true, 1),
    AUTHENTICATORISMG(CmppDataType.OCTERSTRING, true, 16),
    VERSION(CmppDataType.UNSIGNEDINT, true, 1);
    private DataType dataType;
    private boolean isFixFiledLength; 
    private int length;
    private Cmpp20ConnectResponse(DataType dataType, boolean isFixFiledLength, int length) {
        this.dataType = dataType;
        this.isFixFiledLength = isFixFiledLength;
        this.length = length;
    }
    public DataType getDataType() {
        return dataType;
    }
    public boolean isFixFiledLength() {
        return isFixFiledLength;
    }
    public boolean isFixPacketLength() {
    	return true;
    }
    public int getLength() {
        return length;
    }
    public int getBodyLength() {
        
        return 18;
    }
}
