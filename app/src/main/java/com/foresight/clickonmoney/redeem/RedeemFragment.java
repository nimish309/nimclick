package com.foresight.clickonmoney.redeem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
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
 * Created by asd on 06-08-2015.
 */

public class RedeemFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Redeem";
    static final int PICK_CONTACT = 1;
    private MainActivity mainActivity;
    private LinearLayout layoutRecharge, layoutHistory;
    private TextView txtPrepaid, txtPost, txtHistory;
    private Spinner spinnerOperator;
    private RadioGroup radioType;
    private RadioButton radioTopup, radioScheme;
    private EditText etMobileNo, etMobileAmt;
    private Button btnMobileRecharge;
    private RecyclerView historyListView;
    private RecyclerView.LayoutManager myLayoutManager;
    private ProgressBar progressBar;
    private RedeemListAdapter adapter;
    private SlideInRightAnimationAdapter scaleAdapter;
    private ArrayAdapter<String> adapterPre, adapterPost;

    private InterstitialAd interstitial;
    private JSONArray bannerArray, fullscreenArray, videoArray;

    private ArrayList<String> operatorPre = new ArrayList<String>();
    private ArrayList<String> operatorPost = new ArrayList<String>();
    private boolean isPostpaid;
    private ArrayList<String> historyList = new ArrayList<>();
    private String strMobile, strAmt;

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
        View view = localInflater.inflate(R.layout.redeem_fragmet, container, false);

        fullscreenArray = UserDataPreferences.getFullscreenIds(mainActivity);
        if (fullscreenArray != null) {
            showFullScreen();
        }

        txtPrepaid = (TextView) view.findViewById(R.id.txtPrepaid);
        txtPost = (TextView) view.findViewById(R.id.txtPost);
        txtHistory = (TextView) view.findViewById(R.id.txtHistory);
        layoutRecharge = (LinearLayout) view.findViewById(R.id.layoutRecharge);
        layoutHistory = (LinearLayout) view.findViewById(R.id.layoutHistory);

        txtPrepaid.setOnClickListener(this);
        txtPost.setOnClickListener(this);
        txtHistory.setOnClickListener(this);

        spinnerOperator = (Spinner) view.findViewById(R.id.spinnerOperator);
        radioType = (RadioGroup) view.findViewById(R.id.radioType);
        radioTopup = (RadioButton) view.findViewById(R.id.radioTopup);
        radioScheme = (RadioButton) view.findViewById(R.id.radioScheme);
        etMobileNo = (EditText) view.findViewById(R.id.etMobileNo);
        etMobileAmt = (EditText) view.findViewById(R.id.etMobileAmt);
        btnMobileRecharge = (Button) view.findViewById(R.id.btnMobileRecharge);
        btnMobileRecharge.setOnClickListener(this);

        historyListView = (RecyclerView) view.findViewById(R.id.historyListView);
        historyListView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(mainActivity);
        historyListView.setLayoutManager(myLayoutManager);

