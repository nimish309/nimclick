package com.foresight.clickonmoney.pushnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class PNotificationRegister {
	Context context;

	String strRegId;

	public PNotificationRegister(Context context) {
		this.context = context;
	}

	public void registerGcmService() {
		try {
			// Make sure the device has the proper dependencies.
			GCMRegistrar.checkDevice(context);

			// Make sure the manifest permissions was properly set
			GCMRegistrar.checkManifest(context);

			context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
					GCMConfig.DISPLAY_REGISTRATION_MESSAGE_ACTION));

			// Get GCM registration id
			strRegId = GCMRegistrar.getRegistrationId(context);

			Log.v("Get RegId GCM REGISTRAR", strRegId);

			GCMRegistrar.register(context, GCMConfig.GOOGLE_SENDER_ID);
			// Check if regid already presents
			// if (strRegId.equals("")) {
			//
			// Log.i("GCM", "--- Regid = ''" + strRegId);
			//
			// // Register with GCM
			// GCMRegistrar.register(context, GCMConfig.GOOGLE_SENDER_ID);
			//
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void unRegisterFromGCMService(Context context) {
		GCMRegistrar.unregister(context);
	}

	// Create a broadcast receiver to get message and show on screen
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			@SuppressWarnings("unused")
			String newMessage = intent.getExtras().getString(
					GCMConfig.EXTRA_MESSAGE);

			// Waking up mobile if it is sleeping
			WakeLocker.acquire(PNotificationRegister.this.context);

			// Releasing wake lock
			WakeLocker.release();
		}
	};

}
