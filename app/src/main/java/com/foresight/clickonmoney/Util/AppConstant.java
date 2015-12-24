package com.foresight.clickonmoney.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AppConstant {
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showToastLong(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    public static void showToastShort(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void closeAppPopup(final Context context) {

        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.app_name))
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(true).show();

    }

    public static void showNetworkError(final Context context) {

        new AlertDialog.Builder(context)
                .setTitle("Network Error")
                .setMessage("Internet connection not found,\nPlease try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true).show();

    }

    public static void unableConnectServer(final Context context) {

        new AlertDialog.Builder(context)
                .setTitle("Network Error")
                .setMessage("Unable to connect server,\nPlease try again.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true).show();
    }

    public static void showSingleButtonAlertDialog(final Context context, String title, String message) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true).show();
    }

    public static void setToolBarColor(Activity activity)
            throws NoSuchMethodException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.statusbar));
        }
    }

    public static void setToolBarColorStandAloneActivity(Activity activity)
            throws NoSuchMethodException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(
                    R.color.statusbar));

        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int dpToPx(int dp, Context context) {
        return Math
                .round(dp
                        * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static SpannableString spanFont(String string, Context context) {
        SpannableString s;
        s = new SpannableString(string);
        s.setSpan(new TypefaceSpan(context, "fonts/bold.otf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public static void singleButtonAlertDialog(Context context,
                                               String strTitle, String strMsg) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle(strTitle)
                    .setMessage(strMsg)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setCancelable(true).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static final String PROFILE_PIC_FILE_NAME = "profilePic.jpg";

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    public static Bitmap getBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedString, 0,
                decodedString.length);
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }


    public static String longToDate(String timeStamp) {
        try {
            if (!timeStamp.equals("")) {
                Date date = new Date(Long.parseLong(timeStamp.substring(6,
                        timeStamp.length() - 2))
                        + TimeZone.getDefault().getRawOffset());
                return sdf.format(date);

            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static final int getScreenWidth(Context context,
                                           WindowManager wManager) {
        int Measuredwidth = 0;
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wManager.getDefaultDisplay().getSize(size);
            Measuredwidth = size.x;
        } else {
            Display d = wManager.getDefaultDisplay();
            Measuredwidth = d.getWidth();
        }
        return Measuredwidth;

    }

    public static final int getScreenHeight(Context context,
                                            WindowManager wManager) {

        int Measuredheight = 0;
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wManager.getDefaultDisplay().getSize(size);
            Measuredheight = size.y;
        } else {
            Display d = wManager.getDefaultDisplay();
            Measuredheight = d.getHeight();
        }
        return Measuredheight;

    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public static void setDeviceInfo(JSONStringer jsonStringer, Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        if (macAddress == null) {
            macAddress = "000000000000";
        } else {
            macAddress = macAddress.replace(":","");
        }
        Log.d("Mac Address==>", "==>" + macAddress);
        try {
            jsonStringer.key("ma").value(macAddress).key("ua").value(System.getProperty("http.agent"));
            jsonStringer.key("DEVICE").value(android.os.Build.DEVICE);
            jsonStringer.key("MODEL").value(android.os.Build.MODEL);
            jsonStringer.key("PRODUCT").value(android.os.Build.PRODUCT);
            jsonStringer.key("BRAND").value(android.os.Build.BRAND);
            jsonStringer.key("DISPLAY").value(android.os.Build.DISPLAY);
            jsonStringer.key("UNKNOWN").value(android.os.Build.UNKNOWN);
            jsonStringer.key("HARDWARE").value(android.os.Build.HARDWARE);
            jsonStringer.key("ID").value(android.os.Build.ID);
            jsonStringer.key("MANUFACTURER").value(android.os.Build.MANUFACTURER);
            jsonStringer.key("SERIAL").value(android.os.Build.SERIAL);
            jsonStringer.key("USER").value(android.os.Build.USER);
            jsonStringer.key("HOST").value(android.os.Build.HOST);
            jsonStringer.key("CPU_ABI").value(android.os.Build.CPU_ABI);
            jsonStringer.key("CPU_ABI2").value(android.os.Build.CPU_ABI2);

            Log.d("jsonStringer==>", "==>" + jsonStringer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
