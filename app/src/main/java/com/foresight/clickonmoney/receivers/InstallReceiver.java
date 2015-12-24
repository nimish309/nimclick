package com.foresight.clickonmoney.receivers;

/**
 * Created by asd on 25-07-2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.UserDataPreferences;
import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.google.android.gms.analytics.Tracker;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class InstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("referrer onReceive==>", "InstallReceiver called");

        String rawReferrer = intent.getStringExtra("referrer");
        if (rawReferrer != null) {
//            trackReferrerAttributes(rawReferrer, context);
            Log.i("referrer onReceive==>", "" + rawReferrer);
            SharedPreferences.Editor editor = context.getSharedPreferences(
                    "refCodeInfo", Context.MODE_PRIVATE).edit().clear();

            editor.putString("referrer", rawReferrer);
            editor.commit();
        } else {
            Log.i("referrer onReceive==>", "null");
        }

    }

    private void trackReferrerAttributes(String rawReferrer, Context context) {
        String referrer = "";

        try {
            referrer = URLDecoder.decode(rawReferrer, "UTF-8");
            Log.i("referrer==>", "" + referrer);
        } catch (UnsupportedEncodingException e) {
            return;
        }

        if (isNullOrEmpty(referrer)) {
            return;
        }

        Uri uri = Uri.parse('?' + referrer); // appends ? for Uri to pickup query string
        Log.i("uri==>", "" + uri);
        String memberCode, referralMedium;
        try {
            memberCode = uri.getQueryParameter("mcode");
            referralMedium = uri.getQueryParameter("tc");
            Log.i("memberCode==>", memberCode + "==========>" + referralMedium);
        } catch (UnsupportedOperationException e) {
            return;
        }

        SharedPreferences.Editor editor = context.getSharedPreferences(
                "refCodeInfo", Context.MODE_PRIVATE).edit().clear();

        if (!isNullOrEmpty(memberCode)) {
            editor.putString("refCode", memberCode);
        }
        if (!isNullOrEmpty(referralMedium)) {
            editor.putString("refMedium", referralMedium);
        }

        editor.commit();
    }

    private boolean isNullOrEmpty(String str) {
        if (str == null) {
            return true;
        } else {
            if (str.equals("")) {
                return true;
            } else {
                return false;
            }
        }
    }
}