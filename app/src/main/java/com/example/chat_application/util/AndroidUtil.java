package com.example.chat_application.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chat_application.model.UserModel;

public class AndroidUtil {

    public static void showToast(Context context,String msg)
    {
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }



    public static  UserModel userModelGetFromIntent(Intent intent)
    {
        UserModel userModel=new UserModel();
        userModel.setUserName(intent.getStringExtra("userName"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

    public static  void passUserModelAsIntent(Intent intent, UserModel model)
    {
        intent.putExtra("userName",model.getUserName());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("fcmToken",model.getFcmToken());
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
