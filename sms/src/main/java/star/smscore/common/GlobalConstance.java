package star.smscore.common;

import io.netty.util.AttributeKey;

import java.nio.charset.Charset;

import org.marre.sms.SmsAlphabet;
import org.marre.sms.SmsDcs;
import org.marre.sms.SmsMsgClass;

import star.smscore.config.PropertiesUtils;
import star.smscore.handler.cmpp.BlackHoleHandler;
import star.smscore.handler.cmpp.CmppActiveTestRequestMessageHandler;
import star.smscore.handler.cmpp.CmppActiveTestResponseMessageHandler;
import star.smscore.handler.cmpp.CmppServerIdleStateHandler;
import star.smscore.handler.cmpp.CmppTerminateRequestMessageHandler;
import star.smscore.handler.cmpp.CmppTerminateResponseMessageHandler;
import star.smscore.handler.sgip.SgipServerIdleStateHandler;
import star.smscore.handler.smgp.SMGPServerIdleStateHandler;
import star.smscore.handler.smpp.SMPPServerIdleStateHandler;
import star.smscore.session.cmpp.SessionState;

public interface GlobalConstance {
	public final static int MaxMsgLength = 140;
	public final static String emptyString = "";
	public final static byte[] emptyBytes= new byte[0];
	public final static String[] emptyStringArray= new String[0];
  
    public static final Charset defaultTransportCharset = Charset.forName(PropertiesUtils.getDefaultTransportCharset());
    public static final SmsDcs defaultmsgfmt = SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.ASCII, SmsMsgClass.CLASS_UNKNOWN);
    public final static  CmppActiveTestRequestMessageHandler activeTestHandler =  new CmppActiveTestRequestMessageHandler();
    public final static  CmppActiveTestResponseMessageHandler activeTestRespHandler =  new CmppActiveTestResponseMessageHandler();
    public final static  CmppTerminateRequestMessageHandler terminateHandler =  new CmppTerminateRequestMessageHandler();
    public final static  CmppTerminateResponseMessageHandler terminateRespHandler = new CmppTerminateResponseMessageHandler();
    public final static  CmppServerIdleStateHandler idleHandler = new CmppServerIdleStateHandler();
    public final static  SMPPServerIdleStateHandler smppidleHandler = new SMPPServerIdleStateHandler();
    public final static  SgipServerIdleStateHandler sgipidleHandler = new SgipServerIdleStateHandler();
    public final static  SMGPServerIdleStateHandler smgpidleHandler = new SMGPServerIdleStateHandler();
    public final static AttributeKey<SessionState> attributeKey = AttributeKey.newInstance(SessionState.Connect.name());
    public final static BlackHoleHandler blackhole = new BlackHoleHandler();
    public final static String IdleCheckerHandlerName = "IdleStateHandler";
    public final static String loggerNamePrefix = "entity.%s";
    public final static String codecName = "codecName";
}
