package com.foresight.clickonmoney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.foresight.clickonmoney.Util.UserDataPreferences;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent i;
        if(UserDataPreferences.isLogin(this)){
            i = new Intent(SplashActivity.this,
                    MainActivity.class);
        }else{
            i = new Intent(SplashActivity.this,
                    LogInActivity.class);
        }
        startActivity(i);
        finish();

    }
}
