/**
 * 
 */
package star.smscore.codec.sgip12.packet;

import io.netty.handler.codec.MessageToMessageCodec;

import star.smscore.codec.cmpp.packet.PacketStructure;
import star.smscore.codec.cmpp.packet.PacketType;
import star.smscore.codec.sgip12.codec.SgipBindRequestMessageCodec;
import star.smscore.codec.sgip12.codec.SgipBindResponseMessageCodec;
import star.smscore.codec.sgip12.codec.SgipDeliverRequestMessageCodec;
import star.smscore.codec.sgip12.codec.SgipDeliverResponseMessageCodec;
import star.smscore.codec.sgip12.codec.SgipReportRequestMessageCodec;
import star.smscore.codec.sgip12.codec.SgipReportResponseMessageCodec;
import star.smscore.codec.sgip12.codec.SgipSubmitRequestMessageCodec;
import star.smscore.codec.sgip12.codec.SgipSubmitResponseMessageCodec;
import star.smscore.codec.sgip12.codec.SgipUnbindRequestMessageCodec;
import star.smscore.codec.sgip12.codec.SgipUnbindResponseMessageCodec;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public enum SgipPacketType implements PacketType {
	BINDREQUEST(0x00000001, SgipBindRequest.class,SgipBindRequestMessageCodec.class),
	BINDRESPONSE(0x80000001, SgipBindResponse.class,SgipBindResponseMessageCodec.class),
	UNBINDREQUEST(0x00000002, SgipUnbindRequest.class,SgipUnbindRequestMessageCodec.class),
	UNBINDRESPONSE(0x80000002, SgipUnbindResponse.class,SgipUnbindResponseMessageCodec.class),
	SUBMITREQUEST(0x00000003, SgipSubmitRequest.class,SgipSubmitRequestMessageCodec.class),
	SUBMITRESPONSE(0x80000003, SgipSubmitResponse.class,SgipSubmitResponseMessageCodec.class),
	DELIVERREQUEST(0x00000004, SgipDeliverRequest.class,SgipDeliverRequestMessageCodec.class),
	DELIVERRESPONSE(0x80000004, SgipDeliverResponse.class,SgipDeliverResponseMessageCodec.class),
	REPORTREQUEST(0x00000005, SgipReportRequest.class,SgipReportRequestMessageCodec.class),
	REPORTRESPONSE(0x80000005, SgipReportResponse.class,SgipReportResponseMessageCodec.class);

    private int commandId;
    private Class<? extends PacketStructure> packetStructure;
    private Class<? extends MessageToMessageCodec> codec;
    private SgipPacketType(int commandId, Class<? extends PacketStructure> packetStructure,Class<? extends MessageToMessageCodec> codec) {
        this.commandId = commandId;
        this.packetStructure = packetStructure;
        this.codec = codec;
    }
    public int getCommandId() {
        return commandId;
    }
    public PacketStructure[] getPacketStructures() {
    	return packetStructure.getEnumConstants();
    }

    public long getAllCommandId() {
        long defaultId = 0x0;
        long allCommandId = 0x0;
        for(SgipPacketType packetType : SgipPacketType.values()) {
            allCommandId |= packetType.commandId;
        }
        return allCommandId ^ defaultId;
    }
	@Override
	public MessageToMessageCodec getCodec() {
		try {
			return codec.newInstance();
		} catch (InstantiationException e) {
			return null;
		}
		catch(  IllegalAccessException e){
			return null;
		}
	}
}
