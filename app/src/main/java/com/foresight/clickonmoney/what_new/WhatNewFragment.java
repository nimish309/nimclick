package com.foresight.clickonmoney.what_new;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.UserDataPreferences;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.network.NetworkListAdapter;
import com.foresight.clickonmoney.recyclerviewflexibledivider.AlphaInAnimationAdapter;
import com.foresight.clickonmoney.recyclerviewflexibledivider.SlideInRightAnimationAdapter;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

/**
 * Created by Lucky on 16/08/15.
 */


public class WhatNewFragment extends Fragment {

    public static final String TAG = "What is New?";
    private MainActivity mainActivity;

    private RecyclerView whatNewListView;
    private RecyclerView.LayoutManager myLayoutManager;
    private ProgressBar progressBar;
    private WhatNewListAdapter adapter;

    private InterstitialAd interstitial;
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
        View view = localInflater.inflate(R.layout.what_new_fragment, container, false);

        fullscreenArray = UserDataPreferences.getFullscreenIds(mainActivity);
        if (fullscreenArray != null) {
            showFullScreen();
        }

        whatNewListView = (RecyclerView) view.findViewById(R.id.whatNewListView);
        whatNewListView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(mainActivity);
        whatNewListView.setLayoutManager(myLayoutManager);

        if (AppConstant.isNetworkAvailable(mainActivity)) {
            new WhatNewListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            AppConstant.showNetworkError(mainActivity);
        }

        return view;
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
    private class WhatNewListTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message;
        private JSONArray data;
        private ArrayList<String> whatNewList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mainActivity);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            try {
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_what_new);
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    message = item.getString("message");
                    if (flag) {
                        data = item.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            whatNewList.add(data.getJSONObject(i).toString());
                        }
                    }
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
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                if (responseCode == 200) {
                    if (flag) {
                        adapter = new WhatNewListAdapter(mainActivity, whatNewList);
                        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(
                                adapter);
                        SlideInRightAnimationAdapter scaleAdapter = new SlideInRightAnimationAdapter(
                                alphaAdapter);
                        scaleAdapter.setFirstOnly(false);
                        scaleAdapter.setInterpolator(new OvershootInterpolator());
                        whatNewListView.setAdapter(scaleAdapter);
//                        whatNewListView.setAdapter(adapter);
                    } else {
                        AppConstant.showToastShort(mainActivity, message);
                    }
                } else {
                    AppConstant.unableConnectServer(mainActivity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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
