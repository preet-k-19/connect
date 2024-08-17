package com.example.chat_application.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoomModel {

    String chatRoomId;
    List<String> userIds;
    Timestamp lastMSgTime;
    String LastMsgSenderId;
    String LastMsg;

    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatRoomId, List<String> userIds, Timestamp lastMSgTime, String lastMsgSenderId) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMSgTime = lastMSgTime;
        LastMsgSenderId = lastMsgSenderId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserId(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMSgTime() {
        return lastMSgTime;
    }

    public void setLastMSgTime(Timestamp lastMSgTime) {
        this.lastMSgTime = lastMSgTime;
    }

    public String getLastMsgSenderId() {
        return LastMsgSenderId;
    }

    public void setLastMsgSenderId(String lastMsgSenderId) {
        LastMsgSenderId = lastMsgSenderId;
    }

    public String getLastMsg() {
        return LastMsg;
    }

    public void setLastMsg(String lastMsg) {
        LastMsg = lastMsg;
    }
}
