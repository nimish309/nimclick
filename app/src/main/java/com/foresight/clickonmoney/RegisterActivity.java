package com.foresight.clickonmoney;

/**
 * Created by asd on 28-07-2015.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.InternalStorageContentProvider;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.Util.UserDataPreferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private LinearLayout layoutVerifyMobile, layoutVerifyCode, layoutPart1;
    private ImageView imgUser;
    private Button btnVerify, btnDone, btnRegister;
    private EditText etReferralCode, etMobileVerify, etVerificationCode;
    private EditText etName, etRefCode, etMobile, etEmail, etPassword, etCnfPassword;
    private String strReferralCode, strMobileVerify, strVerificationCode, strName, strRefCOde, strMobile, strEmail, strPassword, strCnfPassword;

    private TextInputLayout layoutPwd, layoutCnfPwd;

    //For Api variables
    private String otp;
    private boolean isSocial;
    private String personName, personPhotoUrl, email, login_id;
    private BroadcastReceiver smsReceiver;

    //Image Picker
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    private String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.RegisterTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            AppConstant.setToolBarColor(RegisterActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpToolbar();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        Intent i = getIntent();
        isSocial = i.getBooleanExtra("isSocial", false);
        if (isSocial) {
            personName = i.getStringExtra("name");
            personPhotoUrl = i.getStringExtra("profile_pic");
            email = i.getStringExtra("email");
            login_id = i.getStringExtra("login_id");
        }

        initView();
    }

    private void initView() {
        layoutVerifyMobile = (LinearLayout) findViewById(R.id.layoutVerifyMobile);
        layoutVerifyCode = (LinearLayout) findViewById(R.id.layoutVerifyCode);
        layoutPart1 = (LinearLayout) findViewById(R.id.layoutPart1);

        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        imgUser = (ImageView) findViewById(R.id.imgUser);
        etReferralCode = (EditText) findViewById(R.id.etReferralCode);
        etMobileVerify = (EditText) findViewById(R.id.etMobileVerify);
        etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
        etName = (EditText) findViewById(R.id.etName);
        etRefCode = (EditText) findViewById(R.id.etRefCode);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etCnfPassword = (EditText) findViewById(R.id.etCnfPassword);

        layoutPwd = (TextInputLayout) findViewById(R.id.layoutPwd);
        layoutCnfPwd = (TextInputLayout) findViewById(R.id.layoutCnfPwd);

        if (isSocial) {
            layoutPwd.setVisibility(View.INVISIBLE);
            layoutCnfPwd.setVisibility(View.INVISIBLE);
            etName.setText(personName);
            etEmail.setText(email);
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration
                    .createDefault(this));
            imageLoader.displayImage(personPhotoUrl,
                    imgUser, options);
        }

        btnVerify.setOnClickListener(this);
        btnDone.setOnClickListener(this);

        btnRegister.setOnClickListener(this);
        imgUser.setOnClickListener(this);
    }

    private void initPartOne() {
        strName = etName.getText().toString().trim();
        strRefCOde = etRefCode.getText().toString();
        strMobile = etMobile.getText().toString();
        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString();
        strCnfPassword = etCnfPassword.getText().toString();
    }


    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            setActionbarTitle("Registration");
        }
    }

    private void setActionbarTitle(String string) {
        getSupportActionBar().setTitle(AppConstant.spanFont(string, this));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean goNext() {
        initPartOne();
        if (strName.equals("")) {
            AppConstant.showToastShort(this, "Please enter name");
        } else {
            if (isSocial) {
                return true;
            } else {
                if (strEmail.equals("") || strEmail.matches(EMAIL_REGEX)) {
                    if (strPassword.equals("")) {
                        AppConstant.showToastShort(this, "Please enter password");
                    } else {
                        if (strPassword.length() < 6) {
                            AppConstant.showToastShort(this, "Password must be 6 to 16 characters");
                        } else {
                            if (strPassword.equals(strCnfPassword)) {
                                return true;
                            } else {
                                AppConstant.showToastShort(this, "Confirm Password not match.");
                            }
                        }
                    }
                } else {
                    AppConstant.showToastShort(RegisterActivity.this, "Please enter valid Email Address");
                }
            }
        }
        return false;
    }

    private void verifyCode() {
        strVerificationCode = etVerificationCode.getText().toString();
        if (strVerificationCode.length() == 0) {
            AppConstant.showToastShort(RegisterActivity.this, "Please enter verification code.");
        } else {
            if (otp != null) {
                if (strVerificationCode.equals(otp)) {
                    layoutVerifyCode.setVisibility(View.GONE);
                    Animation animation_in = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
                    layoutPart1.startAnimation(animation_in);
                    Animation animation_out = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
                    layoutVerifyCode.startAnimation(animation_out);
                    etMobile.setText(strMobileVerify);
                    etRefCode.setText(strReferralCode);
                } else {
                    AppConstant.showToastShort(RegisterActivity.this, "Invalid code");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnVerify:
                strReferralCode = etReferralCode.getText().toString();
                strMobileVerify = etMobileVerify.getText().toString();
                if (strReferralCode.equals("")) {
                    AppConstant.showToastShort(this, "Please enter referral code");
                } else {
                    if (strMobileVerify.equals("")) {
                        AppConstant.showToastShort(this, "Please enter mobile no");
                    } else {
                        if (strMobileVerify.length() != 10) {
                            AppConstant.showToastShort(this, "Please enter valid mobile no.");
                        } else {
                            new GenerateOTPTask().execute();
                        }
                    }
                }
                break;

            case R.id.btnDone:
                verifyCode();
                break;


            case R.id.btnRegister:
                if (goNext()) {
                    new RegistrationTask().execute();
                }
                break;

            case R.id.imgUser:
                Crop.pickImage(RegisterActivity.this);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            imgUser.setImageDrawable(null);
            imgUser.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private class GenerateOTPTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(RegisterActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            JSONStringer jsonStringer;
            try {

                jsonStringer = new JSONStringer().object().key("mobile_no").value(strMobileVerify).key("refrell_code").value(strReferralCode).endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.generate_otp, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    message = item.getString("message");
                    if (flag) {
                        otp = item.getJSONObject("data").has("otp") ? (item.getJSONObject("data").isNull("otp") ? null : item.getJSONObject("data").getString("otp")) : null;
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
                    if (flag && otp != null) {
                        layoutVerifyMobile.setVisibility(View.GONE);
                        Animation animation_in = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.slide_in_left);
                        layoutVerifyCode.startAnimation(animation_in);
                        Animation animation_out = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.slide_out_left);
                        layoutVerifyMobile.startAnimation(animation_out);
                    }
                    AppConstant.showToastShort(RegisterActivity.this, message);
                } else {
                    AppConstant.unableConnectServer(RegisterActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private class RegistrationTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message, data;
        private Bitmap bmp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(RegisterActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();

            bmp = convertToBitmap(imgUser.getDrawable(), AppConstant.dpToPx(96, RegisterActivity.this));
            Log.d("BMP==>", "" + bmp);
        }

        public Bitmap convertToBitmap(Drawable drawable, int widthPixels) {
            Bitmap mutableBitmap = null;
            if (drawable != null) {
                mutableBitmap = Bitmap.createBitmap(widthPixels, widthPixels, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(mutableBitmap);
                drawable.setBounds(0, 0, widthPixels, widthPixels);
                drawable.draw(canvas);
            }
            return mutableBitmap;
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            JSONStringer jsonStringer;
            try {

                jsonStringer = new JSONStringer().object().key("user_name").value(strName)
                        .key("mob_imei").value(((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId())
                        .key("refrell_code").value(strRefCOde)
                        .key("mobile_no").value(strMobile)
                        .key("email_id").value(strEmail);

                if (isSocial) {
                    jsonStringer.key("login_id").value(login_id)
                            .key("login_type").value("gmail");
                } else {
                    jsonStringer.key("user_password").value(strPassword)
                            .key("login_type").value("normal");
                }

//                if (bmp != null) {
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
//                    jsonStringer.key("user_portrait").value(byteArray);
//                }
                AppConstant.setDeviceInfo(jsonStringer, RegisterActivity.this);
                jsonStringer.endObject();

                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_register, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    message = item.getString("message");
                    if (flag) {
                        data = item.getJSONObject("data").toString();
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
                        UserDataPreferences.saveUserData(RegisterActivity.this, data);
                        UserDataPreferences.saveLoginStatus(RegisterActivity.this);
                        Intent i = new Intent(RegisterActivity.this,
                                MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        AppConstant.showToastShort(RegisterActivity.this, message);
                    }
                } else {
                    AppConstant.unableConnectServer(RegisterActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("SMSRECEIVED");
        intentFilter.setPriority(1000);
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String code = intent.getStringExtra("code");
                Log.i("InchooTutorial", code);
                try {
                    etVerificationCode.setText(code);
                    verifyCode();
                } catch (Exception e) {
                    Log.e("Edittext", e.toString());
                }
            }
        };

        this.registerReceiver(smsReceiver, intentFilter);
    }

    public void onPause() {
        super.onPause();
        this.unregisterReceiver(this.smsReceiver);
    }
}
