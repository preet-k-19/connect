package com.example.chat_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_application.model.UserModel;
import com.example.chat_application.util.FireBaseUtil;
import com.google.firebase.Timestamp;

public class LoginUserNameActivity extends AppCompatActivity {
EditText userNameInput;
Button letMeInBtn;
ProgressBar progressBar;
String phoneNumber;
UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_name);

        userNameInput=findViewById(R.id.login_username);
        letMeInBtn=findViewById(R.id.login_lets_start_btn);
        progressBar=findViewById(R.id.login_3_progressbar);

        phoneNumber=getIntent().getExtras().getString("phone");
        getUsername();

        letMeInBtn.setOnClickListener((v)->setUserName());
    }


    void setUserName(){
        String userName=userNameInput.getText().toString();
        if(userName.isEmpty() || userName.length()<3){
            userNameInput.setError("UserName Should be at least 3 character ");
            return;
        }
        inProgress(true);
        if(userModel!=null)
        {
            userModel.setUserName(userName);
        }
        else{
            //if user is new user to the system
            userModel = new UserModel(phoneNumber,userName, Timestamp.now(),FireBaseUtil.currentUserId());
        }
        FireBaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            inProgress(false);
            if(task.isSuccessful()){
                Intent intent=new Intent(LoginUserNameActivity.this, MainActivity.class);

                //to clear everything about login open main activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        });

    }

    void getUsername(){
        inProgress(true);
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            inProgress(false);
            if(task.isSuccessful()){
                userModel = task.getResult().toObject(UserModel.class);
                if(userModel!=null){
                    userNameInput.setText(userModel.getUserName());
                }
            }
        });
    }

    void inProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }

    }
}