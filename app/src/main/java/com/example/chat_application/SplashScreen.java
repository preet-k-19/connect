package com.example.chat_application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_application.util.FireBaseUtil;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(FireBaseUtil.isLoggedIn())
                    {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    }
                    else{
                        startActivity(new Intent(SplashScreen.this,LoginPhone.class));
                    }
                    finish();
                }
            },1500);
        }


}