package com.foresight.clickonmoney.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.Util.UserDataPreferences;
import com.foresight.clickonmoney.recyclerviewflexibledivider.AlphaInAnimationAdapter;
import com.foresight.clickonmoney.recyclerviewflexibledivider.SlideInRightAnimationAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdListener;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

/**
 * Created by asd on 04-08-2015.
 */
public class AdvanceNetworkFragment extends Fragment {

    public static final String TAG = "My Network";
    private MainActivity mainActivity;

    private RecyclerView networkListView;

    private RecyclerView.LayoutManager myLayoutManager;
    private ProgressBar progressBar;
    private NetworkListAdapter adapter;


    private String unique_id;
    private ArrayList<String> networkList;
    private InterstitialAd interstitial;
    private JSONArray bannerArray, fullscreenArray, videoArray;

    private boolean isOpenAd;

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public void setNetworkList(ArrayList<String> networkList) {
        this.networkList = networkList;
    }

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
        View view = localInflater.inflate(R.layout.network_fragment, container, false);

        fullscreenArray = UserDataPreferences.getFullscreenIds(mainActivity);
        if (fullscreenArray != null) {
            showFullScreen();
        }

        bannerArray = UserDataPreferences.getBannerIds(mainActivity);
        if (bannerArray != null) {
            try {
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();

                AdView ad1 = new AdView(mainActivity);
                ad1.setAdSize(AdSize.BANNER);
                ad1.setAdUnitId(bannerArray.getString(3));
                if (ad1.getAdSize() != null || ad1.getAdUnitId() != null)
                    ad1.loadAd(adRequest);
                ((LinearLayout) view.findViewById(R.id.adView4)).addView(ad1);

                ad1.setAdListener(new AdListener() {
                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        Log.d("onPause 1==>", "" + Calendar.getInstance().getTimeInMillis());
                        UserDataPreferences.setTimer(mainActivity, Calendar.getInstance().getTimeInMillis());
                        isOpenAd = true;
                    }


                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        AdView ad4 = (AdView) view.findViewById(R.id.adView4);
//        ad4.loadAd(new AdRequest.Builder().build());

        networkListView = (RecyclerView) view.findViewById(R.id.networkListView);
        networkListView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(mainActivity);
        networkListView.setLayoutManager(myLayoutManager);


        adapter = new NetworkListAdapter(mainActivity, networkList, true, AdvanceNetworkFragment.this);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(
                adapter);
        SlideInRightAnimationAdapter scaleAdapter = new SlideInRightAnimationAdapter(
                alphaAdapter);
        scaleAdapter.setFirstOnly(false);
        scaleAdapter.setInterpolator(new OvershootInterpolator());
        networkListView.setAdapter(scaleAdapter);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Log.d("Back Fragment==>", mainActivity.getSupportFragmentManager().getBackStackEntryCount() + "");
                        if (mainActivity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            mainActivity.getSupportFragmentManager().popBackStack();
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        return view;
    }
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
