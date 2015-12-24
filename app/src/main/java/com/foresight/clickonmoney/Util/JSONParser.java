package com.foresight.clickonmoney.Util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class JSONParser {
    static final String COOKIES_HEADER = "Set-Cookie";
    static InputStream is = null;
    static JSONObject jObj = null;
    // static String jsonString = "";
    Context context;

    static String json = "";

    public JSONParser(Context context) {
        this.context = context;

    }

    public JSONParser() {
    }

    public String sendReq(String strUrl, int reqType, String jsonData)
            throws IOException {
        String jsonString = "";

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie", TextUtils.join(",", cookieManager
                    .getCookieStore().getCookies()));
        }
        if (reqType == 0) {
            conn.setRequestMethod("GET");
        } else if (reqType == 1) {
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setFixedLengthStreamingMode(jsonData.getBytes().length);

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(jsonData);
            out.close();
        }
        conn.setConnectTimeout(10000);
        conn.connect();

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                cookieManager.getCookieStore().add(null,
                        HttpCookie.parse(cookie).get(0));
            }
        }

        // read the response
        System.out.println("Response Code: " + conn.getResponseCode() + "---"
                + cookieManager.getCookieStore().getCookies());
        try {
            is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }


        return jsonString;
    }

    public String[] sendPostReq(String strurl, String jsonData)
            throws IOException {
        String jsonString = "";

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(strurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie", TextUtils.join(",", cookieManager
                    .getCookieStore().getCookies()));
        }

        conn.setRequestMethod("POST");

        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setFixedLengthStreamingMode(jsonData.getBytes().length);

        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(jsonData);
        out.close();

        conn.setConnectTimeout(10000);
        conn.connect();

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        try {
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    cookieManager.getCookieStore().add(null,
                            HttpCookie.parse(cookie).get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return new String[]{String.valueOf(conn.getResponseCode()), jsonString};
    }

    public String[] sendGetReq(String strUrl) throws IOException {
        String jsonString = "";

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie", TextUtils.join(",", cookieManager
                    .getCookieStore().getCookies()));
        }

        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.connect();

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                cookieManager.getCookieStore().add(null,
                        HttpCookie.parse(cookie).get(0));
            }
        }

        try {
            is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return new String[]{String.valueOf(conn.getResponseCode()), jsonString};
    }

    public String[] sendPostReq(String strurl)
            throws IOException {
        String jsonString = "";

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(strurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie", TextUtils.join(",", cookieManager
                    .getCookieStore().getCookies()));
        }

        conn.setRequestMethod("POST");

        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");


        conn.setConnectTimeout(10000);
        conn.connect();

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        try {
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    cookieManager.getCookieStore().add(null,
                            HttpCookie.parse(cookie).get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return new String[]{String.valueOf(conn.getResponseCode()), jsonString};
    }


    public String sendPostRequest(String strUrl, String jsonData) {
        String response = "";
        try {
            URL url = new URL(strUrl);
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                            + session.getPeerHost());
                    return true;
                }
            };
            // Now you are telling the JRE to trust any https server.
            // If you know the URL that you are connecting to then this should not be a problem
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            httpsURLConnection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
            httpsURLConnection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(httpsURLConnection.getOutputStream());
            dataOutputStream.writeBytes(jsonData);
            dataOutputStream.flush();
            dataOutputStream.close();

            int responseCode = httpsURLConnection.getResponseCode();
            String output = "Request URL " + url;

            BufferedReader br = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseOutput.append(line);
            }
            response = responseOutput.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public String subscribe(String strUrl, String jsonData) throws Exception {

        String resp = "";
        URL url;
        URLConnection urlConn;
        DataOutputStream printout;
        DataInputStream input;
        String str = "";
        int flag = 1;

        try {
            Properties sysProperties = System.getProperties();
            // change proxy settings if required and enable the below lines
            // sysProperties.put("proxyHost", "proxy.starhub.net.sg");
            // sysProperties.put("proxyPort", "8080");
            // sysProperties.put("proxySet",  "true");
// Now you are telling the JRE to ignore the hostname
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                            + session.getPeerHost());
                    return true;
                }
            };
            // Now you are telling the JRE to trust any https server.
            // If you know the URL that you are connecting to then this should not be a problem
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);

            url = new URL(strUrl);
            urlConn = url.openConnection();
            urlConn.setDoInput(true);
            Object object;
            urlConn.setUseCaches(false);


            urlConn.setRequestProperty("Content-Type", "application/json");
            input = new DataInputStream(urlConn.getInputStream());

            while (null != ((str = input.readLine()))) {
                if (str.length() > 0) {
                    str = str.trim();
                    if (!str.equals("")) {
                        //System.out.println(str);
                        resp += str;
                    }
                }
            }
            input.close();
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return resp;
    }

    private static void trustAllHttpsCertificates() throws Exception {

        //  Create a trust manager that does not validate certificate chains:

        javax.net.ssl.TrustManager[] trustAllCerts =

                new javax.net.ssl.TrustManager[1];

        javax.net.ssl.TrustManager tm = new miTM();

        trustAllCerts[0] = tm;

        javax.net.ssl.SSLContext sc =

                javax.net.ssl.SSLContext.getInstance("SSL");

        sc.init(null, trustAllCerts, null);

        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(

                sc.getSocketFactory());

    }

    // Just add these two functions in your program
    public static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
}