package com.foresight.clickonmoney.Util;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.foresight.clickonmoney.pushnotification.GCMConfig;
import com.google.android.gcm.GCMBaseIntentService;

//AIzaSyC0UMt2nujXBTq68bwle0uRFrH7E-F1a5M  browser key 
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	SharedPreferences sharedpreferences;
	String token;

	public GCMIntentService() {
		// Call extended class Constructor GCMBaseIntentService
		super(GCMConfig.GOOGLE_SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {

		Log.i(TAG, "---------- onRegistered -------------");
		Log.i(TAG, "Device registered: regId = " + registrationId);
		// Log.d("NAME", PushNotificationRegistration.name);
		token = registrationId;

		try {
			AWSCredentials awsCredentials = new BasicAWSCredentials(
					Constants.AwsAccessKey, Constants.AwsSecretAccessKey);
			AmazonSNSClient pushClient = new AmazonSNSClient(awsCredentials);
			pushClient.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
			// US_WEST_2,ap-southeast-1
			CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
			platformEndpointRequest.setToken(registrationId);
			platformEndpointRequest
					.setPlatformApplicationArn(Constants.platformApplicationArn);

			CreatePlatformEndpointResult result = pushClient
					.createPlatformEndpoint(platformEndpointRequest);

			Log.v("Endpoint",
					" endpointArn------------------------->"
							+ result.getEndpointArn());
			String strEndArn = result.getEndpointArn();

//			String strCell = UserDataPreferences
//					.getCellNo(getApplicationContext());

//			SaveRegIdToServer(strCell, strEndArn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SaveRegIdToServer(String strCell, String strEndArn) {
		JSONStringer jsonStringer = null;
		String imei = "";
		try {
			TelephonyManager tManager = (TelephonyManager) getApplicationContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tManager.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// String android_id =
		// Secure.getString(getApplicationContext().getContentResolver(),
		// Secure.ANDROID_ID);
//		try {
//			jsonStringer = new JSONStringer().object().key("cell")
//					.value(strCell).key("android_endpointArn").value(strEndArn)
//					.key("imei").value(imei).endObject();
//
//			JSONParser jparser = new JSONParser(getApplicationContext());
//			String json = jparser.sendPostReq(Constants.api
//					+ Constants.api_save_endpoint_arn, jsonStringer.toString());
//			// Log.d("Json", json);
//			if (json != null) {
//				JSONObject jObj = new JSONObject(json);
//				String flag;
//				flag = jObj.getString("flag");
//				if (flag.equalsIgnoreCase("true")) {
//					UserDataPreferences.saveEndpointArn(
//							getApplicationContext(), strEndArn);
//				}
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	/**
	 * Method called on device unregistred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "---------- onUnregistered -------------");
		Log.i(TAG, "Device unregistered");

//		sharedpreferences = getSharedPreferences(
//				UserDataPreferences.PUSH_NOTIFICATION_PREF,
//				Context.MODE_PRIVATE);
//
//		Editor editor = sharedpreferences.edit();
//		editor.clear();
//		editor.commit();
	}

	/**
	 * Method called on Receiving a new message from GCM server
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "---------- onMessage -------------");
		String message = intent.getExtras().getString("message");
		Log.i("GCM", "message : " + message);
//		try {
//			generateNotification(context, "COM", message);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {

		Log.i(TAG, "---------- onError -------------");
		Log.i(TAG, "Received error: " + errorId);

	}

	/**
	 * Create a notification to inform the user that msg received
	 * 
	 * @throws JSONException
	 */
//	private void generateNotification(Context context, String title,
//			String message) throws JSONException {
//		Log.i(TAG, "---------- generateNotification -------------");
//		try {
//			Intent notificationIntent = null;
//
//			JSONObject jObj = new JSONObject(message);
//			boolean is_member = false;
//			String strMessageToDisplay = jObj.has("message") ? jObj
//					.getString("message") : "";
//
//			is_member = jObj.has("is_member") ? jObj.getBoolean("is_member")
//					: false;
//			UserDataPreferences.saveIsMember(context, is_member);
//			if (is_member) {
//				notificationIntent = new Intent(context, MainActivity.class);
//				notificationIntent.putExtra("is_member", is_member);
//			} else {
//				String strEventId = jObj.has("id") ? jObj.getString("id") : "";
//				String strEventName = jObj.has("name") ? jObj.getString("name")
//						: "";
//				String strEventObj = jObj.has("event") ? jObj
//						.getString("event") : "";
//				String strArticleUrl = jObj.has("article_url") ? jObj
//						.getString("article_url") : "";
//
//				// Log.d("Message", strMessageToDisplay + "\n Event Id" +
//				// strEventId
//				// + "\n Event Name" + strEventName + "\n Article Url"
//				// + strArticleUrl);
//
//				if (strEventId.equals("") && strArticleUrl.equals("")) {
//					notificationIntent = new Intent(context,
//							NotificationMessageView.class);
//					notificationIntent.putExtra("message", strMessageToDisplay);
//					// notificationIntent1.setData(Uri.parse(""
//					// + (int) System.currentTimeMillis()));
//				} else if (!strEventId.equals("")) {
//					notificationIntent = new Intent(context,
//							EventDetailActivity.class);
//					notificationIntent.putExtra("message", strEventName);
//					notificationIntent.putExtra("eventId", strEventId);
//					notificationIntent.putExtra("eventObj", strEventObj);
//					// notificationIntent1.setData(Uri.parse(""
//					// + (int) System.currentTimeMillis()));
//				} else if (!strArticleUrl.equals("")) {
//					notificationIntent = new Intent(Intent.ACTION_VIEW);
//					notificationIntent.setData(Uri.parse(strArticleUrl));
//				}
//			}
//			/**
//			 * Adding content to the notificationIntent, which will be displayed
//			 * on viewing the notification
//			 */
//			notificationIntent.putExtra("name", title);
//
//			/**
//			 * This is needed to make this intent different from its previous
//			 * intents
//			 */
//			// notificationIntent1.setData(Uri.parse(""
//			// + (int) System.currentTimeMillis()));
//
//			/**
//			 * Creating different tasks for each notification. See the flag
//			 * Intent.FLAG_ACTIVITY_NEW_TASK
//			 */
//			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//					notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
//
//			/** Getting the System service NotificationManager */
//			NotificationManager nManager = (NotificationManager) getApplicationContext()
//					.getSystemService(Context.NOTIFICATION_SERVICE);
//
//			/** Configuring notification builder to create a notification */
//			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
//					getApplicationContext())
//					.setWhen(System.currentTimeMillis())
//					.setContentText(title)
//					.setContentTitle(strMessageToDisplay)
//					.setSmallIcon(R.drawable.app_icon)
//					.setAutoCancel(true)
//					.setTicker(strMessageToDisplay)
//					.setContentIntent(pendingIntent)
//					.setSound(
//							RingtoneManager
//									.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
//
//			/** Creating a notification from the notification builder */
//			Notification notification1 = notificationBuilder.build();
//			notification1.defaults = Notification.DEFAULT_ALL;
//			/**
//			 * Sending the notification to system. The first argument ensures
//			 * that each notification is having a unique id If two notifications
//			 * share same notification id, then the last notification replaces
//			 * the first notification
//			 * */
//			nManager.notify((int) System.currentTimeMillis(), notification1);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
