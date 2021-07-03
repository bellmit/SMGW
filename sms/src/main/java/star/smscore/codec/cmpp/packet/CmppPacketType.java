/**
 * 
 */
package star.smscore.codec.cmpp.packet;

import io.netty.handler.codec.MessageToMessageCodec;

import star.smscore.codec.cmpp.CmppActiveTestRequestMessageCodec;
import star.smscore.codec.cmpp.CmppActiveTestResponseMessageCodec;
import star.smscore.codec.cmpp.CmppCancelRequestMessageCodec;
import star.smscore.codec.cmpp.CmppCancelResponseMessageCodec;
import star.smscore.codec.cmpp.CmppConnectRequestMessageCodec;
import star.smscore.codec.cmpp.CmppConnectResponseMessageCodec;
import star.smscore.codec.cmpp.CmppDeliverRequestMessageCodec;
import star.smscore.codec.cmpp.CmppDeliverResponseMessageCodec;
import star.smscore.codec.cmpp.CmppQueryRequestMessageCodec;
import star.smscore.codec.cmpp.CmppQueryResponseMessageCodec;
import star.smscore.codec.cmpp.CmppSubmitRequestMessageCodec;
import star.smscore.codec.cmpp.CmppSubmitResponseMessageCodec;
import star.smscore.codec.cmpp.CmppTerminateRequestMessageCodec;
import star.smscore.codec.cmpp.CmppTerminateResponseMessageCodec;


/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public enum CmppPacketType implements PacketType {
    CMPPCONNECTREQUEST(0x00000001, CmppConnectRequest.class,CmppConnectRequestMessageCodec.class),
    CMPPCONNECTRESPONSE(0x80000001, CmppConnectResponse.class,CmppConnectResponseMessageCodec.class),
    CMPPTERMINATEREQUEST(0x00000002, CmppTerminateRequest.class,CmppTerminateRequestMessageCodec.class),
    CMPPTERMINATERESPONSE(0x80000002, CmppTerminateResponse.class,CmppTerminateResponseMessageCodec.class),    
    CMPPSUBMITREQUEST(0x00000004, CmppSubmitRequest.class,CmppSubmitRequestMessageCodec.class), 
    CMPPSUBMITRESPONSE(0x80000004, CmppSubmitResponse.class,CmppSubmitResponseMessageCodec.class),
    CMPPDELIVERREQUEST(0x00000005, CmppDeliverRequest.class,CmppDeliverRequestMessageCodec.class),
    CMPPDELIVERRESPONSE(0x80000005, CmppDeliverResponse.class,CmppDeliverResponseMessageCodec.class),    
    CMPPQUERYREQUEST(0x00000006, CmppQueryRequest.class,CmppQueryRequestMessageCodec.class),
    CMPPQUERYRESPONSE(0x80000006, CmppQueryResponse.class,CmppQueryResponseMessageCodec.class),
    CMPPCANCELREQUEST(0x00000007, CmppCancelRequest.class,CmppCancelRequestMessageCodec.class),
    CMPPCANCELRESPONSE(0x80000007, CmppCancelResponse.class,CmppCancelResponseMessageCodec.class),
    CMPPACTIVETESTREQUEST(0x00000008, CmppActiveTestRequest.class,CmppActiveTestRequestMessageCodec.class),
    CMPPACTIVETESTRESPONSE(0x80000008, CmppActiveTestResponse.class,CmppActiveTestResponseMessageCodec.class);
    
    private int commandId;
    private Class<? extends PacketStructure> packetStructure;
    private Class<? extends MessageToMessageCodec> codec;
    
    private CmppPacketType(int commandId, Class<? extends PacketStructure> packetStructure,Class<? extends MessageToMessageCodec> codec) {
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
        for(CmppPacketType packetType : CmppPacketType.values()) {
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