//        adapter = new RedeemListAdapter(mainActivity, historyList);
//        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(
//                new RedeemListAdapter(mainActivity, historyList));
        scaleAdapter = new SlideInRightAnimationAdapter(
                new AlphaInAnimationAdapter(
                        new RedeemListAdapter(mainActivity, historyList)));
        scaleAdapter.setFirstOnly(false);
        scaleAdapter.setInterpolator(new OvershootInterpolator());
        historyListView.setAdapter(scaleAdapter);

        layoutHistory.setVisibility(View.GONE);
        layoutRecharge.setVisibility(View.VISIBLE);

        operatorPre.add("SELECT OPERATOR");
        operatorPre.add("VODAFONE");
        operatorPre.add("IDEA");
        operatorPre.add("AIRTEL");
        operatorPre.add("AIRCEL");
        operatorPre.add("BSNL");
        operatorPre.add("LOOP");
        operatorPre.add("MTS");
        operatorPre.add("TATAINDICOM");
        operatorPre.add("TATADOCOMO");
        operatorPre.add("UNINOR");
        operatorPre.add("VIDEOCON");
        operatorPre.add("VIRGINGSM");
        operatorPre.add("VIRGINCDMA");
        operatorPre.add("RELIANCECDMA");
        operatorPre.add("RELIANCEGSM");

        operatorPost.add("SELECT OPERATOR");
        operatorPost.add("AIRTELPOST");
        operatorPost.add("IDEAPOST");
        operatorPost.add("RELIANCECDMAPOST");
        operatorPost.add("RELIANCEGSMPOST");
        operatorPost.add("TATADOCOMOPOST");
        operatorPost.add("TATAPOST");
        operatorPost.add("VODAFONEPOST");

        adapterPre = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, operatorPre);
        adapterPost = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, operatorPost);

        spinnerOperator.setAdapter(adapterPre);
        setBackgroundColor(txtPrepaid);
        etMobileNo.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        if (event.getRawX() >= (etMobileNo.getRight() - etMobileNo
                                .getCompoundDrawables()[DRAWABLE_RIGHT]
                                .getBounds().width())) {
                            Intent i = new Intent(Intent.ACTION_PICK,
                                    ContactsContract.Contacts.CONTENT_URI);
                            startActivityForResult(i, PICK_CONTACT);
                            return true;
                        }
                    } catch (NullPointerException e) {
                        Log.e("On Touch", e.toString());
                    }
                }
                return false;
            }
        });

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
            case R.id.txtPrepaid:
                isPostpaid = false;
                layoutHistory.setVisibility(View.GONE);
                radioType.setVisibility(View.VISIBLE);
                layoutRecharge.setVisibility(View.VISIBLE);
                setBackgroundColor(txtPrepaid);
                spinnerOperator.setAdapter(adapterPre);
                break;
            case R.id.txtPost:
                isPostpaid = true;
                layoutHistory.setVisibility(View.GONE);
                radioType.setVisibility(View.GONE);
                layoutRecharge.setVisibility(View.VISIBLE);
                setBackgroundColor(txtPost);
                spinnerOperator.setAdapter(adapterPost);
                break;
            case R.id.txtHistory:
                isPostpaid = false;
                setBackgroundColor(txtHistory);
                layoutRecharge.setVisibility(View.GONE);
                layoutHistory.setVisibility(View.VISIBLE);
                if (historyList.size() == 0) {
                    if (AppConstant.isNetworkAvailable(mainActivity)) {
                        new RedeemHistoryListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        AppConstant.showNetworkError(mainActivity);
                    }
                }
                break;
            case R.id.btnMobileRecharge:
                strMobile = etMobileNo.getText().toString().trim();
                strAmt = etMobileAmt.getText().toString().trim();
                if (spinnerOperator.getSelectedItemPosition() == 0) {
                    AppConstant.showToastShort(mainActivity, "Please Select Valid Operator");
                }
                else if (strMobile.equals("")) {
                    AppConstant.showToastShort(mainActivity, "Please enter mobile no");
                } else {
                    if (strMobile.length() != 10) {
                        AppConstant.showToastShort(mainActivity, "Please enter 10 digit mobile no");
                    } else {
                        if (strAmt.equals("")) {
                            AppConstant.showToastShort(mainActivity, "Please enter amount");
                        } else {
                            try {
                                int amt = Integer.parseInt(strAmt);
                                if (amt < 10 || amt > 3999) {
                                    AppConstant.showToastShort(mainActivity, "Please enter amount between 10 to 3999");
                                } else {
                                    new RechargeTask().execute();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
        }
    }

    private void setBackgroundColor(TextView textView) {
        txtPrepaid.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        txtPost.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        txtHistory.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightactionbar));
        textView.setBackgroundColor(mainActivity.getResources().getColor(R.color.actionbar));
    }

    private class RedeemHistoryListTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message;
        private JSONArray data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (historyList.size() == 0) {
                dialog = new ProgressDialog(mainActivity);
                dialog.setMessage("Please wait...");
                dialog.setCancelable(true);
                dialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            JSONStringer jsonStringer;
            try {
                jsonStringer = new JSONStringer().object().key("user_unique_id").value(UserDataPreferences.getUniqueId(mainActivity)).endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_redeem_history, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    message = item.getString("message");
                    if (flag) {
                        data = item.getJSONArray("data");
                        Log.d("Data==>", data + "");
                        historyList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            historyList.add(data.getJSONObject(i).toString());
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
                        scaleAdapter.notifyDataSetChanged();
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

    private class RechargeTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode, selectedItem;
        private boolean flag, isScheme;
        private String message;
        private JSONObject data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            selectedItem = spinnerOperator.getSelectedItemPosition();
            if (radioType.getCheckedRadioButtonId() == R.id.radioScheme) {
                isScheme = true;
            }
            if (historyList.size() == 0) {
                dialog = new ProgressDialog(mainActivity);
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            JSONStringer jsonStringer;
            try {
                jsonStringer = new JSONStringer().object().key("user_unique_id").value(UserDataPreferences.getUniqueId(mainActivity))
                        .key("mobile_number").value(strMobile).key("amount").value(Integer.parseInt(strAmt));
                if (isPostpaid) {
                    jsonStringer.key("operator_name").value(operatorPost.get(selectedItem)).key("type").value("BILL");
                } else {
                    if (isScheme) {
                        jsonStringer.key("operator_name").value(operatorPre.get(selectedItem)).key("type").value("MOBILE").key("recharge_type").value(1);
                    } else {
                        jsonStringer.key("operator_name").value(operatorPre.get(selectedItem)).key("type").value("MOBILE").key("recharge_type").value(0);
                    }
                }
                jsonStringer.endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_recharge, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    message = item.getString("message");
                    if (flag) {
                        data = item.getJSONObject("data");
                        String balance = data.has("balance") ? (data.isNull("balance") ? null : data.getString("balance")) : null;
                        if (balance != null) {
                            UserDataPreferences.saveUserBalance(mainActivity, balance);
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
                    AppConstant.showToastShort(mainActivity, message);
                    if (flag) {
                        spinnerOperator.setSelection(0);
                        etMobileNo.setText("");
                        etMobileAmt.setText("");
                        radioTopup.setChecked(true);
                        mainActivity.txtUserBalance.setText(UserDataPreferences.getUserBalance(mainActivity));
                        new RedeemHistoryListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    AppConstant.unableConnectServer(mainActivity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    String cNumber = null;
                    Uri contactData = data.getData();
                    @SuppressWarnings("deprecation")
                    Cursor c = getActivity().managedQuery(contactData, null, null,
                            null, null);
                    if (c.moveToFirst()) {

                        String id = c
                                .getString(c
                                        .getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c
                                .getString(c
                                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getActivity()
                                    .getContentResolver()
                                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                    + " = " + id, null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones
                                    .getColumnIndex("data1"));
                            System.out.println("number is:" + cNumber);

                            if (cNumber.length() > 10) {
                                String strMobileNo = cNumber.substring(
                                        cNumber.length() - 10, cNumber.length());
                                etMobileNo.setText(strMobileNo);
                            } else {
                                etMobileNo.setText(cNumber);
                            }

                        } else {
                            Toast.makeText(getActivity(),
                                    "Phone number not found.", Toast.LENGTH_LONG)
                                    .show();
                        }

                    }
                }
                break;
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
