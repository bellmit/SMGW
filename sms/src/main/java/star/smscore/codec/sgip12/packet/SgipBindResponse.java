package star.smscore.codec.sgip12.packet;

import star.smscore.codec.cmpp.packet.DataType;
import star.smscore.codec.cmpp.packet.PacketStructure;
/**
 * 
 * @author huzorro(huzorro@gmail.com)
 *
 */
public  enum SgipBindResponse implements PacketStructure {
	RESULT(SgipDataType.UNSIGNEDINT, true, 1),
	RESERVE(SgipDataType.OCTERSTRING, true, 8);
	
	private DataType dataType;
    private boolean isFixFiledLength; 
    private int length;
    
    private SgipBindResponse(DataType dataType, boolean isFixFiledLength, int length) {
    	this.length = length;
    	this.dataType = dataType;
    	this.isFixFiledLength = isFixFiledLength;
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
		int bodyLength = 0;
		for(SgipBindResponse r : SgipBindResponse.values()) {
			bodyLength += r.getLength();
		}
		return bodyLength;
	}	

}
