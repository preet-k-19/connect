package com.example.chat_application.util;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

public class FireBaseUtil {
    @NonNull
    public static String currentUserId(){

        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("user").document(currentUserId());
    }
    public static boolean isLoggedIn()
    {
        if(currentUserId()!=null)
        {
            return true;
        }
        return false;
    }


    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("user");
    }


    public static DocumentReference getChatRoomReference(String chatRoomId)
    {
        return FirebaseFirestore.getInstance().collection("chatRooms").document(chatRoomId);
    }


    public static CollectionReference getChatRoomMsgReference( String chatroomId)
    {
        return  getChatRoomReference(chatroomId).collection("chats");
    }

    public static String getChatRoomID(@NonNull String userId1,@NonNull String userId2){
        if(userId1.hashCode()<userId2.hashCode())
        {
            return userId1+"_"+userId2;
        }
        else{
            return userId2+"_"+userId1;
        }
    }

    public static  CollectionReference allChatRoomCollection(){
        return FirebaseFirestore.getInstance().collection("chatRooms");
    }

    public static DocumentReference getOtherUserFromChatRoomModel(List<String>userIds){
        if(userIds.get(0).equals(FireBaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }
        else
        {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static  String timestamp(Timestamp timestamp){
       return new SimpleDateFormat("dd/MM\n" + "HH:mm").format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePic()
    {
        return FirebaseStorage.getInstance().getReference().child("profile_pics")
                .child(FireBaseUtil.currentUserId());
    }

    public static StorageReference getOtherProfilePic(String otherUSerId)
    {
        return FirebaseStorage.getInstance().getReference().child("profile_pics")
                .child(otherUSerId);
    }



}
