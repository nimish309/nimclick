package com.foresight.clickonmoney.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReciver extends BroadcastReceiver {
	@SuppressWarnings({ "unused" })
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		SmsMessage[] msgs = null;
		String str = "";
		String messageBody = "", MobileNo = "";
		if (bundle != null) {
			// ---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get("pdus");

			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				str += "SMS from " + msgs[i].getOriginatingAddress();
				str += " :";
				str += msgs[i].getMessageBody().toString();
				str += "\n";
				messageBody = msgs[i].getMessageBody().toString();
				MobileNo = msgs[i].getOriginatingAddress();
			}

			if (messageBody
					.contains("Your ClickOnMoney")) {
				Log.d("SMS Received matched", messageBody);

				String code = messageBody.substring(messageBody.length() - 6,
						messageBody.length());

				Intent i = new Intent("SMSRECEIVED").putExtra("code", code);
				context.sendBroadcast(i);
				
			} else {
				abortBroadcast();
				Log.d("", "In Else" + MobileNo);
			}
		}

	}

}
