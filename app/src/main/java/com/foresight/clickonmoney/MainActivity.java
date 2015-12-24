package com.foresight.clickonmoney;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.Util.UserDataPreferences;
import com.foresight.clickonmoney.bank_withdraw.WithdrawFragment;
import com.foresight.clickonmoney.change_password.ChangePasswordFragment;
import com.foresight.clickonmoney.earned_history.EarnedFragment;
import com.foresight.clickonmoney.funny_image.ImageFragment;
import com.foresight.clickonmoney.help.HelpFragment;
import com.foresight.clickonmoney.help.OnlyAdsFragment;
import com.foresight.clickonmoney.network.NetworkFragment;
import com.foresight.clickonmoney.offer.OffreFragment;
import com.foresight.clickonmoney.profile.ProfileFragment;
import com.foresight.clickonmoney.redeem.RedeemFragment;
import com.foresight.clickonmoney.what_new.WhatNewFragment;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String PREFERENCES_FILE = "appPref";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private DrawerLayout mDrawerLayout;
    private LinearLayout navDrawerHeader;
    public NavigationView mNavigationView;
    private FrameLayout mContentFrame;
    private TextView txtUserName, txtDrawerEmail;
    private ImageView imageViewUser, imgSetting;
    private boolean mUserLearnedDrawer;


    private Toolbar mToolbar;
    private AsyncTask<Void, Void, Void> task;


    private int mCurrentSelectedPosition;
    boolean mFromSavedInstanceState;
    private Fragment currentFragment;

    public TextView txtUserBalance,txtTitle;


    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemShare) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "ClickOnMoney - Amazing Android App. That Helps you to Earn Unlimited Money For Free.\nOn Download You Can Get Rs.5 Balance,So Try It Now,Click Here https://play.google.com/store/apps/details?id=com.foresight.clickonmoney And Don't Foregot To Add My Mobile Number "
                    + UserDataPreferences.getShareMobileNo(MainActivity.this) + " As referral Code");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            AppConstant.setToolBarColor(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpToolbar();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        navDrawerHeader = (LinearLayout) findViewById(R.id.navDrawerHeader);
//        if (Build.VERSION.SDK_INT >= 21) {
//            navDrawerHeader.setPadding(0, AppConstant.dpToPx(24, this), 0, 0);
//        }

        navDrawerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, UpdateProfileActivity.class);
