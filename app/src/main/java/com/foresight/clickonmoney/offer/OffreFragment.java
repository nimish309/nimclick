package com.foresight.clickonmoney.offer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.Util.UserDataPreferences;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Calendar;

/**
 * Created by asd on 05-08-2015.
 */
public class OffreFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Offer";

    private MainActivity mainActivity;
    private InterstitialAd interstitial;

    private RelativeLayout layoutBanner;
    private LinearLayout layout_offer;
    private TextView txtTodayBalance, txtOffer, txtFull, txtVideo, txtNews;
    private JSONArray bannerArray, fullscreenArray, videoArray;

    private boolean isOpenAd;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final Context contextThemeWrapper = new ContextThemeWrapper(mainActivity, R.style.PollsTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.ads2, container, false);

        txtTodayBalance = (TextView) view.findViewById(R.id.txtTodayBalance);
        txtOffer = (TextView) view.findViewById(R.id.txtOffer);
        txtFull = (TextView) view.findViewById(R.id.txtFull);
        txtVideo = (TextView) view.findViewById(R.id.txtVideo);
        layoutBanner = (RelativeLayout) view.findViewById(R.id.layoutBanner);
        layout_offer = (LinearLayout) view.findViewById(R.id.layout_offer);

        txtTodayBalance.setText(UserDataPreferences.getTodaysBalance(mainActivity));

        txtOffer.setOnClickListener(this);
        txtFull.setOnClickListener(this);
        txtVideo.setOnClickListener(this);

        bannerArray = UserDataPreferences.getBannerIds(mainActivity);
        fullscreenArray = UserDataPreferences.getFullscreenIds(mainActivity);
        videoArray = UserDataPreferences.getVideoIds(mainActivity);

        setBackgroundColor(txtOffer);

        if (bannerArray != null) {
            try {
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();

                AdView ad1 = new AdView(mainActivity);
                ad1.setAdSize(AdSize.BANNER);
                ad1.setAdUnitId(bannerArray.getString(0));
                if (ad1.getAdSize() != null || ad1.getAdUnitId() != null)
                    ad1.loadAd(adRequest);
                ((LinearLayout) view.findViewById(R.id.adView1)).addView(ad1);

                AdView ad2 = new AdView(mainActivity);
                ad2.setAdSize(AdSize.BANNER);
                ad2.setAdUnitId(bannerArray.getString(1));
                if (ad2.getAdSize() != null || ad2.getAdUnitId() != null)
                    ad2.loadAd(adRequest);
                ((LinearLayout) view.findViewById(R.id.adView2)).addView(ad2);

                AdView ad3 = new AdView(mainActivity);
                ad3.setAdSize(AdSize.BANNER);
                ad3.setAdUnitId(bannerArray.getString(2));
                if (ad3.getAdSize() != null || ad3.getAdUnitId() != null)
                    ad3.loadAd(adRequest);
                ((LinearLayout) view.findViewById(R.id.adView3)).addView(ad3);

                ad1.setAdListener(new AdListener() {
                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        Log.d("onPause 1==>", "" + Calendar.getInstance().getTimeInMillis());
                        UserDataPreferences.setTimer(mainActivity, Calendar.getInstance().getTimeInMillis());
                        isOpenAd = true;
                    }


                });

                ad2.setAdListener(new AdListener() {
                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        Log.d("onPause 2==>", "" + Calendar.getInstance().getTimeInMillis());
                        UserDataPreferences.setTimer(mainActivity, Calendar.getInstance().getTimeInMillis());
                        isOpenAd = true;
                    }

                });

                ad3.setAdListener(new AdListener() {
                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        Log.d("onPause 3==>", "" + Calendar.getInstance().getTimeInMillis());
                        UserDataPreferences.setTimer(mainActivity, Calendar.getInstance().getTimeInMillis());
                        isOpenAd = true;
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        txtNews = (TextView) view.findViewById(R.id.txtNews);
        txtNews.setSelected(true);

        try {
            JSONObject news = UserDataPreferences.getNews(mainActivity);
            if (news != null) {
                txtNews.setText(news.has("description") ? (news.isNull("description") ? "" : news.getString("description")) : "");
            } else {
                txtNews.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void setBackgroundColor(TextView textView) {
        txtOffer.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        txtFull.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        txtVideo.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        textView.setBackgroundColor(mainActivity.getResources().getColor(R.color.actionbar));
    }

    @Override
    public void onClick(View v) {
        layoutBanner.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.txtOffer:
                layoutBanner.setVisibility(View.VISIBLE);
                setBackgroundColor(txtOffer);
                break;
            case R.id.txtFull:
                if (fullscreenArray != null) {
                    showFullScreen();
                }
                setBackgroundColor(txtFull);
                break;
            case R.id.txtVideo:
                if (videoArray != null) {
                    showVideo();
                }
                setBackgroundColor(txtVideo);
                break;
        }
    }

    private void showFullScreen() {
        try {
            // Prepare the Interstitial Ad
            interstitial = new InterstitialAd(getActivity());
            // Insert the Ad Unit ID
            interstitial.setAdUnitId(fullscreenArray.getString(0));

            // Request for Ads
            AdRequest adRequest = new AdRequest.Builder().build();


            // Load ads into Interstitial Ads
            interstitial.loadAd(adRequest);

            // Prepare an Interstitial Ad Listener
            interstitial.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    // Call displayInterstitial() function
                    if (interstitial.isLoaded()) {
                        interstitial.show();
                        UserDataPreferences.setTimer(mainActivity, Calendar.getInstance().getTimeInMillis());
                        isOpenAd = true;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showVideo() {
        try {
            interstitial = new InterstitialAd(mainActivity);
            interstitial.setAdUnitId(videoArray.getString(0));

            AdRequest adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);

            interstitial.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    if (interstitial.isLoaded()) {
                        interstitial.show();
                        UserDataPreferences.setTimer(mainActivity, Calendar.getInstance().getTimeInMillis());
                        isOpenAd = true;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isOpenAd) {
            Log.d("Resume==>", "Resume");
            long time = UserDataPreferences.getTimer(mainActivity);
            long currentTime = Calendar.getInstance().getTimeInMillis();
            Log.d("time==>" + time, currentTime + "==========>" + (currentTime - time));
            if (time != 0) {
                if (currentTime - time > 50000 && currentTime - time < 600000) {
                    new SendAdCreditRequest().execute();
                }
            }
            UserDataPreferences.setTimer(mainActivity, 0);
            isOpenAd = false;
        }
    }

    private class SendAdCreditRequest extends AsyncTask<Void, Void, Void> {

        private boolean flag;
        private int responseCode;
        private String message, data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            JSONStringer jsonStringer;
            try {
                jsonStringer = new JSONStringer().object().key("user_unique_id").value(UserDataPreferences.getUniqueId(mainActivity));
                AppConstant.setDeviceInfo(jsonStringer, mainActivity);
                jsonStringer.endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_credit_advertise, jsonStringer.toString());
                Log.d("data==>" + jsonData[0], " " + jsonData[1]);
                responseCode = Integer.parseInt(jsonData[0]);

                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    message = item.getString("message");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {

                if (responseCode == 200) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
