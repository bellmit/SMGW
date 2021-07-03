/**
 * 
 */
package star.smscore.codec.cmpp7F.packet;

import io.netty.handler.codec.MessageToMessageCodec;

import star.smscore.codec.cmpp.CmppActiveTestRequestMessageCodec;
import star.smscore.codec.cmpp.CmppActiveTestResponseMessageCodec;
import star.smscore.codec.cmpp.CmppCancelRequestMessageCodec;
import star.smscore.codec.cmpp.CmppCancelResponseMessageCodec;
import star.smscore.codec.cmpp.CmppConnectRequestMessageCodec;
import star.smscore.codec.cmpp.CmppConnectResponseMessageCodec;
import star.smscore.codec.cmpp.CmppDeliverResponseMessageCodec;
import star.smscore.codec.cmpp.CmppQueryRequestMessageCodec;
import star.smscore.codec.cmpp.CmppQueryResponseMessageCodec;
import star.smscore.codec.cmpp.CmppSubmitResponseMessageCodec;
import star.smscore.codec.cmpp.CmppTerminateRequestMessageCodec;
import star.smscore.codec.cmpp.CmppTerminateResponseMessageCodec;
import star.smscore.codec.cmpp.packet.CmppActiveTestRequest;
import star.smscore.codec.cmpp.packet.CmppActiveTestResponse;
import star.smscore.codec.cmpp.packet.CmppCancelRequest;
import star.smscore.codec.cmpp.packet.CmppCancelResponse;
import star.smscore.codec.cmpp.packet.CmppConnectRequest;
import star.smscore.codec.cmpp.packet.CmppConnectResponse;
import star.smscore.codec.cmpp.packet.CmppDeliverRequest;
import star.smscore.codec.cmpp.packet.CmppDeliverResponse;
import star.smscore.codec.cmpp.packet.CmppQueryRequest;
import star.smscore.codec.cmpp.packet.CmppQueryResponse;
import star.smscore.codec.cmpp.packet.CmppSubmitRequest;
import star.smscore.codec.cmpp.packet.CmppSubmitResponse;
import star.smscore.codec.cmpp.packet.CmppTerminateRequest;
import star.smscore.codec.cmpp.packet.CmppTerminateResponse;
import star.smscore.codec.cmpp.packet.PacketStructure;
import star.smscore.codec.cmpp.packet.PacketType;
import star.smscore.codec.cmpp7F.Cmpp7FDeliverRequestMessageCodec;
import star.smscore.codec.cmpp7F.Cmpp7FSubmitRequestMessageCodec;


/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public enum Cmpp7FPacketType implements PacketType {
    CMPPCONNECTREQUEST(0x00000001, CmppConnectRequest.class,CmppConnectRequestMessageCodec.class),
    CMPPCONNECTRESPONSE(0x80000001, CmppConnectResponse.class,CmppConnectResponseMessageCodec.class),
    CMPPTERMINATEREQUEST(0x00000002, CmppTerminateRequest.class,CmppTerminateRequestMessageCodec.class),
    CMPPTERMINATERESPONSE(0x80000002, CmppTerminateResponse.class,CmppTerminateResponseMessageCodec.class),    
    CMPPSUBMITREQUEST(0x00000004, CmppSubmitRequest.class,Cmpp7FSubmitRequestMessageCodec.class), 
    CMPPSUBMITRESPONSE(0x80000004, CmppSubmitResponse.class,CmppSubmitResponseMessageCodec.class),
    CMPPDELIVERREQUEST(0x00000005, CmppDeliverRequest.class,Cmpp7FDeliverRequestMessageCodec.class),
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
    
    private Cmpp7FPacketType(int commandId, Class<? extends PacketStructure> packetStructure,Class<? extends MessageToMessageCodec> codec) {
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
        for(Cmpp7FPacketType packetType : Cmpp7FPacketType.values()) {
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
