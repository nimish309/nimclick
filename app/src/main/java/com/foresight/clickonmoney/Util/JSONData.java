package com.foresight.clickonmoney.Util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lucky on 19/08/15.
 */
public class JSONData {

    public static String getString(JSONObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? (jsonObject.isNull(key) ? "" : jsonObject.getString(key)) : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getStringDefNull(JSONObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? (jsonObject.isNull(key) ? null : jsonObject.getString(key)) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean getBoolean(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? false : item.getBoolean(key))
                    : false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getBooleanDefTrue(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? true : item.getBoolean(key))
                    : true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static int getInt(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? 0 : item.getInt(key))
                    : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getLong(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? 0 : item.getLong(key))
                    : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Double getDouble(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? 0.0 : item.getDouble(key))
                    : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static Double getDoubleDefNull(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? null : item.getDouble(key))
                    : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? (jsonObject.isNull(key) ? new JSONObject() : jsonObject.getJSONObject(key)) : new JSONObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONObject getJSONObjectDefNull(JSONObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? (jsonObject.isNull(key) ? null : jsonObject.getJSONObject(key)) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? (jsonObject.isNull(key) ? new JSONArray() : jsonObject.getJSONArray(key)) : new JSONArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static JSONArray getJSONArrayDefNull(JSONObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? (jsonObject.isNull(key) ? null : jsonObject.getJSONArray(key)) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getObject(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? new Object() : item.get(key))
                    : new Object();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object();
    }

    public static Object getObjectDefNull(JSONObject item, String key) {
        try {
            return item.has(key) ? (item.isNull(key) ? null : item.get(key))
                    : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
