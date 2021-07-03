/**
 * 
 */
package star.smscore.codec.sgip12.msg;

import star.smscore.codec.cmpp.msg.Header;
import star.smscore.codec.sgip12.packet.SgipPacketType;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public class SgipUnbindRequestMessage extends SgipDefaultMessage {
	
	private static final long serialVersionUID = 6344903835739798820L;
	public SgipUnbindRequestMessage() {
		super(SgipPacketType.UNBINDREQUEST);
	}
	
	public SgipUnbindRequestMessage(Header header) {
		super(SgipPacketType.UNBINDREQUEST,header);
	}
	
}
