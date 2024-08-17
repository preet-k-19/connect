package com.example.chat_application.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_application.ChatAcitivity;
import com.example.chat_application.R;
import com.example.chat_application.model.ChatRoomModel;
import com.example.chat_application.model.UserModel;
import com.example.chat_application.util.AndroidUtil;
import com.example.chat_application.util.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel,RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;


    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options,Context context) {
        super(options);
        this.context= context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FireBaseUtil.getOtherUserFromChatRoomModel(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        boolean lastMsgSentByMe=model.getLastMsgSenderId().equals(FireBaseUtil.currentUserId());

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        FireBaseUtil.getOtherProfilePic(otherUserModel.getUserId()).getDownloadUrl()
                            .addOnCompleteListener(t -> {
                                if(t.isSuccessful())
                                {
                                    Uri uri= t.getResult();
                                    AndroidUtil.setProfilePic(context,uri,holder.profilePic);
                                }
                            });

                        holder.userNameText.setText(otherUserModel.getUserName());
                        if(lastMsgSentByMe){
                            holder.last_msg_txt.setText("You: "+model.getLastMsg());
                        }

                        else
                        {
                            holder.last_msg_txt.setText(model.getLastMsg());
                        }
                        holder.last_msg_time_txt.setText(FireBaseUtil.timestamp(model.getLastMSgTime()));


                        holder.itemView.setOnClickListener(v->{
                            //navigating to the chat screen
                            Intent intent=new Intent(context, ChatAcitivity.class);
                            AndroidUtil.passUserModelAsIntent(intent,otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        });

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                builder.setMessage("Are you Sure want to delete this chat?");
                                builder.setTitle("Alert!");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Yess",(DialogInterface.OnClickListener)(dialog, which)->{
                                  dialog.cancel();
                                });
                                builder.setNegativeButton("No",(DialogInterface.OnClickListener)(dialog, which)->{
                                    dialog.cancel();
                                });
                                AlertDialog alertDialog= builder.create();
                                alertDialog.show();
                                return false;
                            }
                        });


                    }
                });
    }


    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatRoomModelViewHolder(view);
    }


    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder{

        TextView userNameText;
        TextView last_msg_txt;
        TextView last_msg_time_txt;
        ImageView profilePic;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText=itemView.findViewById(R.id.user_name_text);
            last_msg_txt=itemView.findViewById(R.id.last_msg_text);
            last_msg_time_txt=itemView.findViewById(R.id.last_msg_time_text);
            profilePic=itemView.findViewById(R.id.profile_pic_image_view);


        }
    }

}
