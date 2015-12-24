package com.foresight.clickonmoney.bank_withdraw;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.Util.UserDataPreferences;
import com.foresight.clickonmoney.earned_history.EarnedListAdapter;
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
 * Created by Lucky on 12/08/15.
 */
public class WithdrawFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Bank Withdraw";
    private MainActivity mainActivity;

    private TextView txtRequest, txtPending, txtTransaction;
    private LinearLayout layoutRequest, layoutPending, layoutTransaction;

    //For Request
    private EditText etBankName, etAccountNo, etIsfcCode, etAmount, etVerificationCode, etPassword;
    private Button btnCancel, btnRedeem, btnVerify;

    //For Pending
    private RecyclerView pendingListView;
    private RecyclerView.LayoutManager myLayoutManager;
    private ProgressBar progressBar;
    private PendingListAdapter pendingListAdapter;

    private InterstitialAd interstitial;
    private JSONArray bannerArray, fullscreenArray, videoArray;

    //For Transaction
    private RecyclerView transactionListView;
    private RecyclerView.LayoutManager myLayoutManager1;
    private ProgressBar progressBar1;
    private TransactionListAdapter transactionListAdapter;

    private String strBankName, strAccountNo, strIsfcCode, strAmount, strVerificationId, strVerificationCode, strPassword;
    private double amount;

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
        View view = localInflater.inflate(R.layout.bank_withdraw_fragment, container, false);

        fullscreenArray = UserDataPreferences.getFullscreenIds(mainActivity);
        if (fullscreenArray != null) {
            showFullScreen();
        }

        txtRequest = (TextView) view.findViewById(R.id.txtRequest);
        txtPending = (TextView) view.findViewById(R.id.txtPending);
        txtTransaction = (TextView) view.findViewById(R.id.txtTransaction);

        layoutRequest = (LinearLayout) view.findViewById(R.id.layoutRequest);
        layoutPending = (LinearLayout) view.findViewById(R.id.layoutPending);
        layoutTransaction = (LinearLayout) view.findViewById(R.id.layoutTransaction);

        txtRequest.setOnClickListener(this);
        txtPending.setOnClickListener(this);
        txtTransaction.setOnClickListener(this);
        setBackgroundColor(txtRequest, layoutRequest);

        //For Request
        etBankName = (EditText) view.findViewById(R.id.etBankName);
        etAccountNo = (EditText) view.findViewById(R.id.etAccountNo);
        etIsfcCode = (EditText) view.findViewById(R.id.etIsfcCode);
        etAmount = (EditText) view.findViewById(R.id.etAmount);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnRedeem = (Button) view.findViewById(R.id.btnRedeem);

        btnRedeem.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        // For Pending
        pendingListView = (RecyclerView) view.findViewById(R.id.pendingListView);
        pendingListView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(mainActivity);
        pendingListView.setLayoutManager(myLayoutManager);

        // For Transaction
        transactionListView = (RecyclerView) view.findViewById(R.id.transactionListView);
        transactionListView.setHasFixedSize(true);
        myLayoutManager1 = new LinearLayoutManager(mainActivity);
        transactionListView.setLayoutManager(myLayoutManager1);

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
    private void setBackgroundColor(TextView textView, LinearLayout linearLayout) {
        txtRequest.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        txtPending.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        txtTransaction.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        textView.setBackgroundColor(mainActivity.getResources().getColor(R.color.actionbar));
        layoutRequest.setVisibility(View.GONE);
        layoutPending.setVisibility(View.GONE);
        layoutTransaction.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    private boolean getDataFromUI() {
        try {
            strBankName = etBankName.getText().toString().trim();
            strAccountNo = etAccountNo.getText().toString().trim();
            strIsfcCode = etIsfcCode.getText().toString().trim();
            strAmount = etAmount.getText().toString().trim();

            if (strBankName.equals("")) {
                AppConstant.showToastShort(mainActivity, "Please enter Bank name");
                return false;
            } else {
                if (strAccountNo.equals("")) {
                    AppConstant.showToastShort(mainActivity, "Please enter Account no");
                    return false;
                } else {
                    if (strIsfcCode.equals("")) {
                        AppConstant.showToastShort(mainActivity, "Please enter ISFC Code");
                        return false;
                    } else {
                        if (strAmount.equals("")) {
                            AppConstant.showToastShort(mainActivity, "Please enter amount");
                            return false;
                        } else {
                            amount = Double.valueOf(strAmount);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRedeem:
                if (getDataFromUI()) {
                    if (AppConstant.isNetworkAvailable(mainActivity)) {
                        new SendWithdrawRequest().execute();
                    } else {
                        AppConstant.showNetworkError(mainActivity);
                    }
                }
                break;

            case R.id.txtRequest:
                setBackgroundColor(txtRequest, layoutRequest);
                break;
            case R.id.txtPending:
                setBackgroundColor(txtPending, layoutPending);
                if (pendingListAdapter == null) {
                    if (AppConstant.isNetworkAvailable(mainActivity)) {
                        new PendingListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }else {
                        AppConstant.showNetworkError(mainActivity);
                    }
                }
                break;
            case R.id.txtTransaction:
                setBackgroundColor(txtTransaction, layoutTransaction);
                if (transactionListAdapter == null) {
                    if (AppConstant.isNetworkAvailable(mainActivity)) {
                        new TransactionListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }else {
                        AppConstant.showNetworkError(mainActivity);
                    }
                }
                break;
            case R.id.btnCancel:
                break;
        }
    }

    private class SendWithdrawRequest extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                        .key("mob_imei").value(((TelephonyManager) mainActivity.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId())
                        .key("bank").value(strBankName)
                        .key("account").value(strAccountNo)
                        .key("ifsc_code").value(strIsfcCode)
                        .key("amount").value(amount)
                        .endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_bank_withdrawal, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    message = item.getString("message");
                    if (flag) {
                        strVerificationId = item.getString("data");
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
                        openVerificationPopup();
                    }
                    AppConstant.showToastShort(mainActivity, message);

                } else {
                    AppConstant.unableConnectServer(mainActivity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void openVerificationPopup() {
        final Dialog dialog = new Dialog(mainActivity);

        dialog.getWindow().setBackgroundDrawableResource(
                R.color.activityBackground);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.verify_withdraw_popup);
        dialog.show();

        etVerificationCode = (EditText) dialog.findViewById(R.id.etVerificationCode);
        etPassword = (EditText) dialog.findViewById(R.id.etPassword);
        btnVerify = (Button) dialog.findViewById(R.id.btnVerify);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strVerificationCode = etVerificationCode.getText().toString();
                strPassword = etPassword.getText().toString();
                if (strVerificationCode.equals("")) {
                    AppConstant.showToastShort(mainActivity, "Please enter verification code");
                } else {
                    if (strPassword.equals("")) {
                        AppConstant.showToastShort(mainActivity, "Please enter password");
                    } else {
                        if (AppConstant.isNetworkAvailable(mainActivity)) {
                            new SendWithdrawVerificationRequest(dialog).execute();
                        } else {
                            AppConstant.showNetworkError(mainActivity);
                        }
                    }
                }
            }
        });

    }

    private class SendWithdrawVerificationRequest extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message;
        private Dialog dialog1;

        public SendWithdrawVerificationRequest(Dialog dialog) {
            this.dialog1 = dialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                        .key("Id").value(strVerificationId).key("otp").value(strVerificationCode)
                        .key("user_password").value(strPassword)
                        .endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_bank_withdrawal_verification, jsonStringer.toString());
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
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                if (responseCode == 200) {
                    if (flag) {
                        dialog1.dismiss();
                    }
                    AppConstant.showToastShort(mainActivity, message);

                } else {
                    AppConstant.unableConnectServer(mainActivity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class PendingListTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message;
        private JSONArray data;
        private ArrayList<String> pendingList = new ArrayList<>();

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
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_bank_pending_history, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    message = item.getString("message");
                    if (flag) {
                        data = item.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            pendingList.add(data.getJSONObject(i).toString());
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
                        pendingListAdapter = new PendingListAdapter(mainActivity, pendingList);
                        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(
                                pendingListAdapter);
                        SlideInRightAnimationAdapter scaleAdapter = new SlideInRightAnimationAdapter(
                                alphaAdapter);
                        scaleAdapter.setFirstOnly(false);
                        scaleAdapter.setInterpolator(new OvershootInterpolator());
                        pendingListView.setAdapter(scaleAdapter);
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

    private class TransactionListTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message;
        private JSONArray data;
        private ArrayList<String> transactionList = new ArrayList<>();

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
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_bank_transaction_history, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    message = item.getString("message");
                    if (flag) {
                        data = item.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            transactionList.add(data.getJSONObject(i).toString());
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
                        transactionListAdapter = new TransactionListAdapter(mainActivity, transactionList);
                        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(
                                transactionListAdapter);
                        SlideInRightAnimationAdapter scaleAdapter = new SlideInRightAnimationAdapter(
                                alphaAdapter);
                        scaleAdapter.setFirstOnly(false);
                        scaleAdapter.setInterpolator(new OvershootInterpolator());
                        transactionListView.setAdapter(scaleAdapter);
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