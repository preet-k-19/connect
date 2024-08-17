package com.example.chat_application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginPhone extends AppCompatActivity {
EditText phoneInput;
CountryCodePicker countryCodePicker;
Button sendOtpBtn;
ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        phoneInput=findViewById(R.id.login_mobile_number);
        countryCodePicker=findViewById(R.id.login_country_code);
        sendOtpBtn=findViewById(R.id.send_otp_btn);
        progressBar=findViewById(R.id.login_1_progressbar);

        progressBar.setVisibility(View.GONE);
        countryCodePicker.registerCarrierNumberEditText(phoneInput);

        sendOtpBtn.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Invalid PhoneNumber");
                return;
            }
            Intent intent=new Intent(LoginPhone.this, loginOTPActivity.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        });
    }
}