//                MainActivity.this.startActivityForResult(i, Constants.REFRESH_USERIMAGE);
            }
        });

        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(this, PREF_USER_LEARNED_DRAWER, "false"));
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                AppConstant.hideKeyboard(MainActivity.this, drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        setUpNavDrawer();

        imageViewUser = (ImageView) findViewById(R.id.imageViewUser);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtDrawerEmail = (TextView) findViewById(R.id.txtDrawerEmail);

        refreshUserDetails();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mContentFrame = (FrameLayout) findViewById(R.id.nav_contentframe);
        setNavigationViewItemFont();
        if (!UserDataPreferences.isLogin(this)) {
            Menu menu = mNavigationView.getMenu();
            menu.findItem(R.id.itemProfile).setVisible(false);
            menu.findItem(R.id.itemOffer).setVisible(false);
            menu.findItem(R.id.itemNetwork).setVisible(false);
            menu.findItem(R.id.itemLevel).setVisible(false);
            menu.findItem(R.id.itemEarnedHistory).setVisible(false);
            menu.findItem(R.id.itemRedeem).setVisible(false);
            menu.findItem(R.id.itemBankWithdraw).setVisible(false);
            menu.findItem(R.id.itemWhatNew).setVisible(false);
            menu.findItem(R.id.itemHelp).setVisible(false);
            menu.findItem(R.id.itemTerms).setVisible(false);
            menu.findItem(R.id.itemTopOffers).setVisible(false);
            menu.findItem(R.id.itemHotOffers).setVisible(false);
            menu.findItem(R.id.itemRewards).setVisible(false);
            menu.findItem(R.id.itemCredits).setVisible(false);
            menu.findItem(R.id.itemSetting).setVisible(false);
            menu.findItem(R.id.itemFollow).setVisible(false);
            menu.findItem(R.id.itemVideo).setVisible(false);
            menu.findItem(R.id.itemGame).setVisible(false);
            menu.findItem(R.id.itemFunnyImages).setVisible(false);
            menu.findItem(R.id.itemProduct).setVisible(false);
            menu.findItem(R.id.itemShopping).setVisible(false);
            menu.findItem(R.id.itemEncylopedia).setVisible(false);
            menu.findItem(R.id.itemKnowledge).setVisible(false);
            menu.findItem(R.id.itemAboutUs).setVisible(false);
            menu.findItem(R.id.itemChangePassword).setVisible(false);
            menu.findItem(R.id.itemLogout).setVisible(false);

        }
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                txtUserBalance.setText(UserDataPreferences.getUserBalance(MainActivity.this));
                if (task != null) {
                    task.cancel(true);
                    task = new GetUserBalance().execute();
                }
                closeNavigationDrawer();
                switch (menuItem.getItemId()) {
                    case R.id.itemProfile:
                        if (mCurrentSelectedPosition != 0) {
                            menuItem.setChecked(true);
                            currentFragment = new ProfileFragment();
                            makeFragmentVisible(currentFragment, ProfileFragment.TAG);
                            mCurrentSelectedPosition = 0;
                            setActionbarTitle(ProfileFragment.TAG);
                        }
                        return true;
                    case R.id.itemOffer:
                        if (mCurrentSelectedPosition != 1) {
                            menuItem.setChecked(true);
                            currentFragment = new OffreFragment();
                            makeFragmentVisible(currentFragment, OffreFragment.TAG);
                            mCurrentSelectedPosition = 1;
                            setActionbarTitle(OffreFragment.TAG);
                        }
                        return true;
                    case R.id.itemNetwork:
                        if (mCurrentSelectedPosition != 2) {
                            menuItem.setChecked(true);
                            NetworkFragment networkFragment = new NetworkFragment();
                            networkFragment.setUnique_id(UserDataPreferences.getUniqueId(MainActivity.this));
                            currentFragment = networkFragment;
                            makeFragmentVisible(currentFragment, NetworkFragment.TAG);
                            mCurrentSelectedPosition = 2;
                            setActionbarTitle(NetworkFragment.TAG);
                        }
                        return true;
                    case R.id.itemEarnedHistory:
                        if (mCurrentSelectedPosition != 3) {
                            menuItem.setChecked(true);
                            currentFragment = new EarnedFragment();
                            makeFragmentVisible(currentFragment, EarnedFragment.TAG);
                            mCurrentSelectedPosition = 3;
                            setActionbarTitle(EarnedFragment.TAG);
                        }
                        return true;

                    case R.id.itemRedeem:
                        if (mCurrentSelectedPosition != 4) {
                            menuItem.setChecked(true);
                            currentFragment = new RedeemFragment();
                            makeFragmentVisible(currentFragment, RedeemFragment.TAG);
                            mCurrentSelectedPosition = 4;
                            setActionbarTitle(RedeemFragment.TAG);
                        }
                        return true;

                    case R.id.itemBankWithdraw:
                        if (mCurrentSelectedPosition != 5) {
                            menuItem.setChecked(true);
                            currentFragment = new WithdrawFragment();
                            makeFragmentVisible(currentFragment, WithdrawFragment.TAG);
                            mCurrentSelectedPosition = 5;
                            setActionbarTitle(WithdrawFragment.TAG);
                        }
                        return true;

                    case R.id.itemWhatNew:
                        if (mCurrentSelectedPosition != 6) {
                            menuItem.setChecked(true);
                            currentFragment = new WhatNewFragment();
                            makeFragmentVisible(currentFragment, WhatNewFragment.TAG);
                            mCurrentSelectedPosition = 6;
                            setActionbarTitle(WhatNewFragment.TAG);
                        }
                        return true;

                    case R.id.itemHelp:
                        if (mCurrentSelectedPosition != 7) {
                            menuItem.setChecked(true);
                            currentFragment = new HelpFragment();
                            makeFragmentVisible(currentFragment, HelpFragment.TAG);
                            mCurrentSelectedPosition = 7;
                            setActionbarTitle(HelpFragment.TAG);
                        }
                        return true;

                    case R.id.itemLogout:
                        menuItem.setChecked(true);
                        UserDataPreferences.logout(MainActivity.this);
                        Intent i = new Intent(MainActivity.this,
                                LogInActivity.class);
                        startActivity(i);
                        finish();
                        return true;

                    case R.id.itemFunnyImages:
                        if (mCurrentSelectedPosition != 8) {
                            menuItem.setChecked(true);
                            currentFragment = new ImageFragment();
                            makeFragmentVisible(currentFragment, ImageFragment.TAG);
                            mCurrentSelectedPosition = 8;
                            setActionbarTitle(ImageFragment.TAG);
                        }
                        return true;
                    case R.id.itemChangePassword:
                        if (mCurrentSelectedPosition != 9) {
                            menuItem.setChecked(true);
                            currentFragment = new ChangePasswordFragment();
                            makeFragmentVisible(currentFragment, ChangePasswordFragment.TAG);
                            mCurrentSelectedPosition = 9;
                            setActionbarTitle(ChangePasswordFragment.TAG);
                        }
                        return true;
                    default:
                        menuItem.setChecked(true);
                        currentFragment = new OnlyAdsFragment();
                        makeFragmentVisible(currentFragment, OnlyAdsFragment.TAG);
                        mCurrentSelectedPosition = 7;
                        setActionbarTitle(OnlyAdsFragment.TAG);
                        return true;
                }
            }
        });

        if (savedInstanceState == null) {
            mNavigationView.getMenu().getItem(1).setChecked(true);
            currentFragment = new OffreFragment();
            makeFragmentVisible(currentFragment, OffreFragment.TAG);
            mCurrentSelectedPosition = 1;
            setActionbarTitle(OffreFragment.TAG);
        }

        task = new GetUserBalance().execute();
    }

    public void refreshUserDetails() {
        txtUserName.setText(UserDataPreferences.getUserName(this));
        txtDrawerEmail.setText(UserDataPreferences.getEmailId(this));
    }

    @Override
    public void onBackPressed() {
        txtUserBalance.setText(UserDataPreferences.getUserBalance(MainActivity.this));
        if (task != null) {
            task.cancel(true);
            task = new GetUserBalance().execute();
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        } else {
            if (mCurrentSelectedPosition == 1) {
                AppConstant.closeAppPopup(MainActivity.this);
            } else {
                mNavigationView.getMenu().getItem(1).setChecked(true);
                currentFragment = new OffreFragment();
                makeFragmentVisible(currentFragment, OffreFragment.TAG);
                mCurrentSelectedPosition = 1;
                setActionbarTitle(OffreFragment.TAG);
            }
        }
    }

    private void makeFragmentVisible(Fragment fragment, String strName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.nav_contentframe, fragment).addToBackStack(strName).commit();
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            mUserLearnedDrawer = true;
            saveSharedSetting(this, PREF_USER_LEARNED_DRAWER, "true");
        }
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        txtTitle = (TextView) mToolbar.findViewById(R.id.txtTitle);
        txtUserBalance = (TextView) mToolbar.findViewById(R.id.txtUserBalance);
        txtUserBalance.setText(UserDataPreferences.getUserBalance(this));
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            setActionbarTitle("Dashboard");
        }
    }

    private void setActionbarTitle(String string) {
        getSupportActionBar().setTitle(AppConstant.spanFont(string, this));
        if(txtTitle != null){
            txtTitle.setText(string);
        }
    }


    @Override
    public void onClick(View v) {


    }


    public class GetUserBalance extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message, todaysbalance;
        private JSONArray bannerArray, fullscreenArray, videoArray;
        private JSONObject news;


        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            JSONStringer jsonStringer;
            try {

                jsonStringer = new JSONStringer().object().key("unique_id").value(UserDataPreferences.getUniqueId(MainActivity.this))
                        .key("version").value(getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0).versionCode).endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_user_balance, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    message = item.has("data") ? (item.isNull("data") ? null : item.getString("data")) : null;
                    bannerArray = item.has("banner") ? (item.isNull("banner") ? null : item.getJSONArray("banner")) : null;
                    fullscreenArray = item.has("fullscreen") ? (item.isNull("fullscreen") ? null : item.getJSONArray("fullscreen")) : null;
                    videoArray = item.has("video") ? (item.isNull("video") ? null : item.getJSONArray("video")) : null;
                    news = item.has("news") ? (item.isNull("news") ? null : item.getJSONObject("news")) : null;
                    todaysbalance = item.has("todays_balance") ? (item.isNull("todays_balance") ? "0" : item.getString("todays_balance")) : "0";
                    UserDataPreferences.saveAdsId(MainActivity.this, bannerArray, fullscreenArray, videoArray);
                    UserDataPreferences.saveNews(MainActivity.this, news);
                    UserDataPreferences.saveTodaysBalance(MainActivity.this, todaysbalance);
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
                    if (message != null && txtUserBalance != null) {
                        UserDataPreferences.saveUserBalance(MainActivity.this, message);
                        txtUserBalance.setText(Constants.rupee_symbol + message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void closeNavigationDrawer() {
        if (mUserLearnedDrawer) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setNavigationViewItemFont() {
        Menu menu = mNavigationView.getMenu();
        MenuItem menuItem;
        for (int i = 0; i < menu.size(); i++) {
            menuItem = menu.getItem(i);
            menuItem.setTitle(AppConstant.spanFont(menuItem.getTitle().toString(), this));
        }
    }
}
