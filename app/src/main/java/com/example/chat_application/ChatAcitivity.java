package com.example.chat_application;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_application.adapter.ChatRecyclerAdapter;
import com.example.chat_application.model.ChatMsgModel;
import com.example.chat_application.model.ChatRoomModel;
import com.example.chat_application.model.UserModel;
import com.example.chat_application.util.AndroidUtil;
import com.example.chat_application.util.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatAcitivity extends AppCompatActivity {
    UserModel otherUser;
    EditText msgInput;
    ImageButton send_btn,backBtn;
    TextView otherUserName;
    RecyclerView recyclerView;
    String chatRoomId;
    ChatRoomModel chatRoomModel;
    ChatRecyclerAdapter adapter;
    ImageView imageView;
    Uri link;



    //  @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acitivity);



        otherUser= AndroidUtil.userModelGetFromIntent(getIntent());

        chatRoomId= FireBaseUtil.getChatRoomID(FireBaseUtil.currentUserId(),otherUser.getUserId());




       // chatRoomId= FireBaseUtil.getChatRoomID(FireBaseUtil.currentUserId(),otherUser.getUserId());
        //get user details
        msgInput=findViewById(R.id.chat_msg_input);
        send_btn=findViewById(R.id.msg_send_btn);
        backBtn=findViewById(R.id.back_btn1);
        otherUserName=findViewById(R.id.other_userName);
        recyclerView=findViewById(R.id.chat_act_recyclerview);
        imageView=findViewById(R.id.profile_pic_image_view);


        FireBaseUtil.getOtherProfilePic(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);
                    }
                });



        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });




        otherUserName.setText(otherUser.getUserName());

        send_btn.setOnClickListener((v-> {
            String message = msgInput.getText().toString().trim();
            if (message.isEmpty())

                 return;

            sendMsgToUser(message);
        }));


       getOrCreateChatRoomModel();
        setUpChatRecyclerView();

    }

    void setUpChatRecyclerView(){
        Query query = FireBaseUtil.getChatRoomMsgReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMsgModel> options = new FirestoreRecyclerOptions.Builder<ChatMsgModel>()
                .setQuery(query, ChatMsgModel.class).build();

        adapter=new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart,itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMsgToUser(String message)
    {
        chatRoomModel.setLastMSgTime(Timestamp.now());
        chatRoomModel.setLastMsgSenderId(FireBaseUtil.currentUserId());
        chatRoomModel.setLastMsg(message);
        FireBaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);

        ChatMsgModel chatMsgModel=new ChatMsgModel(message,FireBaseUtil.currentUserId(),Timestamp.now());

        FireBaseUtil.getChatRoomMsgReference(chatRoomId).add(chatMsgModel)
                .addOnCompleteListener(
                        task -> {
                            if(task.isSuccessful())
                            {
                                msgInput.setText("");
                                sendNotification(message);
                            }
                        });

    }

    void getOrCreateChatRoomModel()
    {
        FireBaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task->{
            if(task.isSuccessful())
            {
                chatRoomModel=task.getResult().toObject(ChatRoomModel.class);
                if(chatRoomModel==null)
                {
                    //if there is no chat and it's first time...
                    chatRoomModel=new ChatRoomModel(
                            chatRoomId,
                            Arrays.asList(FireBaseUtil.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FireBaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);
                }
            }
        });
    }

    void sendNotification(String message)
    {
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               UserModel currentUSer = task.getResult().toObject(UserModel.class);
               try
               {
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("title",currentUSer.getUserName());
                    notificationObject.put("body",message);

                    JSONObject dataObject = new JSONObject();
                    dataObject.put("userId", currentUSer.getUserId());
                    jsonObject.put("notification", notificationObject);
                    jsonObject.put("data",dataObject);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);
               }
               catch (Exception e)
               {

               }
           }
        });
    }

    void callApi(JSONObject jsonObject)
    {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body=RequestBody.create(jsonObject.toString(),JSON);
        Request request=new Request.Builder().
                url(url)
                .post(body)
                .header("Authorization","Bearer AAAABynaHsU:APA91bHqs9LZUX6-RcqYs_2xG1BRW_04yp9yiJk5yMx3pjFaqk5VieO28DRhMGw4huyUrHm9qFucjTBlMzyB6eu2aTVErXT9HQ9welkGzZqXuQwcMS_ZgisWziWFoDRvIa2pCM0_iwap")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}