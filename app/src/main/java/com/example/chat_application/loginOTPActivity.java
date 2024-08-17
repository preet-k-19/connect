package com.example.chat_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat_application.util.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class loginOTPActivity extends AppCompatActivity {
String phoneNumber;
Long timeoutSeconds = 60L;
String verificationCode;
PhoneAuthProvider.ForceResendingToken resendingToken;
EditText otpInput;
Button nextOtp;
ProgressBar progressBar;
TextView resendTxt;
FirebaseAuth mAuth =FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otpactivity);

        otpInput=findViewById(R.id.login_otp_edt);
        nextOtp=findViewById(R.id.login_next_btn);
        progressBar=findViewById(R.id.login_2_progressbar);
        resendTxt=findViewById(R.id.resend_otp_txt);

        phoneNumber=getIntent().getExtras().getString("phone");
        Toast.makeText(getApplicationContext(),phoneNumber,Toast.LENGTH_SHORT);

        sendOTP(phoneNumber,false);

        nextOtp.setOnClickListener((v)-> {
            String enteredOtp=otpInput.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
            signIn(credential);
            inProgress(true);
        });

        resendTxt.setOnClickListener((v)-> {
            sendOTP(phoneNumber,true);
        });

    }

    void sendOTP(String phoneNUmber, boolean isResend){
        startResendOTP();
        inProgress(true);
        PhoneAuthOptions .Builder builder=PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        inProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showToast(getApplicationContext(),"OTP is Incorrect Or Verification Failed");
                        inProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode=s;
                        resendingToken=forceResendingToken;
                        AndroidUtil.showToast(getApplicationContext(),"OTP Send Successfully");
                        inProgress(false);
                    }
                });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }
        else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void inProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            nextOtp.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            nextOtp.setVisibility(View.VISIBLE);
        }

    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        //Login and going to next Activity
         inProgress(true);
         mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                inProgress(false);
                 if(task.isSuccessful()){
                    Intent intent=new Intent(loginOTPActivity.this,LoginUserNameActivity.class);
                    intent.putExtra("phone",phoneNumber);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }
                else
                {
                    AndroidUtil.showToast(getApplicationContext(),"OTP Incorrect Or Verification failed");
                }
             }
         });
    }

    void startResendOTP()
    {
        resendTxt.setEnabled(false);
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resendTxt.setText("Resend OTP in "+timeoutSeconds+"seconds");
                if(timeoutSeconds<=0)
                {
                    timeoutSeconds=60L;
                    timer.cancel();
                    runOnUiThread(()->{
                        resendTxt.setEnabled(true);
                        resendTxt.setText("Resend OTP");
                    });
                }
            }
        },0,1000);
    }
}