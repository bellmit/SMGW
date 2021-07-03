package star.smscore.codec.cmpp.wap;

import org.marre.sms.SmsMessage;

import star.smscore.LongSMSMessage;

class SmsMessageHolder {
	SmsMessage smsMessage;
	LongSMSMessage msg;
	SmsMessageHolder(SmsMessage smsMessage,LongSMSMessage msg){
		this.msg = msg;
		this.smsMessage = smsMessage;
	}
}
