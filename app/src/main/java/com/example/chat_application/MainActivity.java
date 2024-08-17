package com.example.chat_application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_application.util.FireBaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
BottomNavigationView bottomNavigationView;
ImageButton searchBtn;
chat_fragment  chatFragment;
profile_Fragment profileFragment;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_nav_bar);
        searchBtn=findViewById(R.id.search_btn);

        chatFragment = new chat_fragment();
        profileFragment =new profile_Fragment();

        searchBtn.setOnClickListener((v)->{
            startActivity(new Intent(MainActivity.this, Search_user_activity.class));
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout,chatFragment).commit();
                }
                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout,profileFragment).commit();
                }
                return true;
            }
        });
            bottomNavigationView.setSelectedItemId(R.id.menu_chat);
            getFcmToken();
    }

    void getFcmToken()
    {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                FireBaseUtil.currentUserDetails().update("fcmToken",token);
            }
        });
    }
}