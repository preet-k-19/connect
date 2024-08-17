package com.example.chat_application.adapter;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_application.R;
import com.example.chat_application.model.ChatMsgModel;
import com.example.chat_application.util.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMsgModel,ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;


    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMsgModel> options, List<Message> messages) {
        super(options);

    }

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMsgModel> options, Context context) {
        super(options);
        this.context=context;
    }


    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMsgModel model) {
        Log.i("haushd","asdj");

      if(model.getSenderId().equals(FireBaseUtil.currentUserId()))
      {
          holder.leftChatLayout.setVisibility(View.GONE);
          holder.rightChatLayout.setVisibility(View.VISIBLE);
          holder.rightChatText.setText(model.getMessage());

      }
      else
      {
          holder.leftChatLayout.setVisibility(View.VISIBLE);
          holder.rightChatLayout.setVisibility(View.GONE);
          holder.leftChatText.setText(model.getMessage());

      }

    }

    @NonNull
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.chat_msg_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatText,rightChatText;


        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
        
            leftChatLayout=itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout=itemView.findViewById(R.id.right_chat_layout);
            leftChatText=itemView.findViewById(R.id.left_chat_txt);
            rightChatText=itemView.findViewById(R.id.right_chat_txt);
        }
    }

    private void showDeleteDialog() {     // String messageId

    }

    private void deleteMessage(String messageId) {


     FirebaseFirestore messageRef = FirebaseFirestore.getInstance();
     messageRef.collection("message").document(messageId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show());
    }
}