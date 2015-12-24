package com.foresight.clickonmoney.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.JSONData;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.Util.UserDataPreferences;

import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * Created by Lucky on 21/08/15.
 */
public class ProfileFragment extends Fragment {

    public static final String TAG = "Profile";
    private MainActivity mainActivity;

    private ImageView imgUser;
    private Button btnRegister;
    private EditText etName, etMobile, etEmail;
    private String strName, strEmail;

    private String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

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
        View view = localInflater.inflate(R.layout.profile_fragment, container, false);

        imgUser = (ImageView) view.findViewById(R.id.imgUser);
        etName = (EditText) view.findViewById(R.id.etName);
        etMobile = (EditText) view.findViewById(R.id.etMobile);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        btnRegister = (Button) view.findViewById(R.id.btnRegister);

        etName.setText(UserDataPreferences.getUserName(mainActivity));
        etMobile.setText(UserDataPreferences.getMobileNo(mainActivity));
        etEmail.setText(UserDataPreferences.getEmailId(mainActivity));

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstant.isNetworkAvailable(mainActivity)) {
                    strName = etName.getText().toString().trim();
                    strEmail = etEmail.getText().toString().trim();
                    if (strName.equals("")) {
                        AppConstant.showToastShort(mainActivity, "Please enter name");
                    } else {
                        if (strEmail.equals("") || strEmail.matches(EMAIL_REGEX)) {
                            new UpdateProfileTask().execute();
                        } else {
                            AppConstant.showToastShort(mainActivity, "Please enter valid Email Address");
                        }
                    }
                } else {
                    AppConstant.showNetworkError(mainActivity);
                }
            }
        });

        return view;
    }


    private class UpdateProfileTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message, data;
        private Bitmap bmp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mainActivity);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();

//            bmp = convertToBitmap(imgUser.getDrawable(),AppConstant.dpToPx(96, mainActivity));
//            Log.d("BMP==>", "" + bmp);
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

                jsonStringer = new JSONStringer().object().key("unique_id").value(UserDataPreferences.getUniqueId(mainActivity)).key("user_name").value(strName)
                        .key("email_id").value(strEmail);

//                if (bmp != null) {
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
//                    jsonStringer.key("user_portrait").value(byteArray);
//                }

                jsonStringer.endObject();

                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_edit_profile, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = JSONData.getBoolean(item, "flag");
                    message = JSONData.getString(item, "message").trim();
                    if (flag) {
                        data = JSONData.getJSONObject(item, "data").toString();
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
                        UserDataPreferences.saveUserData(mainActivity, data);
                        mainActivity.refreshUserDetails();
                    }
                    if (!message.equals("")) {
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


//    private class ImageUploadTask extends AsyncTask<Void, Void, String> {
//
//        private ProgressDialog dialog;
//        private InputStream is;
//        String sResponse;
//
//
//        @Override
//        protected void onPreExecute() {
//            // TODO Auto-generated method stub
//            super.onPreExecute();
//            dialog = new ProgressDialog(mainActivity);
//            dialog.setMessage("Please wait...");
//            dialog.setIndeterminate(true);
//            dialog.setCancelable(false);
//            dialog.show();
//        }
//
//        @Override
//        protected String doInBackground(Void... unsued) {
//            try {
//                HttpClient httpClient = new DefaultHttpClient();
//                HttpContext localContext = new BasicHttpContext();
//                HttpPost httpPost = new HttpPost(Constants.api
//                        + Constants.api_register);
//
//                // httpPost.setHeader("Content-Type","undefined");
//
//                MultipartEntity entity = new MultipartEntity(
//                        HttpMultipartMode.BROWSER_COMPATIBLE);
//
//                entity.addPart("name", new StringBody(etFName.getText()
//                        .toString()));
//                entity.addPart("email ", new StringBody(etEmail.getText()
//                        .toString()));
//
//                if (isProfileSelected) {
//                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                    byte[] data = bos.toByteArray();
//                    entity.addPart("image", new ByteArrayBody(data,
//                            "myImage.jpg"));
//
//                }else{
//                    entity.addPart("image", new StringBody("null"));
//                }
//
//                httpPost.setEntity(entity);
//                HttpResponse response = httpClient.execute(httpPost,
//                        localContext);
//
//
//                try {
//                    is = response.getEntity().getContent();
//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(is, "iso-8859-1"), 8);
//                    StringBuilder sb = new StringBuilder();
//                    String line = null;
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line + "\n");
//                    }
//                    is.close();
//                    sResponse = sb.toString();
//
//                } catch (Exception e) {
//                   e.printStackTrace();
//                }
//
//                return sResponse;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//
//        }
//
//        @Override
//        protected void onPostExecute(String sResponse) {
//            if (dialog.isShowing())
//                dialog.dismiss();
//            try {
//                if (sResponse != null) {
////                    Log.d("Json", sResponse);
////                    JSONObject jObj = new JSONObject(sResponse);
////
////                    boolean flag = false;
////                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
////                    String message = jObj.has("message") ? jObj
////                            .getString("message") : "";
////                    if (flag) {
////
////                        Toast.makeText(mainActivity,
////                                "Successfully Registered", Toast.LENGTH_LONG)
////                                .show();
////                        JSONObject dataObj = jObj.getJSONObject("data");
////
////                        String image = dataObj.has("image") ? dataObj
////                                .getString("image") : "";
////
////
////                    } else {
////
////
////                    }
//
//                }
//
//            } catch (Exception e) {
//               e.printStackTrace();
//            }
//        }
//    }


}