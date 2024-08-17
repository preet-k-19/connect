package com.example.chat_application.model;

import com.google.firebase.Timestamp;

import io.reactivex.rxjava3.annotations.NonNull;

public class UserModel {
    private  String phone;
    private  String userName;
    private Timestamp createdTimeStamp;
    private String userId;
    private String FcmToken;

    public UserModel() {
    }

    @NonNull
    public UserModel(@NonNull  String phone, String userName, Timestamp createdTimeStamp,@NonNull String userId) {
        this.phone = phone;
        this.userName = userName;
        this.createdTimeStamp = createdTimeStamp;
        this.userId=userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    @NonNull
    public String getUserId() {

        return userId;
    }

    @NonNull
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return FcmToken;
    }

    public void setFcmToken(String fcmToken) {
        FcmToken = fcmToken;
    }
}

