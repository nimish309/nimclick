package com.foresight.clickonmoney.Util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserDataPreferences {

    private static String AUTHENTICATION_PREF = "AuthPref";

    public static void saveLoginStatus(Context context) {
        context.getSharedPreferences(
                AUTHENTICATION_PREF, Context.MODE_PRIVATE).edit().putBoolean("loginStatus", true).commit();
    }

    public static boolean isLogin(Context context) {
        return context.getSharedPreferences(
                AUTHENTICATION_PREF, Context.MODE_PRIVATE).getBoolean("loginStatus", false);
    }

    public static void logout(Context context) {
        context.getSharedPreferences(
                AUTHENTICATION_PREF, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public static void saveUserData(Context context, String userData) {
        context.getSharedPreferences(
                AUTHENTICATION_PREF, Context.MODE_PRIVATE).edit().putString("userData", userData).commit();
    }

    public static String getUserName(Context context) {
        try {
            JSONObject item = new JSONObject(context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("userData", ""));
            return JSONData.getString(item, "user_name");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getEmailId(Context context) {
        try {
            JSONObject item = new JSONObject(context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("userData", ""));
            return JSONData.getString(item, "email_id");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMobileNo(Context context) {
        try {
            JSONObject item = new JSONObject(context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("userData", ""));
            return JSONData.getString(item, "mobile_no");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String getUniqueId(Context context) {
        try {
            JSONObject item = new JSONObject(context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("userData", ""));
            return item.getString("unique_id");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getShareMobileNo(Context context) {
        try {
            JSONObject item = new JSONObject(context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("userData", ""));
            return "(" + item.getString("mobile_no") + ")";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void saveUserBalance(Context context, String userBalance) {
        context.getSharedPreferences(
                AUTHENTICATION_PREF, Context.MODE_PRIVATE).edit().putString("userBalance", userBalance).commit();
    }

    public static String getUserBalance(Context context) {
        return Constants.rupee_symbol + context.getSharedPreferences(
                AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("userBalance", "0");
    }

    public static void saveTodaysBalance(Context context, String userBalance) {
        context.getSharedPreferences(
                AUTHENTICATION_PREF, Context.MODE_PRIVATE).edit().putString("userTodaysBalance", userBalance).commit();
    }

    public static String getTodaysBalance(Context context) {
        return Constants.rupee_symbol + context.getSharedPreferences(
                AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("userTodaysBalance", "0");
    }

    public static void saveAdsId(Context context, JSONArray bannerIds, JSONArray fullscreenIds, JSONArray videoIds) {
        if (bannerIds != null) {
            context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).edit().putString("bannerIds", bannerIds.toString()).commit();
        }
        if (fullscreenIds != null) {
            context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).edit().putString("fullscreenIds", fullscreenIds.toString()).commit();
        }
        if (videoIds != null) {
            context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).edit().putString("videoIds", videoIds.toString()).commit();
        }
    }

    public static JSONArray getBannerIds(Context context) {
        try {
            return new JSONArray(context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("bannerIds", null));
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONArray getFullscreenIds(Context context) {
        try {
            return new JSONArray(context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("fullscreenIds", null));
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONArray getVideoIds(Context context) {
        try {
            return new JSONArray(context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("videoIds", null));
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveNews(Context context, JSONObject news) {
        if (news != null) {
            context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).edit().putString("news", news.toString()).commit();
        }
    }

    public static JSONObject getNews(Context context) {
        try {
            return new JSONObject(context.getSharedPreferences(
                    AUTHENTICATION_PREF, Context.MODE_PRIVATE).getString("news", null));
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRefMedium(Context context) {
        return context.getSharedPreferences(
                "refCodeInfo", Context.MODE_PRIVATE).getString("referrer", null);
    }

    public static void setTimer(Context context, long time) {
        context.getSharedPreferences(
                AUTHENTICATION_PREF, Context.MODE_PRIVATE).edit().putLong("timer", time).commit();
    }

    public static long getTimer(Context context) {
        return context.getSharedPreferences(
                AUTHENTICATION_PREF, Context.MODE_PRIVATE).getLong("timer", 0);
    }
}
