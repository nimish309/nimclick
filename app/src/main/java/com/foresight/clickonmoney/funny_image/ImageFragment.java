package com.foresight.clickonmoney.funny_image;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.JSONData;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.customcontroll.ParallaxPagerTransformer;
import com.foresight.clickonmoney.Util.UserDataPreferences;
import com.foresight.clickonmoney.network.NetworkListAdapter;
import com.foresight.clickonmoney.recyclerviewflexibledivider.AlphaInAnimationAdapter;
import com.foresight.clickonmoney.recyclerviewflexibledivider.SlideInRightAnimationAdapter;
import com.google.android.gms.plus.model.people.Person;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

/**
 * Created by Lucky on 27/08/15.
 */

public class ImageFragment extends Fragment {

    public static final String TAG = "Funny Images";
    private MainActivity mainActivity;

    private ViewPager viewPager;
    DemoParallaxAdapter mAdapter;

    private InterstitialAd interstitial;
    private JSONArray bannerArray, fullscreenArray, videoArray;

    private boolean isOpenAd;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_launcher).cacheInMemory(true)
            .cacheOnDisk(true).considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

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
        View view = localInflater.inflate(R.layout.image_fragment, container, false);

        fullscreenArray = UserDataPreferences.getFullscreenIds(mainActivity);
        if (fullscreenArray != null) {
            showFullScreen();
        }

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        ParallaxPagerTransformer pt = new ParallaxPagerTransformer((R.id.image));
        pt.setBorder(20);
        //pt.setSpeed(0.2f);
        viewPager.setPageTransformer(false, pt);

        mAdapter = new DemoParallaxAdapter(mainActivity.getSupportFragmentManager());
        mAdapter.setPager(viewPager); //only for this transformer

        if (AppConstant.isNetworkAvailable(mainActivity)) {
            new FunnyImageListTask().execute();
        } else {
            AppConstant.showNetworkError(mainActivity);
        }

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
    public class ImageAdapter extends PagerAdapter {
        Context context;
        private ArrayList<String> url;

        ImageAdapter(Context context, ArrayList<String> url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public float getPageWidth(int position) {
            if (url.size() == 1) {
                return 1f;
            } else {
                return 0.95f;
            }

        }

        @Override
        public int getCount() {
            return url.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            final ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ImageLoader.getInstance().displayImage(url.get(position),
                    imageView, options);

            ((ViewPager) container).addView(imageView, position);

            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    Intent viewImage = new Intent(context,
//                            ViewSingleImage.class);
//                    viewImage.putExtra("BitmapImage", url.get(position));
//                    viewImage.putStringArrayListExtra("BitmapImage", url);
//                    viewImage.putExtra("CurrentPosition", position);
//                    context.startActivity(viewImage);
                }
            });

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }


    private class FunnyImageListTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message;
        private JSONArray data;
        private ArrayList<String> imageUrlList = new ArrayList<>();

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
                jsonStringer = new JSONStringer().object().key("image_type").value("Funny").endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_funny_image, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    if (flag) {
                        data = item.getJSONArray("imageArray");
                        for (int i = 0; i < data.length(); i++) {
                            imageUrlList.add(data.getString(i));
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
                        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mainActivity));
                        for (int i = 0; i < imageUrlList.size(); i++) {
                            Bundle b = new Bundle();
                            b.putString("image", imageUrlList.get(i));
                            DemoParallaxFragment demoParallaxFragment = new DemoParallaxFragment();
                            demoParallaxFragment.setArguments(b);
                            mAdapter.add(demoParallaxFragment);
                        }
                        viewPager.setAdapter(mAdapter);
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