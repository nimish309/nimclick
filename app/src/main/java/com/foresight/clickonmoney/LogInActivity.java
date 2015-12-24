package com.foresight.clickonmoney;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.JSONData;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.Util.UserDataPreferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.InputStream;

public class LogInActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener {

    //Variables
    private String strMobile, strPassword;
    private ProgressDialog progressDialog;
    private EditText etMobile, etPassword;
    private Button btnLogin, btnRegister;
    private LinearLayout layoutEditText, layoutBtn;
    private boolean gnailLogin;

    //For Gmail
    private String personName = "", personPhotoUrl = "", email = "";
    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private SignInButton btnSignInGmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        AppConstant.showSingleButtonAlertDialog(this, "Key Hash", AppConstant.printKeyHash(this));

//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(getResources().getColor(R.color.statusbar));

        setContentView(R.layout.activity_login);

        etMobile = (EditText) findViewById(R.id.etMobile);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnSignInGmail = (SignInButton) findViewById(R.id.btn_sign_in);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        layoutEditText = (LinearLayout) findViewById(R.id.layoutEditText);
        layoutBtn = (LinearLayout) findViewById(R.id.layoutBtn);

        btnSignInGmail.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


    }

    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;
            if (mGoogleApiClient != null) {
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        if (gnailLogin) {
            getProfileInformation();
            gnailLogin = false;
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private void signInWithGplus() {
        if (mGoogleApiClient != null) {

            if (mGoogleApiClient.isConnected()) {
                if (gnailLogin) {
                    getProfileInformation();
                    gnailLogin = false;
                }
            } else {
                if (!mGoogleApiClient.isConnecting()) {
                    mSignInClicked = true;
                    resolveSignInError();
                }
            }
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult != null) {
            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                personName = currentPerson.getDisplayName();
                personPhotoUrl = currentPerson.getImage().getUrl();
                email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e("Information==>", "Name: " + personName + ", email: " + email
                        + ", Image: " + personPhotoUrl);
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
                Log.d("Information==>", personPhotoUrl);
                new LoginTask(3, currentPerson.getId()).execute();

                new LoadProfileImage(null).execute(personPhotoUrl);
//                    imgProfilePic is Object Of imageview

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (layoutEditText.getVisibility() == View.VISIBLE) {
//            layoutEditText.setVisibility(View.GONE);
//            layoutBtn.setVisibility(View.VISIBLE);
//
//        } else {
//            finish();
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                if (AppConstant.isNetworkAvailable(LogInActivity.this)) {
                    gnailLogin = true;
                    signInWithGplus();
                } else {
                    Toast.makeText(LogInActivity.this,
                            "No Internet Connection Found.", Toast.LENGTH_LONG)
                            .show();
                }
                break;

            case R.id.btnLogin:
                if (layoutEditText.getVisibility() == View.GONE) {
                    layoutEditText.setVisibility(View.VISIBLE);
                    layoutBtn.setVisibility(View.GONE);
                    etMobile.requestFocus();
                } else {
                    strMobile = etMobile.getText().toString();
                    strPassword = etPassword.getText().toString();
                    if (strMobile.equals("")) {
                        AppConstant.showToastShort(LogInActivity.this, "Please enter mobile no");
                    } else {
                        if (strMobile.length() == 10) {
                            if (strPassword.equals("")) {
                                AppConstant.showToastShort(LogInActivity.this, "Please enter password");
                            } else {
                                new LoginTask(1, null).execute();
                            }
                        } else {
                            AppConstant.showToastShort(LogInActivity.this, "Please enter valid mobile no");
                        }
                    }
                }
                break;

            case R.id.btnRegister:
                Intent i = new Intent(LogInActivity.this,
                        RegisterActivity.class);
                startActivity(i);
                break;

            default:
                break;
        }
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
        }
    }

    private class LoginTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode, code;
        private String message, data, login_id;
        private int type;
        private JSONArray bannerArray, fullscreenArray, videoArray;
        private JSONObject news;

        LoginTask(int type, String login_id) {
            this.type = type;
            this.login_id = login_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LogInActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            JSONStringer jsonStringer;
            try {
                if (type == 1) {
                    jsonStringer = new JSONStringer().object().key("login_type").value("normal").key("mobile_no").value(strMobile)
                            .key("user_password").value(strPassword);
                } else if (type == 2) {
                    jsonStringer = new JSONStringer().object().key("login_type").value("facebook").key("login_id").value(login_id);
                } else {
                    jsonStringer = new JSONStringer().object().key("login_type").value("gmail").key("login_id").value(login_id);
                }

//                jsonStringer.key("mob_imei").value("1234567891234568").key("version").value(getPackageManager().getPackageInfo(LogInActivity.this.getPackageName(), 0).versionCode);
                jsonStringer.key("mob_imei").value(((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId()).key("version").value(getPackageManager().getPackageInfo(LogInActivity.this.getPackageName(), 0).versionCode);
                AppConstant.setDeviceInfo(jsonStringer, LogInActivity.this);
                jsonStringer.endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_login, jsonStringer.toString());
//                String jData = jsonParser.sendPostReq1(Constants.api + Constants.api_login, jsonStringer.toString());
                Log.d("data==>" + jsonData[0], " " + jsonData[1]);
                responseCode = Integer.parseInt(jsonData[0]);


                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    code = JSONData.getInt(item, "code");
                    message = JSONData.getString(item, "message");
                    if (code == 200) {
                        data = item.getJSONObject("data").toString();
                        bannerArray = item.has("banner") ? (item.isNull("banner") ? null : item.getJSONArray("banner")) : null;
                        fullscreenArray = item.has("fullscreen") ? (item.isNull("fullscreen") ? null : item.getJSONArray("fullscreen")) : null;
                        videoArray = item.has("video") ? (item.isNull("video") ? null : item.getJSONArray("video")) : null;
                        news = item.has("news") ? (item.isNull("news") ? null : item.getJSONObject("news")) : null;
                        UserDataPreferences.saveAdsId(LogInActivity.this, bannerArray, fullscreenArray, videoArray);
                        UserDataPreferences.saveNews(LogInActivity.this, news);
                        UserDataPreferences.saveTodaysBalance(LogInActivity.this, item.has("todays_balance") ? (item.isNull("todays_balance") ? "0" : item.getString("todays_balance")) : "0");
                        UserDataPreferences.saveUserBalance(LogInActivity.this, item.has("balance") ? (item.isNull("balance") ? "0" : item.getString("balance")) : "0");
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
                    if (code == 200) {
                        UserDataPreferences.saveUserData(LogInActivity.this, data);
                        UserDataPreferences.saveLoginStatus(LogInActivity.this);
                        Intent i = new Intent(LogInActivity.this,
                                MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        if (code == 1 && type != 1) {
                            Intent i = new Intent(LogInActivity.this,
                                    RegisterActivity.class);
                            i.putExtra("isSocial", true);
                            i.putExtra("name", personName);
                            i.putExtra("profile_pic", personPhotoUrl);
                            i.putExtra("email", email);
                            i.putExtra("login_id", login_id);
                            startActivity(i);
                        } else if (code == 3) {
                            AppConstant.showSingleButtonAlertDialog(LogInActivity.this, "Authentication Error", message);
                        } else {
                            AppConstant.showToastShort(LogInActivity.this, message);
                        }
                    }
                } else {
                    AppConstant.unableConnectServer(LogInActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
