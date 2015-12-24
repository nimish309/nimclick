package com.foresight.clickonmoney.help;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.JSONData;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.Util.UserDataPreferences;
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
public class HelpFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Help & Support";
    private MainActivity mainActivity;

    private Spinner spinnerSubject;
    private EditText etQuery;
    private Button btnSubmit;

    private String strQuery;
    private ArrayList<String> subjectList = new ArrayList<>();
    private ArrayAdapter adapter;

    private LinearLayout layouthelpRequest, layouthelphistory;
    private TextView txtHelpRequest, txtHelpHistory;

    private InterstitialAd interstitial;
    private JSONArray bannerArray, fullscreenArray, videoArray;

    //For Pending
    private RecyclerView helpHistoryListView;
    private RecyclerView.LayoutManager myLayoutManager;
    private ProgressBar progressBar;
    private HelpHistoryListAdapter historyadapter;

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
        View view = localInflater.inflate(R.layout.help_fragment, container, false);

        fullscreenArray = UserDataPreferences.getFullscreenIds(mainActivity);
        if (fullscreenArray != null) {
            showFullScreen();
        }

        layouthelpRequest = (LinearLayout) view.findViewById(R.id.layouthelpRequest);
        layouthelphistory = (LinearLayout) view.findViewById(R.id.layouthelphistory);

        txtHelpRequest = (TextView) view.findViewById(R.id.txtHelpRequest);
        txtHelpHistory = (TextView) view.findViewById(R.id.txtHelpHistory);

        spinnerSubject = (Spinner) view.findViewById(R.id.spinnerSubject);
        etQuery = (EditText) view.findViewById(R.id.etQuery);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);

        subjectList.add("- Select -");
        subjectList.add("Recharge");
        subjectList.add("Network");
        subjectList.add("Redeem");
        subjectList.add("Other");

        adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, subjectList);
        spinnerSubject.setAdapter(adapter);

        // For Pending
        helpHistoryListView = (RecyclerView) view.findViewById(R.id.helpHistoryListView);
        helpHistoryListView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(mainActivity);
        helpHistoryListView.setLayoutManager(myLayoutManager);

        txtHelpRequest.setOnClickListener(this);
        txtHelpHistory.setOnClickListener(this);
        setBackgroundColor(txtHelpRequest, layouthelpRequest);
        btnSubmit.setOnClickListener(this);

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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (spinnerSubject.getSelectedItemPosition() == 0) {
                    AppConstant.showToastShort(mainActivity, "Please select subject for query.");
                } else {
                    strQuery = etQuery.getText().toString().trim();
                    if (strQuery.equals("")) {
                        AppConstant.showToastShort(mainActivity, "Please enter query");
                    } else {
                        if (AppConstant.isNetworkAvailable(mainActivity)) {
                            new SendQueryTask().execute();
                        } else {
                            AppConstant.showNetworkError(mainActivity);
                        }
                    }
                }
                break;
            case R.id.txtHelpRequest:
                setBackgroundColor(txtHelpRequest, layouthelpRequest);
                break;
            case R.id.txtHelpHistory:
                setBackgroundColor(txtHelpHistory, layouthelphistory);
                if (historyadapter == null) {
                    if (AppConstant.isNetworkAvailable(mainActivity)) {
                        new HelpHistoryListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        AppConstant.showNetworkError(mainActivity);
                    }
                }
                break;
        }
    }

    private void setBackgroundColor(TextView textView, LinearLayout linearLayout) {
        txtHelpRequest.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        txtHelpHistory.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        textView.setBackgroundColor(mainActivity.getResources().getColor(R.color.actionbar));
        layouthelpRequest.setVisibility(View.GONE);
        layouthelphistory.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    public class SendQueryTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message;
        private int selectedItem;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            selectedItem = spinnerSubject.getSelectedItemPosition();
            dialog = new ProgressDialog(mainActivity);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            JSONStringer jsonStringer;
            try {

                jsonStringer = new JSONStringer().object().key("user_unique_id").value(UserDataPreferences.getUniqueId(mainActivity))
                        .key("type").value(subjectList.get(selectedItem))
                        .key("description").value(strQuery).key("version").value(mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0).versionCode).endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_help_support, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    message = item.has("message") ? (item.isNull("message") ? null : item.getString("message")) : null;
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
                    if (message != null) {
                        AppConstant.showSingleButtonAlertDialog(mainActivity, "Thank You...", message);
                    }
                    etQuery.setText("");
                    spinnerSubject.setSelection(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private class HelpHistoryListTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message;
        private JSONArray data;
        private ArrayList<String> helpHistoryList = new ArrayList<>();

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
            JSONStringer jsonStringer;
            try {
                jsonStringer = new JSONStringer().object().key("user_unique_id").value(UserDataPreferences.getUniqueId(mainActivity)).endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_help_history, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = JSONData.getBoolean(item, "flag");
                    message = JSONData.getString(item, "message");
                    if (flag) {
                        data = JSONData.getJSONArray(item, "data");
                        for (int i = 0; i < data.length(); i++) {
                            helpHistoryList.add(data.getJSONObject(i).toString());
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
                        historyadapter = new HelpHistoryListAdapter(mainActivity, helpHistoryList);
                        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(historyadapter);
                        SlideInRightAnimationAdapter scaleAdapter = new SlideInRightAnimationAdapter(
                                alphaAdapter);
                        scaleAdapter.setFirstOnly(false);
                        scaleAdapter.setInterpolator(new OvershootInterpolator());
                        helpHistoryListView.setAdapter(scaleAdapter);
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
